package com.wxb.rpc.proxy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.HashMap;

import com.wxb.rpc.codec.RpcRequestCodec;
import com.wxb.rpc.codec.RpcResponseCodec;
import com.wxb.rpc.protocol.RpcProtocolHeader;
import com.wxb.rpc.protocol.RpcReponseProtocol;

/**
 * @discription: 用于对服务器收到的数据进行处理
 * @author: 王夏兵
 * @createDate: 2022.10.21
 */
public class RpcThreadHandle implements Runnable{
    private Socket socket;
    private HashMap<String,Object> registeredThreads;

    RpcThreadHandle(Socket socket,HashMap<String,Object> registeredThreads)
    {
        this.socket=socket;
        this.registeredThreads=registeredThreads;
    }
   
    @Override
    public void run() 
    {
        try
        {
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream ObjectOutputStream = new ObjectOutputStream(socket.getOutputStream());

            //处理收的数据
            RpcReponseProtocol rpcReponseProtocol = (RpcReponseProtocol)objectInputStream.readObject();
            RpcProtocolHeader rpcProtocolHeader = rpcReponseProtocol.getHeader();

            if(rpcProtocolHeader.getMessageId()==1)
            {
                //处理protocol层
                byte[] rpcProtocolBoby = rpcReponseProtocol.getBodys();
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(rpcProtocolBoby);
                ObjectInputStream objectInputStream2 = new ObjectInputStream(byteArrayInputStream);
                //反解析到codec层
                RpcRequestCodec rpcRequestCodec = (RpcRequestCodec)objectInputStream2.readObject();


                //调用任务
                Object task = registeredThreads.get(rpcRequestCodec.getInterfaceName());
                Method method = task.getClass().getMethod(rpcRequestCodec.getFunctionName(), rpcRequestCodec.getParamsTypes());
                Object retObject = method.invoke(task, rpcRequestCodec.getParams());


                /*-------------------------传输返回值--------------------*/

                //codec层
                RpcResponseCodec rpcResponseCodec = RpcResponseCodec.builder().retObject(retObject).build();
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
                objectOutputStream.writeObject(rpcResponseCodec);
                byte[] bytes = byteArrayOutputStream.toByteArray(); 

                //protocol层
                RpcProtocolHeader reverseProtocolHeader = rpcProtocolHeader;
                RpcReponseProtocol reversePReponseProtocol = RpcReponseProtocol.builder().header(reverseProtocolHeader).bodys(bytes).build();
                objectOutputStream.writeObject(reversePReponseProtocol);
                objectOutputStream.flush(); 


            }
            
        }
        catch(IOException |ClassNotFoundException | NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException error)
        {
            error.printStackTrace();
        }
        

    }

    
}
