package com.wxb.rpc.protocol;

import java.io.Serializable;

import lombok.Builder;
import lombok.Data;

/**
 * @description: protocol层 定义了RPC协议
 * @author: 王夏兵
 * @createDate: 2022.10.20
 */

@Data
@Builder
public class RpcRequestProtocol implements Serializable{
    //协议头
    private RpcProtocolHeader header;
    //协议体
    private byte[] bodys;
}
