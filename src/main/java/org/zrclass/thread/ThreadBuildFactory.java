package org.zrclass.thread;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @module
 * @Author zhourui
 * @Date 2021/08/05/19:10
 */
public class ThreadBuildFactory implements ThreadFactory {

    private final ThreadGroup group;
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final String namePrefix;
    /**
     * 新建的线程默认名为：
     * 	defaultName + "-thread-N"
     * @param defaultName
     */
    public ThreadBuildFactory(String defaultName) {
        SecurityManager s = System.getSecurityManager();
        group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
        namePrefix = "threadpool-"+defaultName ;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
        if (t.isDaemon()){
            t.setDaemon(false);
        }
        if (t.getPriority() != Thread.NORM_PRIORITY){
            t.setPriority(Thread.NORM_PRIORITY);
        }
        return t;
    }

}
