package com.wxb.rpc.proxy;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import lombok.Data;

@Data
public class RpcServerProxy {
    private ExecutorService threadPool;//线程池
    private HashMap<String,Object> registeredThreads;//本地函数注册

    public RpcServerProxy(int corePoolSize,int maximumPoolSize,int keepAliveTime)
    {
        //存放任务的阻塞队列
        BlockingQueue<Runnable> tasksQueue = new ArrayBlockingQueue<Runnable>(1000);
        
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        this.threadPool=new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, tasksQueue, threadFactory);
        this.registeredThreads=new HashMap<String,Object>();
    }

    /**
     * @discription:注册方法
     * @param task
     */
    public void Register(Object task)
    {
        registeredThreads.put(task.getClass().getInterfaces()[0].getName(), task);
    }

    /**
     * @discription: 处理tcp请求
     * @param port
     * @throws IOException
     */
    public void server(int port) throws IOException
    {
        ServerSocket serverSocket = new ServerSocket(port);
        Socket dataSocket;
        while((dataSocket = serverSocket.accept())!=null)
        {
            System.out.println("receive data from:"+dataSocket.getInetAddress());
            threadPool.execute(new RpcThreadHandle(dataSocket, registeredThreads));
        }

    }


}
