package com.wxb.cient;

import java.util.Scanner;

import com.wxb.IDL.Proto;
import com.wxb.rpc.proxy.RpcCientProxy;

public class Cient2 {
    //@Test
    public static void main(String args[])
    {
        //获取rpc代理
        RpcCientProxy rpcCientProxy = new RpcCientProxy("127.0.0.1",55555,15);
        Proto protoProxy = rpcCientProxy.getProxyService(Proto.class);

        //发送请求
        Scanner scanner = new Scanner(System.in);
        String str,result;
        System.out.println("please input str: ");
        while(scanner.hasNext())
        {
            str=scanner.nextLine();
            result = protoProxy.uppercase(str);
            System.out.printf("the result is %s\n",result);
            System.out.println("please input str: ");
        }
        scanner.close();

    }
    
}
