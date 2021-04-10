package indi.utils;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolManager {

    /**
     * 单例设计模式（懒汉模式）
     * 单例首先私有化构造方法，然后懒汉模式在使用时创建，并提供get方法
     */

    private static class CreateThreadPoolManager{
        private static final ThreadPoolManager mInstance = new ThreadPoolManager();
    }


    public static ThreadPoolManager getInstance() {
        return CreateThreadPoolManager.mInstance;
    }

    private final ThreadPoolExecutor executor;


    private ThreadPoolManager() {
        //给corePoolSize赋值：当前设备可用处理器核心数*2 + 1,能够让cpu的效率得到最大程度执行（有研究论证的）
        //核心线程池的数量，同时能够执行的线程数量
        int corePoolSize = Runtime.getRuntime().availableProcessors() * 2 + 1;
        //最大线程池数量，表示当缓冲队列满的时候能继续容纳的等待任务的数量
        //存活时间
        long keepAliveTime = 1;
        TimeUnit unit = TimeUnit.HOURS;
        executor = new ThreadPoolExecutor(
                corePoolSize, //当某个核心任务执行完毕，会依次从缓冲队列中取出等待任务
                corePoolSize, //5,先corePoolSize,然后new LinkedBlockingQueue<Runnable>(),然后maximumPoolSize,但是它的数量是包含了corePoolSize的
                keepAliveTime, //表示的是maximumPoolSize当中等待任务的存活时间
                unit,
                new LinkedBlockingQueue<Runnable>(), //缓冲队列，用于存放等待任务，Linked的先进先出
                Executors.defaultThreadFactory(), //创建线程的工厂
                new ThreadPoolExecutor.AbortPolicy() //用来对超出maximumPoolSize的任务的处理策略
        );
    }

    /**
     * 执行任务
     */
    public void execute(Runnable runnable){
        if(runnable==null)return;

        executor.execute(runnable);
        System.gc();
    }
    /**
     * 从线程池中移除任务
     */
    public void remove(Runnable runnable){
        if(runnable==null)return;
        executor.remove(runnable);
        System.gc();
    }
}
