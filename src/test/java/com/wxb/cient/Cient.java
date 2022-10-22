package com.wxb.cient;

import com.wxb.IDL.Proto;
import com.wxb.rpc.proxy.RpcCientProxy;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class Cient {
    //@Test
    public static void main(String args[])
    {
        //获取rpc代理
        RpcCientProxy rpcCientProxy = new RpcCientProxy("127.0.0.1",55555,15);
        Proto protoProxy = rpcCientProxy.getProxyService(Proto.class);

        //发送请求
        float a=1.1f;
        float b=1.2f;

        float result = protoProxy.sum(a, b);
        System.out.println(result);

    }
    
}
