package com.wxb.server;


import java.io.IOException;

import org.junit.Test;

import com.wxb.rpc.proxy.RpcServerProxy;


public class Server {
    //@Test
    public static void main(String args[]) {

        //创建RPC代理
        RpcServerProxy rpcServerProxy = new RpcServerProxy(5, 20, 60);
        //注册对象里的方法
        ProtoImpl protoImpl = new ProtoImpl();
        rpcServerProxy.Register(protoImpl);

        try
        {
            rpcServerProxy.server(55555);
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        

    }
    
}
