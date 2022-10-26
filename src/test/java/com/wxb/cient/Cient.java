package com.wxb.cient;

import java.util.Scanner;

import com.wxb.IDL.Proto;
import com.wxb.rpc.proxy.RpcCientProxy;

public class Cient {
    public static void main(String args[])
    {
        //获取rpc代理
        RpcCientProxy rpcCientProxy = new RpcCientProxy("127.0.0.1",55555,30);
        Proto protoProxy = rpcCientProxy.getProxyService(Proto.class);

        Scanner scanner = new Scanner(System.in);
        float a,b,result;
        System.out.println("please input a and b: ");
        while(scanner.hasNext())
        {
            a=scanner.nextFloat();
            b=scanner.nextFloat();
            result = protoProxy.sum(a, b);
            System.out.printf("the result is %.3f\n",result);
            System.out.println("please input a and b: ");
        }
        scanner.close();

    }
    
}
