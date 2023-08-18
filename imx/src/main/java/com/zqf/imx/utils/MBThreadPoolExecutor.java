/*
 * Copyright (C) 2022  即时通讯网(52im.net) & Jack Jiang.
 * The MobileIMSDK_TCP (MobileIMSDK v6.x TCP版) Project.
 * All rights reserved.
 *
 * > Github地址：https://github.com/JackJiang2011/MobileIMSDK
 * > 文档地址：  http://www.52im.net/forum-89-1.html
 * > 技术社区：  http://www.52im.net/
 * > 技术交流群：185926912 (http://www.52im.net/topic-qqgroup.html)
 * > 作者公众号：“即时通讯技术圈】”，欢迎关注！
 * > 联系作者：  http://www.52im.net/thread-2792-1-1.html
 *
 * "即时通讯网(52im.net) - 即时通讯开发者社区!" 推荐开源工程。
 *
 * MBThreadPoolExecutor.java at 2022-7-28 17:24:48, code by Jack Jiang.
 */
package com.zqf.imx.utils;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 所有的操作：线程池提供和管理线程
 */
public class MBThreadPoolExecutor {

    private static final String TAG = MBThreadPoolExecutor.class.getSimpleName();
    //获取cpu核心线程数->得到可用的计算资源。
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    //核心线程池大小
    private static final int CORE_POOL_SIZE = Math.max(2, Math.min(CPU_COUNT - 1, 4));
    //线程池最大值 cpu * 2 + 1
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    //保持心跳的时间
    private static final long KEEP_ALIVE_TIME = 30L;
    //用于线程池缓存任务的阻塞队列大小
    private static final int WAIT_COUNT = 128;

    //线程池
    private static ThreadPoolExecutor pool = createThreadPoolExecutor();

    /**
     * 线程工厂->创建线程池实例并分配大小
     *
     * @return 线程池实例对象
     */
    private static ThreadPoolExecutor createThreadPoolExecutor() {
        if (pool == null) {
            pool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS,
                    new LinkedBlockingQueue<Runnable>(WAIT_COUNT),
                    new CThreadFactory("MBThreadPool", Thread.NORM_PRIORITY - 2),
                    new CHandlerException());
        }
        return pool;
    }

    /**
     * 线程工长
     */
    public static class CThreadFactory implements ThreadFactory {
        //原子操作的Integer类，通过线程安全的方式操作加减
        private AtomicInteger counter = new AtomicInteger(1);
        //前缀
        private String prefix = "";
        //优先级 线程默认的优先级 5 范围1~10，此处代表概率，不代表绝对的按照设定执行先后顺序
        private int priority = Thread.NORM_PRIORITY;

        /**
         * 构造函数
         *
         * @param prefix   前缀：做线程的名字
         * @param priority 下线程优先级
         */
        public CThreadFactory(String prefix, int priority) {
            this.prefix = prefix;
            this.priority = priority;
        }

        public CThreadFactory(String prefix) {
            this.prefix = prefix;
        }

        public Thread newThread(Runnable r) {
            Thread executor = new Thread(r, prefix + " #" + counter.getAndIncrement());
            executor.setDaemon(true);
            executor.setPriority(priority);
            return executor;
        }
    }

    /**
     * 处理线程池的异常类
     */
    private static class CHandlerException extends ThreadPoolExecutor.AbortPolicy {

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
            Log.d(TAG, "rejectedExecution:" + r);
            Log.e(TAG, logAllThreadStackTrace().toString());
            if (!pool.isShutdown()) {
                pool.shutdown();
                pool = null;
            }

            pool = createThreadPoolExecutor();
        }
    }

    private static ExecutorService jobsForUI = Executors.newFixedThreadPool(
            CORE_POOL_SIZE, new CThreadFactory("MBJobsForUI", Thread.NORM_PRIORITY - 1));

    public static void runInBackground(Runnable runnable) {
        if (pool == null) {
            createThreadPoolExecutor();
        }
        pool.execute(runnable);
    }

    private static Thread mainThread;
    private static Handler mainHandler;

    static {
        Looper mainLooper = Looper.getMainLooper();
        mainThread = mainLooper.getThread();
        mainHandler = new Handler(mainLooper);
    }

    public static boolean isOnMainThread() {
        return mainThread == Thread.currentThread();
    }

    public static void runOnMainThread(Runnable r) {
        if (isOnMainThread()) {
            r.run();
        } else {
            mainHandler.post(r);
        }
    }

    public static void runOnMainThread(Runnable r, long delayMillis) {
        if (delayMillis <= 0) {
            runOnMainThread(r);
        } else {
            mainHandler.postDelayed(r, delayMillis);
        }
    }

    private static HashMap<Runnable, Runnable> mapToMainHandler = new HashMap<Runnable, Runnable>();

    public static void runInBackground(final Runnable runnable, long delayMillis) {
        if (delayMillis <= 0) {
            runInBackground(runnable);
        } else {
            Runnable mainRunnable = () -> {
                mapToMainHandler.remove(runnable);
                pool.execute(runnable);
            };

            //# Bug FIX: 20200716 by Jack Jiang
            if (mapToMainHandler.containsKey(runnable)) {
                Log.d(TAG, "该runnable（" + runnable + "）仍在mapToMainHandler中，表示它并未被执行，将" +
                        "先从mainHandler中移除，否则存在上次延迟执行并未完成，本次又再次提交延迟执行任务，失去了延迟执行的意义！");
                removeCallbackInBackground(runnable);
            }
            //# Bug FIX: END

            mapToMainHandler.put(runnable, mainRunnable);
            mainHandler.postDelayed(mainRunnable, delayMillis);
        }
    }

    public static void removeCallbackOnMainThread(Runnable r) {
        mainHandler.removeCallbacks(r);
    }

    public static void removeCallbackInBackground(Runnable runnable) {
        Runnable mainRunnable = mapToMainHandler.get(runnable);
        if (mainRunnable != null) {
            mainHandler.removeCallbacks(mainRunnable);
            pool.remove(mainRunnable); // add by jackjiang 20200718：记得尝试清除已加入线程队列但未开始执行的任务
        } else
            pool.remove(runnable);     // add by jackjiang 20200718：记得尝试清除已加入线程队列但未开始执行的任务
    }

    public static void logStatus() {
        StringBuilder sb = new StringBuilder();
        sb.append("getActiveCount=");
        sb.append(pool.getActiveCount());
        sb.append("\ngetTaskCount=");
        sb.append(pool.getTaskCount());
        sb.append("\ngetCompletedTaskCount=");
        sb.append(pool.getCompletedTaskCount());
        Log.d(TAG, sb.toString());
    }

    public static StringBuilder logAllThreadStackTrace() {
        StringBuilder builder = new StringBuilder();
        Map<Thread, StackTraceElement[]> liveThreads = Thread.getAllStackTraces();
        for (Iterator<Thread> i = liveThreads.keySet().iterator(); i.hasNext(); ) {
            Thread key = i.next();
            builder.append("Thread ").append(key.getName()).append("\n");
            StackTraceElement[] trace = liveThreads.get(key);
            for (int j = 0; j < trace.length; j++) {
                builder.append("\tat ").append(trace[j]).append("\n");
            }
        }
        return builder;
    }
}
