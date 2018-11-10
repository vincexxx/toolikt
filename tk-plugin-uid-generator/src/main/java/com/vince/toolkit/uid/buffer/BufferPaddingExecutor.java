/*
 * Copyright (c) 2017 Baidu, Inc. All Rights Reserve.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.vince.toolkit.uid.buffer;

import com.vince.toolkit.framework.util.log.LogUtil;
import com.vince.toolkit.uid.util.NamingThreadFactory;
import com.vince.toolkit.uid.util.PaddedAtomicLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class BufferPaddingExecutor {
    private static final Logger LOGGER = LoggerFactory.getLogger(RingBuffer.class);
    private static final String WORKER_NAME = "RingBuffer-Padding-Worker";

    /**
     * 填充线程运行状态
     */
    private final AtomicBoolean running;

    /**
     * 记录当前时间，用于获取下一毫秒时间
     */
    private final PaddedAtomicLong lastMillisecond;

    /**
     * RingBuffer & BufferUidProvider
     */
    private final RingBuffer ringBuffer;
    private final BufferedUidProvider uidProvider;

    /**
     * Padding immediately by the thread pool
     */
    private final ExecutorService bufferPadExecutors;

    public BufferPaddingExecutor(RingBuffer ringBuffer, BufferedUidProvider uidProvider) {
        this.running = new AtomicBoolean(false);
        this.lastMillisecond = new PaddedAtomicLong(TimeUnit.MILLISECONDS.toMillis(System.currentTimeMillis()));
        this.ringBuffer = ringBuffer;
        this.uidProvider = uidProvider;

        // initialize thread pool
        int cores = Runtime.getRuntime().availableProcessors();
        bufferPadExecutors = Executors.newFixedThreadPool(cores * 2, new NamingThreadFactory(WORKER_NAME));
    }

    /**
     * Shutdown executors
     */
    public void shutdown() {
        if (!bufferPadExecutors.isShutdown()) {
            bufferPadExecutors.shutdownNow();
        }
    }

    public boolean isRunning() {
        return running.get();
    }

    public void asyncPadding() {
        bufferPadExecutors.submit(this::paddingBuffer);
    }

    /**
     * 当RingBuffer不满时
     */
    public void paddingBuffer() {
        LogUtil.debug(LOGGER, "开始补充到RingBuffer lastSecond:{}. {}", lastMillisecond.get(), ringBuffer);
        //填充线程还在运行时，不继续运行
        if (!running.compareAndSet(false, true)) {
            LogUtil.debug(LOGGER, "上个补充线程还在执行中,中止当次执行. {}", ringBuffer);
            return;
        }
        //如果线程填充不满，继续取下一秒的序列填充到缓冲区，知道填充慢RingBuffer为止
        boolean isFullRingBuffer = false;
        while (!isFullRingBuffer) {
            List<Long> uidList = uidProvider.provide(lastMillisecond.incrementAndGet());
            for (Long uid : uidList) {
                isFullRingBuffer = !ringBuffer.put(uid);
                if (isFullRingBuffer) {
                    break;
                }
            }
        }
        //设置线程执行结束
        running.compareAndSet(true, false);
        LogUtil.debug(LOGGER, "结束补充到RingBuffer lastSecond:{}. {}", lastMillisecond.get(), ringBuffer);
    }

}
