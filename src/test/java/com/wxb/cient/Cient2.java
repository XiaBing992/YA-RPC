package com.wxb.cient;

import com.wxb.IDL.Proto;
import com.wxb.rpc.proxy.RpcCientProxy;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class Cient2 {
    //@Test
    public static void main(String args[])
    {
        //获取rpc代理
        RpcCientProxy rpcCientProxy = new RpcCientProxy();
        Proto protoProxy = rpcCientProxy.getProxyService(Proto.class);

        //发送请求
        String str="test";

        String result = protoProxy.uppercase(str);
        System.out.println(result);

    }
    
}
