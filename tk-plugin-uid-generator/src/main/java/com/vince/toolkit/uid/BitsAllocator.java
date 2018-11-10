package com.vince.toolkit.uid;

import com.vince.toolkit.base.exception.BusinessException;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class BitsAllocator {

    //总长度64位
    public static final int TOTAL_BITS = 1 << 6;
    //当前时间，相对于时间基点"@epochStr"的增量值，单位：毫秒,长度40位
    public static final int TIME_BITS = 40;
    //机器id系列为，默认12位，最多2048个服务同时
    public static final int WORKER_BITS = 11;
    //每秒下的并发序列，默认11位,可支持每毫秒4096个并发
    public static final int SEQ_BITS = 12;

    //保留1位
    private int signBits = 1;
    private final int timestampBits;
    private final int workerIdBits;
    private final int sequenceBits;

    private final long maxDeltaSeconds;
    private final long maxWorkerId;
    private final long maxSequence;

    private final int timestampShift;
    private final int workerIdShift;

    public BitsAllocator(int timestampBits, int workerIdBits, int sequenceBits) {
        // 校验长度是否够64位
        int allocateTotalBits = signBits + timestampBits + workerIdBits + sequenceBits;
        if (allocateTotalBits != TOTAL_BITS) {
            throw new BusinessException("分配长度总和不是64位.[signBits:%d,timestampBits:%d,workerIdBits:%d,sequenceBits:%d]", signBits, timestampBits, workerIdBits, sequenceBits);
        }
        // initialize bits
        this.timestampBits = timestampBits;
        this.workerIdBits = workerIdBits;
        this.sequenceBits = sequenceBits;

        // initialize max value
        this.maxDeltaSeconds = ~(-1L << timestampBits);
        this.maxWorkerId = ~(-1L << workerIdBits);
        this.maxSequence = ~(-1L << sequenceBits);

        // initialize shift
        this.timestampShift = workerIdBits + sequenceBits;
        this.workerIdShift = sequenceBits;
    }

    public long allocate(long deltaSeconds, long workerId, long sequence) {
        return (deltaSeconds << timestampShift) | (workerId << workerIdShift) | sequence;
    }

    public int getSignBits() {
        return signBits;
    }

    public int getTimestampBits() {
        return timestampBits;
    }

    public int getWorkerIdBits() {
        return workerIdBits;
    }

    public int getSequenceBits() {
        return sequenceBits;
    }

    public long getMaxDeltaSeconds() {
        return maxDeltaSeconds;
    }

    public long getMaxWorkerId() {
        return maxWorkerId;
    }

    public long getMaxSequence() {
        return maxSequence;
    }

    public int getTimestampShift() {
        return timestampShift;
    }

    public int getWorkerIdShift() {
        return workerIdShift;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}