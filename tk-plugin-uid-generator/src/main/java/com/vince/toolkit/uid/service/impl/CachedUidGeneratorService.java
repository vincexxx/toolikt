package com.vince.toolkit.uid.service.impl;

import com.vince.toolkit.base.exception.BusinessException;
import com.vince.toolkit.framework.util.log.LogUtil;
import com.vince.toolkit.uid.BitsAllocator;
import com.vince.toolkit.uid.buffer.BufferPaddingExecutor;
import com.vince.toolkit.uid.buffer.RejectedPutBufferHandler;
import com.vince.toolkit.uid.buffer.RejectedTakeBufferHandler;
import com.vince.toolkit.uid.buffer.RingBuffer;
import com.vince.toolkit.uid.service.UidGeneratorService;
import com.vince.toolkit.uid.util.DateUtils;
import com.vince.toolkit.uid.worker.WorkerIdAssigner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class CachedUidGeneratorService implements UidGeneratorService, InitializingBean, DisposableBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(CachedUidGeneratorService.class);
    //缓冲id扩容，原bufferSize << boostPower
    private int boostPower = RingBuffer.DEFAULT_BOOST_POWER;
    //当RingBuffer中的id剩余比例小于paddingFactor％百分比时，填充RingBuffer
    private int paddingFactor = RingBuffer.DEFAULT_PADDING_PERCENT;

    //拒绝策略: 当环已满, 无法继续填充时
    private RejectedPutBufferHandler rejectedPutBufferHandler;
    //拒绝策略: 当环已空, 无法继续获取时
    private RejectedTakeBufferHandler rejectedTakeBufferHandler;

    private RingBuffer ringBuffer;
    private BufferPaddingExecutor bufferPaddingExecutor;

    //开始时间截 (2018-01-01) (ms: 1514736000000)
    protected long epochMilliseconds = TimeUnit.MILLISECONDS.toMillis(1514736000000L);

    protected BitsAllocator bitsAllocator;
    protected long workerId;

    //workId生成策略
    @Autowired
    protected WorkerIdAssigner workerIdAssigner;

    @Override
    public void afterPropertiesSet() throws Exception {
        // 初始化workid和各长度位
        bitsAllocator = new BitsAllocator(BitsAllocator.TIME_BITS, BitsAllocator.WORKER_BITS, BitsAllocator.SEQ_BITS);
        //初始化workId，可以自定义实现assignWorkerId方法，可以用db实现，或者redis，zk实现都可以，生成唯一id
        workerId = workerIdAssigner.assignWorkerId();
        if (workerId > bitsAllocator.getMaxWorkerId()) {
            throw new BusinessException("WorkerId[%d]超越最大限制[%d]", workerId, bitsAllocator.getMaxWorkerId());
        }
        LogUtil.info(LOGGER, "workerID:{},初始化空间分配(1, {}, {}, {})", workerId, BitsAllocator.TIME_BITS, BitsAllocator.WORKER_BITS, BitsAllocator.SEQ_BITS);
        //初始化RingBuffer和RingBufferPaddingExecutor
        this.initRingBuffer();
        LogUtil.info(LOGGER, "初始化RingBuffer成功.");
    }

    @Override
    public long getUID() {
        try {
            return ringBuffer.take();
        } catch (Exception e) {
            LogUtil.error(LOGGER, "生成uid异常", e);
            throw new BusinessException(e);
        }
    }

    @Override
    public String parseUID(long uid) {
        long totalBits = BitsAllocator.TOTAL_BITS;
        long signBits = bitsAllocator.getSignBits();
        long timestampBits = bitsAllocator.getTimestampBits();
        long workerIdBits = bitsAllocator.getWorkerIdBits();
        long sequenceBits = bitsAllocator.getSequenceBits();
        //解析uid
        long sequence = (uid << (totalBits - sequenceBits)) >>> (totalBits - sequenceBits);
        long workerId = (uid << (timestampBits + signBits)) >>> (totalBits - workerIdBits);
        long deltaMilliseconds = uid >>> (workerIdBits + sequenceBits);
        Date thatTime = new Date(TimeUnit.MILLISECONDS.toMillis(epochMilliseconds + deltaMilliseconds));
        String thatTimeStr = DateUtils.formatByDateTimePattern(thatTime);
        return String.format("{\"UID\":\"%d\",\"timestamp\":\"%s\",\"workerId\":\"%d\",\"sequence\":\"%d\"}",
                uid, thatTimeStr, workerId, sequence);
    }

    @Override
    public void destroy() throws Exception {
        bufferPaddingExecutor.shutdown();
    }

    /**
     * 初始化当前秒的所有序列
     * @param currentMillisecond
     * @return
     */
    protected List<Long> nextIdsForOneMillisecond(long currentMillisecond) {
        int listSize = (int) bitsAllocator.getMaxSequence() + 1;
        List<Long> uidList = new ArrayList<>(listSize);
        long firstSeqUid = bitsAllocator.allocate(currentMillisecond - epochMilliseconds, workerId, 0L);
        for (int offset = 0; offset < listSize; offset++) {
            uidList.add(firstSeqUid + offset);
        }
        return uidList;
    }

    private void initRingBuffer() {
        //初始化RingBuffer
        int bufferSize = ((int) bitsAllocator.getMaxSequence() + 1) << boostPower;
        this.ringBuffer = new RingBuffer(bufferSize, paddingFactor);
        LogUtil.info(LOGGER, "初始化RingBuffer长度:{}, 扩容触发百分比:{}", bufferSize, paddingFactor);

        //初始化RingBufferPaddingExecutor
        this.bufferPaddingExecutor = new BufferPaddingExecutor(ringBuffer, this::nextIdsForOneMillisecond);
        this.ringBuffer.setBufferPaddingExecutor(bufferPaddingExecutor);

        if (rejectedPutBufferHandler != null) {
            this.ringBuffer.setRejectedPutHandler(rejectedPutBufferHandler);
        }
        if (rejectedTakeBufferHandler != null) {
            this.ringBuffer.setRejectedTakeHandler(rejectedTakeBufferHandler);
        }

        //填充slots
        bufferPaddingExecutor.paddingBuffer();
    }

}
