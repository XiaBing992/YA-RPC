import com.wxb.rpc.proxy.RpcServerProxy;

public class Server {
    public static void main(String[] args) {

        //创建RPC代理
        RpcServerProxy rpcServerProxy = new RpcServerProxy(5, 20, 60);
        //注册对象里的方法
        ProtoImpl protoImpl = new ProtoImpl();
        rpcServerProxy.Register(protoImpl);

        rpcServerProxy.server(8888);

    }
    
}
