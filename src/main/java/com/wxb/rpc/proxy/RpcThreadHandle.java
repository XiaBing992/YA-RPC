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
import com.wxb.rpc.protocol.RpcProtocolStatus;
import com.wxb.rpc.protocol.RpcReponseProtocol;
import com.wxb.rpc.protocol.RpcRequestProtocol;

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
            ObjectInputStream socketInputStream = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream socketOutputStream = new ObjectOutputStream(socket.getOutputStream());

            //处理收的数据
            RpcRequestProtocol rpcRequestProtocol = (RpcRequestProtocol)socketInputStream.readObject();
            RpcProtocolHeader rpcProtocolHeader = rpcRequestProtocol.getHeader();

            //判断请求头
            if(rpcProtocolHeader.getStatus()==RpcProtocolStatus.OK&&rpcProtocolHeader.getMagic()==1&&rpcProtocolHeader.getMessageType().equals("byte"))
            {
                System.out.println("111");
                //处理protocol层
                byte[] rpcProtocolBoby = rpcRequestProtocol.getBodys();
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(rpcProtocolBoby);
                ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
                //反解析到codec层
                RpcRequestCodec rpcRequestCodec = (RpcRequestCodec)objectInputStream.readObject();


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

                //传输返回值
                System.out.println("send result:"+retObject);
                socketOutputStream.writeObject(reversePReponseProtocol);
                socketOutputStream.flush(); 


            }
            
        }
        catch(IOException |ClassNotFoundException | NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException error)
        {
            error.printStackTrace();
        }
        

    }

    
}
