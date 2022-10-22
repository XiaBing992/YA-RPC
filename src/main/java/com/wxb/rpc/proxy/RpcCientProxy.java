package com.wxb.rpc.proxy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import com.wxb.rpc.codec.RpcRequestCodec;
import com.wxb.rpc.codec.RpcResponseCodec;
import com.wxb.rpc.protocol.RpcProtocolHeader;
import com.wxb.rpc.protocol.RpcProtocolStatus;
import com.wxb.rpc.protocol.RpcReponseProtocol;
import com.wxb.rpc.protocol.RpcRequestProtocol;
import com.wxb.rpc.transfer.RpcCientTransfer;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author: 王夏兵
 * @createDate: 2022.10.20
 */
@Data
@AllArgsConstructor
public class RpcCientProxy implements InvocationHandler{
    String ip;
    int port;
    int MAX_RETRANSMISSION_TIMES=15;

    /**
     * @description: 得到当前对象的代理服务
     * @param <T>: 需要代理的对象
     * @return Proxy
     */
    public <T> T getProxyService(Class<T> c)
    {
        return (T) Proxy.newProxyInstance(c.getClassLoader(), new Class<?>[]{c}, this);
    }

    /**
     * @description: 代理的动态映射函数
     * @param proxy: 代理
     * @param method：方法名
     * @param args: 方法参数
     */
    @Override
    public Object invoke(Object proxy,Method method,Object args[]) throws Throwable
    {
        System.out.println("codec...");
        /*--------------------------处理codec层------------------------------*/

        //传入调用函数基本信息
        RpcRequestCodec rpcRequestCodec = RpcRequestCodec.builder()
            .interfaceName(method.getDeclaringClass().getName())
            .functionName(method.getName())
            .params(args)
            .paramsTypes(method.getParameterTypes())
            .build();
        
        //创建字节流数组
        ByteArrayOutputStream requestByteData=new ByteArrayOutputStream();
        ObjectOutputStream requestObjectData=new ObjectOutputStream(requestByteData);

        //请求对象转化为字节流
        requestObjectData.writeObject(rpcRequestCodec);
        byte[] bytes=requestByteData.toByteArray();


        /*--------------------------处理protocol层----------------------------- */

        System.out.println("protocol...");
        //将codec的信息放入protocol
        RpcRequestProtocol rpcRequestProtocol = RpcRequestProtocol.builder()
            .header(RpcProtocolHeader.builder().magic(1).status(RpcProtocolStatus.OK).messageType("byte").messageEncoding("utf-8").build())
            .bodys(bytes).build();

        /*---------------------------处理transfer层----------------------------- */
        System.out.println("transfer...");
        //发送请求(at_least_once)
        RpcCientTransfer rpcCientTransfer = RpcCientTransfer.builder().ip(this.ip).port(this.port).build();
        RpcReponseProtocol rpcReponseProtocol=null;

        //未收到server的ACK就再次重传保证at_least_once
        for(int i=0;i<this.MAX_RETRANSMISSION_TIMES;i++)
        {
            try
            {
                rpcReponseProtocol = rpcCientTransfer.sendData(rpcRequestProtocol);
                break;
            }
            catch(IOException e)
            {
                System.out.printf("未收到服务器ACK，尝试第%d重传数据...\n",i+1);
                Thread.sleep(1000);
            }
        }
        //仍然无法收到ACK
        if(rpcReponseProtocol==null)
        {
            throw new IOException();
        }
        

        /*---------------------------处理返回数据--------------------------------- */
        RpcProtocolHeader rpcProtocolHeader = rpcReponseProtocol.getHeader();
        byte[] rpcProtocolBoby=rpcReponseProtocol.getBodys();
        
        //判断请求头
        if(rpcProtocolHeader.getStatus()==RpcProtocolStatus.OK&&rpcProtocolHeader.getMagic()==1&&rpcProtocolHeader.getMessageType().equals("byte"))
        {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(rpcProtocolBoby);
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);

            RpcResponseCodec rpcResponseCodec = (RpcResponseCodec)objectInputStream.readObject();

            //返回远程调用得到的数据
            return rpcResponseCodec.getRetObject();
        }

        return null;
        


    }
    
}
