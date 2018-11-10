package com.vince.toolkit.uid.buffer;

@FunctionalInterface
public interface RejectedTakeBufferHandler {

    /**
     * 拒绝获取请求
     *
     * @param ringBuffer
     */
    void rejectTakeBuffer(RingBuffer ringBuffer);
}
