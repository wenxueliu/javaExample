package org.wenxueliu.thrift;

import org.apache.thrift.server.TServer.Args;
import org.wenxueliu.thrift.service.ThriftService;
import org.wenxueliu.thrift.service.ThriftServiceImpl;
import java.net.ServerSocket;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;

public class ThriftServer {
    public static void main(String []args) throws Exception {
        TServerSocket serverTransport = new TServerSocket(7912);
        ThriftService.Processor processor = new ThriftService.Processor(new ThriftServiceImpl());
        TServer server = new TSimpleServer(new Args(serverTransport).processor(processor));
        System.out.println("Running server...");
        server.serve();
    }
}

