package com.vince.toolkit.http.handle;

import org.apache.http.conn.HttpClientConnectionManager;


public class IdleConnectionEvictor extends Thread {

    private final HttpClientConnectionManager connectionManager;

    private volatile boolean shutdown;

    public IdleConnectionEvictor(HttpClientConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
        this.start();//启动线程
    }

    @Override
    public void run() {
        try {
            while (!shutdown) {
                synchronized (this) {
                    wait(5000);
                    // 关闭失效的连接
                    connectionManager.closeExpiredConnections();
                }
            }
        } catch (InterruptedException ex) {
            // 结束
        }
    }

    public void shutdown() {
        shutdown = true;
        synchronized (this) {
            notifyAll();
        }
    }
}
