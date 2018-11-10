package com.vince.toolkit.uid.buffer;

import java.util.concurrent.atomic.AtomicLong;

import com.vince.toolkit.base.exception.BusinessException;
import com.vince.toolkit.framework.util.log.LogUtil;
import com.vince.toolkit.uid.util.PaddedAtomicLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

public class RingBuffer {
    private static final Logger LOGGER = LoggerFactory.getLogger(RingBuffer.class);

    /**
     * Constants
     */
    private static final int START_POINT = -1;
    private static final long CAN_PUT_FLAG = 0L;
    private static final long CAN_TAKE_FLAG = 1L;
    //默认扩容
    public static final int DEFAULT_BOOST_POWER = 5;
    //填充缓冲区默认触发比例
    public static final int DEFAULT_PADDING_PERCENT = 50;

    private final int bufferSize;
    private final long indexMask;
    private final long[] slots;
    private final PaddedAtomicLong[] flags;

    /**
     * Tail: last position sequence to produce
     */
    private final AtomicLong tail = new PaddedAtomicLong(START_POINT);

    /**
     * Cursor: current position sequence to consume
     */
    private final AtomicLong cursor = new PaddedAtomicLong(START_POINT);

    /**
     * Threshold for trigger padding buffer
     */
    private final int paddingThreshold;

    /**
     * 拒绝策略
     */
    private RejectedPutBufferHandler rejectedPutHandler = this::discardPutBuffer;
    private RejectedTakeBufferHandler rejectedTakeHandler = this::exceptionRejectedTakeBuffer;

    /**
     * 填充线程
     */
    private BufferPaddingExecutor bufferPaddingExecutor;


    public RingBuffer(int bufferSize) {
        this(bufferSize, DEFAULT_PADDING_PERCENT);
    }

    /**
     * 初始化RingBuffer
     *
     * @param bufferSize
     * @param paddingFactor
     */
    public RingBuffer(int bufferSize, int paddingFactor) {
        if (bufferSize <= 0) {
            throw new BusinessException("RingBuffer bufferSize必须为正整数");
        }
        if (Integer.bitCount(bufferSize) != 1) {
            throw new BusinessException("RingBuffer bufferSize必须为2的次方");
        }
        if (paddingFactor <= 0 || paddingFactor >= 100) {
            throw new BusinessException("RingBuffer paddingFactor百分比必须为(0~100)的正整数");
        }
        this.bufferSize = bufferSize;
        this.indexMask = bufferSize - 1;
        this.slots = new long[bufferSize];
        this.flags = initFlags(bufferSize);
        this.paddingThreshold = bufferSize * paddingFactor / 100;
    }

    /**
     * Put an UID in the ring & tail moved<br>
     * We use 'synchronized' to guarantee the UID fill in slot & publish new tail sequence as atomic operations<br>
     * <p>
     * <b>Note that: </b> It is recommended to put UID in a serialize way, cause we once batch generate a series UIDs and put
     * the one by one into the buffer, so it is unnecessary put in multi-threads
     *
     * @param uid
     * @return false means that the buffer is full, apply {@link RejectedPutBufferHandler}
     */
    public synchronized boolean put(long uid) {
        long currentTail = tail.get();
        long currentCursor = cursor.get();

        // tail catches the cursor, means that you can't put any cause of RingBuffer is full
        long distance = currentTail - (currentCursor == START_POINT ? 0 : currentCursor);
        if (distance == bufferSize - 1) {
            rejectedPutHandler.rejectPutBuffer(this, uid);
            return false;
        }

        // 1. pre-check whether the flag is CAN_PUT_FLAG
        int nextTailIndex = calSlotIndex(currentTail + 1);
        if (flags[nextTailIndex].get() != CAN_PUT_FLAG) {
            rejectedPutHandler.rejectPutBuffer(this, uid);
            return false;
        }

        // 2. put UID in the next slot
        // 3. update next slot' flag to CAN_TAKE_FLAG
        // 4. publish tail with sequence increase by one
        slots[nextTailIndex] = uid;
        flags[nextTailIndex].set(CAN_TAKE_FLAG);
        tail.incrementAndGet();

        // The atomicity of operations above, guarantees by 'synchronized'. In another word,
        // the take operation can't consume the UID we just put, until the tail is published(tail.incrementAndGet())
        return true;
    }

    /**
     * 获取uid
     * 获取的同时校验缓冲区剩余是否达到补充RingBuffer条件，达到条件则触发另一个线程去填充RingBuffer的slots
     * 如果没有可用的uid，则执行拒绝策略，可自定义实现
     *
     * @return
     */
    public long take() {
        // spin get next available cursor
        long currentCursor = cursor.get();
        long nextCursor = cursor.updateAndGet(old -> old == tail.get() ? old : old + 1);

        if (nextCursor < currentCursor) {
            //正常不应该出现该异常
            throw new BusinessException("下标不能回退");
        }
        //判断是否需要触发补充slots
        long currentTail = tail.get();
        if (currentTail - nextCursor < paddingThreshold) {
            LogUtil.debug(LOGGER, "达到触发条件，补充RingBuffer:{}. tail:{}, cursor:{}, rest:{}", paddingThreshold, currentTail,
                    nextCursor, currentTail - nextCursor);
            bufferPaddingExecutor.asyncPadding();
        }
        if (nextCursor == currentCursor) {
            rejectedTakeHandler.rejectTakeBuffer(this);
        }

        // 1. check next slot flag is CAN_TAKE_FLAG
        int nextCursorIndex = calSlotIndex(nextCursor);
        if (flags[nextCursorIndex].get() != CAN_TAKE_FLAG) {
            throw new BusinessException("不能获取到下标状态");
        }
        // 2. get UID from next slot
        // 3. set next slot flag as CAN_PUT_FLAG.
        long uid = slots[nextCursorIndex];
        flags[nextCursorIndex].set(CAN_PUT_FLAG);

        // Note that: Step 2,3 can not swap. If we set flag before get value of slot, the producer may overwrite the
        // slot with a new UID, and this may cause the consumer take the UID twice after walk a round the ring
        return uid;
    }

    /**
     * Calculate slot index with the slot sequence (sequence % bufferSize)
     */
    protected int calSlotIndex(long sequence) {
        return (int) (sequence & indexMask);
    }

    protected void discardPutBuffer(RingBuffer ringBuffer, long uid) {
        LogUtil.warn(LOGGER, "缓冲区已满,拒绝填充到Buffer UID:{}. {}", uid, ringBuffer);
    }

    protected void exceptionRejectedTakeBuffer(RingBuffer ringBuffer) {
        LogUtil.warn(LOGGER, "缓冲区不足，拒绝从缓冲区获取Buffer.{}", ringBuffer);
        throw new BusinessException("缓冲区不足，拒绝从缓冲区获取Buffer.%s", ringBuffer);
    }

    /**
     * Initialize flags as CAN_PUT_FLAG
     */
    private PaddedAtomicLong[] initFlags(int bufferSize) {
        PaddedAtomicLong[] flags = new PaddedAtomicLong[bufferSize];
        for (int i = 0; i < bufferSize; i++) {
            flags[i] = new PaddedAtomicLong(CAN_PUT_FLAG);
        }
        return flags;
    }

    /**
     * Setters
     */
    public void setBufferPaddingExecutor(BufferPaddingExecutor bufferPaddingExecutor) {
        this.bufferPaddingExecutor = bufferPaddingExecutor;
    }

    public void setRejectedPutHandler(RejectedPutBufferHandler rejectedPutHandler) {
        this.rejectedPutHandler = rejectedPutHandler;
    }

    public void setRejectedTakeHandler(RejectedTakeBufferHandler rejectedTakeHandler) {
        this.rejectedTakeHandler = rejectedTakeHandler;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("RingBuffer [bufferSize=").append(bufferSize)
                .append(", tail=").append(tail)
                .append(", cursor=").append(cursor)
                .append(", paddingThreshold=").append(paddingThreshold).append("]");

        return builder.toString();
    }

}
