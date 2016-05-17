package org.wenxueliu.thrift;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.wenxueliu.thrift.service.ThriftService.Client;

public class ThriftClient {
    public static void main(String []args) {
        TTransport transport = new TSocket("localhost", 7912);
        TProtocol protocol = new TBinaryProtocol(transport);
        Client client = new Client(protocol);
        try {
            transport.open();
            client.helloString("hello thrift");
            client.add(1,1);
        } catch (TTransportException e) {
            System.out.println(e.getMessage());
        } catch (TException e) {
            System.out.println(e.getMessage());
        }

    }
}
