package com.wxb.rpc.codec;

import java.io.Serializable;

import lombok.Builder;
import lombok.Data;

/**
 * @description: codec层，用于客服端发送请求编码
 * @author: 王夏兵
 * @createDate: 2022.10.20
 */

@Data
@Builder
public class RpcRequestCodec implements Serializable{
    private String interfaceName;
    private String functionName;
    private Object[] params;
    private Class<?>[] paramsTypes;
}
