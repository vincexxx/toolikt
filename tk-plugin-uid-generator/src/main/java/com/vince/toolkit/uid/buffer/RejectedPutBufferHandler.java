package com.vince.toolkit.uid.buffer;

@FunctionalInterface
public interface RejectedPutBufferHandler {

    /**
     * 拒绝填充请求
     *
     * @param ringBuffer
     * @param uid
     */
    void rejectPutBuffer(RingBuffer ringBuffer, long uid);
}
