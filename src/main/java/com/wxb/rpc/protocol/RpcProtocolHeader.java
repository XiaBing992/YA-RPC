package com.wxb.rpc.protocol;

import java.io.Serializable;

import lombok.Builder;
import lombok.Data;


/**
 * @description: protocol层协议头
 * @author: 王夏兵
 * @createDate: 2022.10.20
 */


@Data
@Builder
public class RpcProtocolHeader implements Serializable{
    private Integer magic; //协议版本号
    private RpcProtocolStatus status;//标识状态
    private String messageType;//消息类型
    private String messageEncoding;//消息编码
    
}
