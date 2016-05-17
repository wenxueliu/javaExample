namespace java org.wenxueliu.thrift.monitor

include "DestHost.thrift"

enum STATUS {
    OPEN,
    PENDING,
    CLOSE;
}

enum ACTIVE {
    ON,
    OFF,
}

struct DestHost {
    1: optional string id;
    2: optional string name;
    3: required string ip;
    4: required i32    port;
    5: required string protocol;
    6: optional STATUS status;
    7: optional ACTIVE active;
}

service IMonitorService {
    void connectChanged(1:DestHost host);
}

service IMonitorService {
    void addListener(1:IMonitorListener listener)
	DestHost addDestHost(1:DestHost host);
	DestHost addDestHost(1:string ip, 2:i32 port, 3:string protocol);
	DestHost addDestHost(1:string ip, 2:i32 port);
	void addDestHost(1:string beginIP, 2:string endIP, 3:i32 beginPort, 4:i32 endPort);
	void addDestHost(1:string beginIP, 2:string endIP, 3:i32 beginPort, 4:i32 endPort, 5:string protocol);
	DestHost getDestHost(1:string id);
	list<DestHost> getDestHosts();
    DestHost delDestHost(1:string id);
    DestHost delDestHost(1:destHost host);
    void delDestHosts();
	void dumpConnectedHost() throws (1:InterruptedException, 2:ExecutionException);
	void start();
}
