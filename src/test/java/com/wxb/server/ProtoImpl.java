package com.wxb.server;
import com.wxb.IDL.Proto;

import lombok.Builder;


/**
 * @discription: 服务器的方法实现
 * @author: 王夏兵
 * @createDate: 2022.10.21
 */
public class ProtoImpl implements Proto{
    @Override
    public float sum(float a,float b)
    {
        //System.out.println(a+b);
        return a+b;
    }

    @Override
    public String uppercase(String str)
    {
        return str.toUpperCase();
    }
}
