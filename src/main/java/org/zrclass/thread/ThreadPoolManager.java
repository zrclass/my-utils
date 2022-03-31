package org.zrclass.thread;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @module
 * @Author zhourui
 * @Date 2021/08/05/19:11
 * @description 注意不同模块请按下面方式重新定义线程池，严禁不同模块线程池的混用
 */
public class ThreadPoolManager {

    private static volatile ThreadPoolManager manager;

    /**
     * 双重检查延迟初始化
     *
     * @return
     */
    public static ThreadPoolManager getInstance() {
        if (manager == null) {
            synchronized (ThreadPoolManager.class) {
                if (manager == null) {
                    manager = new ThreadPoolManager();
                }
            }
        }
        return manager;
    }

    /**
     * 普通分发线程池
     */
    private ThreadPoolExecutor taskPool;

    /**
     * 事件审计工单线程线程
     */
    private ThreadPoolExecutor eventAuditPool;

    /**
     * 事件审计工单线程线程
     */
    private ThreadPoolExecutor eventKafkaPool;

    /**
     * 策略稽核线程
     */
    private ThreadPoolExecutor policyCheckPool;

    /**
     * syslog日志发送线程
     */
    private ThreadPoolExecutor syslogSendPool;


    private ThreadPoolManager() {
        this.taskPool = new ThreadPoolExecutor(
                64,
                128,
                60,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue(100),
                new ThreadBuildFactory("task_thread"),
                new ThreadPoolExecutor.CallerRunsPolicy());
        this.eventAuditPool = new ThreadPoolExecutor(
                64,
                128,
                60,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue(100),
                new ThreadBuildFactory("event_audit"),
                new ThreadPoolExecutor.CallerRunsPolicy());
        this.eventKafkaPool = new ThreadPoolExecutor(
                64,
                128,
                60,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue(100),
                new ThreadBuildFactory("event_kafka"),
                new ThreadPoolExecutor.CallerRunsPolicy());
        this.policyCheckPool = new ThreadPoolExecutor(
                64,
                128,
                60,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue(100),
                new ThreadBuildFactory("policy_check"),
                new ThreadPoolExecutor.CallerRunsPolicy());
        this.syslogSendPool = new ThreadPoolExecutor(
                64,
                128,
                60,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue(100),
                new ThreadBuildFactory("syslog_send"),
                new ThreadPoolExecutor.CallerRunsPolicy());
    }


    public ThreadPoolExecutor getTaskPool() {
        return taskPool;
    }

    public ThreadPoolExecutor getEventAuditPool() {
        return eventAuditPool;
    }

    public ThreadPoolExecutor getEventKafkaPool() {
        return eventKafkaPool;
    }

    public ThreadPoolExecutor getPolicyCheckPool() {
        return policyCheckPool;
    }

    public ThreadPoolExecutor getSyslogSendPool() {
        return syslogSendPool;
    }

    public boolean allDown() {
        return taskPool.getActiveCount() == 0;
    }

}
