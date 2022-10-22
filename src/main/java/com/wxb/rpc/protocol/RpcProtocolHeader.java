package com.wxb.rpc.protocol;

import java.io.Serializable;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RpcProtocolHeader implements Serializable{
    private Integer version; //协议版本号
    private Integer messageSize;//消息大小
    private Integer messageId;//消息id
    
}
