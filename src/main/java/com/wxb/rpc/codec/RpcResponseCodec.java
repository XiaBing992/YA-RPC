package com.wxb.rpc.codec;

import java.io.Serializable;

import lombok.Builder;
import lombok.Data;

/**
 * @description: codec层，用于服务端返回值的编码
 * @author: 王夏兵
 * @createDate: 2022.10.20
 */

@Data
@Builder
public class RpcResponseCodec implements Serializable{
    private Object retObject; 
}
