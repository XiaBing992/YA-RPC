package com.wxb.rpc.transfer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

import com.wxb.rpc.protocol.RpcReponseProtocol;
import com.wxb.rpc.protocol.RpcRequestProtocol;

import lombok.Builder;
import lombok.Data;

/**
 * @description: transfer层 处理客户端请求
 * @author: 王夏兵
 * @createDate: 2022.10.21
 */

@Data
@Builder
public class RpcCientTransfer {
    private String ip;
    private String port;

    /**
     * @description: 发送数据
     */
    public RpcReponseProtocol sendData(RpcRequestProtocol rpcRequestProtocol) throws IOException,ClassNotFoundException
    {
        //创建套接字
        Socket socket = new Socket(ip,Integer.parseInt(port));
        //创建流对象
        ObjectOutputStream socketOutStream=new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream socketInputStream=new ObjectInputStream(socket.getInputStream());

        //发送数据
        socketOutStream.writeObject(rpcRequestProtocol);
        socketOutStream.flush();

        //接受返回值
        RpcReponseProtocol rpcReponseProtocol = (RpcReponseProtocol)socketInputStream.readObject();

        return rpcReponseProtocol;

    }
}
