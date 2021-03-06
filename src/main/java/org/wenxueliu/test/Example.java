package org.wenxueliu.test;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.UUID;
import java.util.Map;
import java.io.IOException;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.IndexOutOfBoundsException;
import java.lang.Exception; import java.lang.ref.WeakReference; import java.util.concurrent.TimeUnit;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import org.wenxueliu.annotations.AnnotationParsing;
import org.wenxueliu.fileiterators.TextFile;
import org.wenxueliu.interclass.EnumTest;
import org.wenxueliu.enums.StatusExample1;
import org.wenxueliu.enums.StatusExample2;
import org.wenxueliu.reference.ReferenceExample;
import org.wenxueliu.reference.ReferenceExample.Host;
import org.wenxueliu.collection.HashMapTest;
import org.wenxueliu.collection.ListTest;
import org.wenxueliu.concurrent.ThreadSyn;
import org.wenxueliu.concurrent.InsertData;
import org.wenxueliu.concurrent.ThreadLock;
import org.wenxueliu.concurrent.LinkedBlockingQueueExample;
import org.wenxueliu.concurrent.ThreadTest;
import org.wenxueliu.concurrent.MyThreadLocal;
import org.wenxueliu.concurrent.MyThreadLocalError;
import org.wenxueliu.concurrent.MyThreadLocalPlus;
import org.wenxueliu.concurrent.BenchTest;
import org.wenxueliu.concurrent.ExecutorsTest;
import org.wenxueliu.demotask.DemoExecutor;
import org.wenxueliu.classloader.MyClassLoader;
import org.wenxueliu.pdfbox.PDFEditor;
import org.wenxueliu.sizeof.ObjectSize;
import org.wenxueliu.zookeeper.ZKMap;
import org.wenxueliu.dery.DatabaseManager;
import org.wenxueliu.http.Client;
import org.wenxueliu.util.CmdLineExector;
import org.wenxueliu.io.NIOScatteringandGathering;
import org.wenxueliu.config.Resource;
import org.wenxueliu.log.LogConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
/**
 *
 */

/**
 * @author wenxueliu
 *
 */
public class Example {

    private static Logger logger = LoggerFactory.getLogger(Example.class);
    private final String DELIMITER = ":";

    void testCmdLineExecutor() {
        CmdLineExector.test();
    }

    void testForIn() {
        System.out.println("test for in of null");
        List<String> list = null;
        for (String str : list) {
            System.out.println(str);
        }
    }

    void testExecutors() {
        ExecutorsTest e = new ExecutorsTest();
        e.test();
        e.testCallable();
        e.testInvorkAll();
        e.testInvorkAny();
        e.testSchedule();
    }

    void testSystemCopyOf() {
        List<String> src = new ArrayList<String>();
        System.out.println(src.size() + " list to array  " + src.toArray(new String[src.size()]).length);
    }

    void testZookeeper() {
        CuratorFramework client = null;
        try {
            String connectionString = "192.168.0.134:2181";

            HashMap<String, String> topoMap = new HashMap<String, String>();

            String cluster1     = "/a5/cluster/1";
            topoMap.put(cluster1 + "/name", "c1");
            topoMap.put(cluster1 + "/type", "cluster");

            String c1a1         = cluster1 + "/a5/1";
            topoMap.put(c1a1 + "/name", "c1a1");
            topoMap.put(c1a1 + "/controller", "10.1.3.1:6633");
            topoMap.put(c1a1 + "/ovs", "10.1.3.1");

            String c1a2         = cluster1 + "/a5/2";
            topoMap.put(c1a2 + "/name", "c1a2");
            topoMap.put(c1a2 + "/controller", "10.1.2.2:6633");
            topoMap.put(c1a2 + "/ovs", "10.1.2.2");

            String c1p1         = cluster1 + "/pools" + "/1";
            topoMap.put(c1p1 + "/name", "c1p1");
            topoMap.put(c1p1 + "/mac", "00:00:00:00:00;01");
            topoMap.put(c1p1 + "/ip", "10.1.2.13");
            topoMap.put(c1p1 + "/port", "8000");
            topoMap.put(c1p1 + "/protocol", "tcp");
            topoMap.put(c1p1 + "/lbmethod", "rr");
            topoMap.put(c1p1 + "/type", "follower");
            String c1p1servers = c1p1 + "/servers";
            topoMap.put(c1p1servers + "/1", "1");
            topoMap.put(c1p1servers + "/2", "2");

            String p1b1         = cluster1 + "/servers" + "/1";
            topoMap.put(p1b1 + "/name", "1");
            topoMap.put(p1b1 + "/mac", "00:00:00:00:00:02");
            topoMap.put(p1b1 + "/ip", "10.1.2.3");
            topoMap.put(p1b1 + "/port", "80");
            topoMap.put(p1b1 + "/protocol", "tcp");
            topoMap.put(p1b1 + "/state", "1");
            topoMap.put(p1b1 + "/pool", "1");

            String p1b2         = cluster1 + "/servers" + "/2";
            topoMap.put(p1b2 + "/name", "2");
            topoMap.put(p1b2 + "/mac", "00:00:00:00:00:03");
            topoMap.put(p1b2 + "/ip", "10.1.2.4");
            topoMap.put(p1b2 + "/port", "80");
            topoMap.put(p1b2 + "/protocol", "tcp");
            topoMap.put(p1b2 + "/state", "1");
            topoMap.put(p1b2 + "/pool", "1");

            String cluster2     = "/a5/cluster/2";
            topoMap.put(cluster2 + "/name", "c2");
            topoMap.put(cluster2 + "/type", "ha");

            String p2b1         = cluster2 + "/servers" + "/1";
            topoMap.put(p2b1 + "/name", "1");
            topoMap.put(p2b1 + "/mac", "00:00:00:00:00:02");
            topoMap.put(p2b1 + "/ip", "10.1.2.3");
            topoMap.put(p2b1 + "/port", "80");
            topoMap.put(p2b1 + "/protocol", "tcp");
            topoMap.put(p2b1 + "/state", "1");
            topoMap.put(p2b1 + "/pool", "1");

            String c2a1         = cluster2 + "/a5/1";
            topoMap.put(c2a1 + "/name", "c2a1");
            topoMap.put(c2a1 + "/controller", "10.1.3.2:6633");
            topoMap.put(c2a1 + "/ovs", "10.1.3.2");

            client = CuratorFrameworkFactory.builder()
                .connectString(connectionString)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .connectionTimeoutMs(3000)
                .sessionTimeoutMs(3000)
                .build();
            client.start();

            Iterator<Map.Entry<String, String>> iter = topoMap.entrySet().iterator();
            while(iter.hasNext()) {
                Map.Entry<String, String> entry = iter.next();
                client.create().creatingParentsIfNeeded().forPath(entry.getKey(), entry.getValue().getBytes());
                //client.create().forPath(entry.getKey(), entry.getValue().getBytes());
            }

            iter = topoMap.entrySet().iterator();
            while(iter.hasNext()) {
                Map.Entry<String, String> entry = iter.next();
                byte[] data = client.getData().forPath(entry.getKey());
                System.out.println(entry.getKey() + "-->" + data);
            }

            //while(iter.hasNext()) {
            //    Map.Entry<String, String> entry = iter.next();
            //    client.delete().forPath(entry.getKey());
            //    Stat state = client.checkExists().forPath(entry.getKey());
            //    System.out.println(entry.getKey() + "state -->" + state);
            //}

        } catch (Exception e) {
            e.printStackTrace();
            if (client != null) {
                client.close();
            }
        } finally {
            if (client != null) {
                client.close();
            }
        }
    }

    void testZKMap() {
       ZKMap.test();
    }

    void testDery() {
        try {
            DatabaseManager.initDatabase("test", "a5", "a5", true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void ObjectSizeTest() {
        ObjectSize.testSize();
    }


    void pdfboxTest() {
        PDFEditor read = new PDFEditor();
        String s = read.readFdf("/home/wenxueliu/book/release-notes.en.pdf");
        System.out.println(s);
    }

    void ClassLoaderTest() {
        MyClassLoader.defaultLoader();
        MyClassLoader.serviceLoader();
    }

    void ThreadLocalPlusTest() {
        final MyThreadLocalPlus test = new MyThreadLocalPlus();

        test.set();
        System.out.println(test.getLong());
        System.out.println(test.getString());


        Thread thread1 = new Thread(){
                    public void run() {
                                    test.set();
                                    System.out.println(test.getLong());
                                    System.out.println(test.getString());
                                };
                };
        thread1.start();

        try {
            thread1.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(test.getLong());
        System.out.println(test.getString());
    }

    void ThreadLocalErrorTest() {
        final MyThreadLocalError test = new MyThreadLocalError();

        try {
            System.out.println(test.getLong());
            System.out.println(test.getString());

            Thread thread1 = new Thread(){
                        public void run() {
                                        test.set();
                                        System.out.println(test.getLong());
                                        System.out.println(test.getString());
                                    };
                    };
            thread1.start();
            try {
                thread1.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println(test.getLong());
            System.out.println(test.getString());
        } catch (NullPointerException e) {
            System.out.println("Expect: null pointer exception throw");
        }
    }

    void ThreadLocalTest() {
        MyThreadLocal.LocalTest();
    }
    void ThreadBasicTest() {
        ThreadTest test = new ThreadTest();
        test.testSelf();
    }
    void LinkedBlockingQueueExampleTest() {
        LinkedBlockingQueueExample.example();
    }

    void DemoExecutorTest() {
        DemoExecutor de = new DemoExecutor();
        de.example();
    }
    void ThreadLockTest() {
        final ThreadLock tl = new ThreadLock();
        System.out.println("------ ThreadLockTest --------");
        tl.testLocalLock();
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch(InterruptedException e) {
            //No-op
        }
        System.out.println("-- testGlobalLock --");
        tl.testGlobalLock();
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch(InterruptedException e) {
            //No-op
        }

        System.out.println("-- testTryLock --");
        tl.testTryLock();
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch(InterruptedException e) {
            //No-op
        }
        System.out.println("-- testInterruptLock --");
        tl.testInterruptLock();
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch(InterruptedException e) {
            //No-op
        }
        System.out.println("-- testSynchronizedGet --");
        tl.testSynchronizedGet();
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch(InterruptedException e) {
            //No-op
        }
        System.out.println("-- testReentrantReadWriteLockGet --");
        tl.testReentrantReadWriteLockGet();
    }

    void ThreadSynTest() {
        ThreadSyn ts = new ThreadSyn();
        //System.out.println("------ ThreadSynTest --------");
        //System.out.println("------------ normal Test ------------");
        //ts.test();
        //try {
        //    TimeUnit.SECONDS.sleep(3);
        //} catch(InterruptedException e) {
        //    //No-op
        //}
        //System.out.println("------------ synchronized Test ------------");
        //ts.testSyn();
        //try {
        //    TimeUnit.SECONDS.sleep(3);
        //} catch(InterruptedException e) {
        //    //No-op
        //}
        //System.out.println("------------ synchronized static Test ------------");
        //ts.testStaticSyn();
        //try {
        //    TimeUnit.SECONDS.sleep(3);
        //} catch(InterruptedException e) {
        //    //No-op
        //}
		//System.out.println("------------ CountDownLatchTest Test ------------");
		//ts.CountDownLatchTest();
		//try {
        //    TimeUnit.SECONDS.sleep(3);
        //} catch(InterruptedException e) {
        //    //No-op
        //}
		//System.out.println("------------  CyclicBarrierTest ------------");
		//ts.CyclicBarrierTest();

		System.out.println("------------  voliateTest ------------");
        ts.voliateTest();

		System.out.println("------------  voliateSynTest ------------");
        ts.voliateSynTest();

		System.out.println("------------  voliateLockTest ------------");
        ts.voliateLockTest();
    }

    void listLoopTest() {
        ListTest listTest = new ListTest();
        System.out.println("------ listLoopTest --------");
        listTest.TestList(100);
        listTest.TestList(1000);
        listTest.TestList(10000);
        listTest.TestList(100000);
        listTest.TestList(1000000);
    }

    void ListHashMap() {
        HashMapTest hash = new HashMapTest();
        System.out.println("------ HashMapTravelTest --------");
        hash.loopMapCompare(hash.getHashMaps(100));
        hash.loopMapCompare(hash.getHashMaps(1000));
        hash.loopMapCompare(hash.getHashMaps(10000));
        hash.loopMapCompare(hash.getHashMaps(100000));
        hash.loopMapCompare(hash.getHashMaps(1000000));
    }
    void StringCompare() {
        System.out.println("------ StringCompare --------");
        String str = new String("192.168.1.24:8080");
        String str2 = new String("192.168.1.24:8080");
        System.out.println("str:" + str);
        System.out.println("str2:" + str2);
        System.out.println("str.equals(str2) : " + str.equals(str2));
        System.out.println("str==str2 : " + (str == str2));
    }
    void ArrayListToString() {
        System.out.println("------ ArrayListToString --------");
        ArrayList<String> str = new ArrayList<String>();
        str.add("add");
        str.add("add");
        str.add("add");
        System.out.println(str.toString());
    }
    void ReferenceTest() {
        System.out.println("------ ReferenceTest --------");
        Host hs = new ReferenceExample.Host("10.0.0.1", "00:00:00:00:00");
        Host hs1 = new ReferenceExample.Host("100.0.0.1", "00:00:00:00:00");
        int id = 1;
        String name = "aaa";
        ReferenceExample re = new ReferenceExample(id, name,hs);

        HashMap<String, ReferenceExample> hm = new HashMap<String, ReferenceExample>();
        hm.put(String.valueOf(re.getId()), re);
        //ReferenceExample re1 = hm.get("1");
        //re1.setName("bbb");
        //re1.setHost(hs1);
        WeakReference<ReferenceExample> re1 = new WeakReference<ReferenceExample>(hm.get("1"));
        System.out.println("before change:" + re1.get().toString());
        System.out.println("before change:" + hm.get("1").toString());
        re1.get().setName("bbb");
        re1.get().setHost(hs1);
        hs1.setIp("100.0.0.2");
        System.out.println("after change:" + hm.get("1").toString());
        hm.remove("1");
        System.gc();
        System.out.println("after remove:" + re1.get().toString());

    }

    void HashSetVsArrayList() {
        System.out.println("------ HashSetVsArrayList --------");
        HashSet<String> hashSet = new HashSet<String>();
        long start = System.currentTimeMillis();
        for (int i = 0; i < 900000; i++) {
            hashSet.add(String.valueOf(i));
        }

        System.out.println("Insert HashSet Time: " + (System.currentTimeMillis() - start));


        ArrayList<String> arrayList = new ArrayList<String>();

        start = System.currentTimeMillis();

        for (int i = 0; i < 900000; i++) {
            arrayList.add(String.valueOf(i));
        }
        System.out.println("Insert ArrayList Time: " + (System.currentTimeMillis() - start));
    }

    public class Cluster {
        protected Long id; // the lowest id of the nodes
        protected Map<Long, Set<String>> links; // set of links connected to a node.

        public Cluster() {
            id = new Long(1);
            links = new HashMap<Long, Set<String>>();
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        void add(Long n) {
            if (links.containsKey(n) == false) {
                links.put(n, new HashSet<String>());
    			if (id == 0 || n < id)
    				id = n ;
            }
        }

        @Override
        public int hashCode() {
            return (int) (id + id >>>32);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;

            Cluster other = (Cluster) obj;
            return (this.id.equals(other.id));
        }

        public String toString() {
            return "[Cluster id=" + id.toString() + ", " + links.keySet() + "]";
        }
    }


    public void HashMapTest() {
        System.out.println("------ HashMapTest --------");
        Cluster cluster = new Cluster();
        HashMap<String, Cluster> strMap = new HashMap<String, Cluster>();
        cluster.add(new Long(1));
        strMap.put("1", cluster);
        System.out.println("before modify " + strMap.get("1").toString());
        cluster.add(new Long(2));

        System.out.println("after modify " + strMap.get("1").toString());
    }
    //public void TestException() throws IndexOutOfBoundsException
    public void TestException(List<String> list)
    {
        System.out.println("travel list");
        for (String s: list){
            System.out.println(s);
        }
        //list.add("aaaaaaaaaa");
        //list.remove(list.size() - 1);
    }

    public void TestThrow(String str){
        if (str == "") {
            throw new java.lang.IllegalArgumentException("can not be empty");
        }
    }

    public void ThrowTest() {
        System.out.println("------ testThrow2 --------");
        try {
            TestThrow2();
        } catch (IllegalArgumentException ex) {
            System.out.println(" error : " + ex.getMessage());
        }

        System.out.println("------ testThrow3 --------");
        try {
            TestThrow3();
        } catch (IllegalArgumentException ex) {
            System.out.println(" error : " + ex.getMessage());
        }
    }

    public void TestThrow2() {
        TestThrow("");
    }

    public void TestThrow3() throws IndexOutOfBoundsException {
        TestThrow("");
    }

    public void AnnotationTest(){
        System.out.println("------ AnnotationTest --------");
        AnnotationParsing.parseMethodInfo();
    }

    public void FileIteratorTest(String filename){
        System.out.println("------ FileIteratorTest --------");
        //使用增强for循环进行文件的读取
        for(String line:new TextFile(filename)){
            System.err.println(line);
        }
    }

    public void interclassTest(){
        System.out.println("------ interclassTest --------");
        EnumTest enumTest = new EnumTest(EnumTest.WeekDay.valueOf(0));
        System.out.println("today : " + enumTest.toString());
        try {
            enumTest.setDay(EnumTest.WeekDay.valueOf(2));
        } catch (IllegalArgumentException ex){
            System.out.println("IllegalArgumentException:" + ex.getMessage());
        }
        System.out.println("today : " + enumTest.getDay());
        //System.out.println("today name: " + enumTest.name());
        System.out.println("today toStrin: " + enumTest.toString());
        EnumTest.info();
    }

    void ExceptionTest() {
        System.out.println("------ Exception Test --------");

        List<String> list = new ArrayList<String>();
        //List<String> list = null;
        try {
            TestException(list);
        } catch (Exception ex) {
            System.out.println("IndexOutOfBoundsException exception");
            ex.printStackTrace();
        }
    }

    void RefCopyTest() {
		Map <String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();
		ArrayList<String> array = new ArrayList<String>();
		array.add("abc");
		map.put("2", array);
		map.put("1", new ArrayList<String>(Arrays.asList("cde")));
		System.out.println(map.get("1").toString());
		map.get("1").add("bcd");
		System.out.println(map.get("1").toString());
    }

    //public void StatusTest(){
    //    System.out.println("------ StatusTest  --------");
    //    StatusExample1 st = new StatusExample1(StatusExample1.Process.STATUS_OPEN);

    //    StatusExample1.Status s1 = StatusExample1.Status(1);
    //    StatusExample1.Status s2 = StatusExample1.Status(6);
    //    System.out.println(s1.status());
    //    System.out.println(s2.status());

    //    StatusExample1.Status s3 = StatusExample1.Status(1, "hello");
    //    StatusExample1.Status s4 = StatusExample1.Status(6, "enum");
    //    System.out.println(s3.status());
    //    System.out.println(s4.status());
    //}

    void benchTest() {
        BenchTest bench = new BenchTest();
        bench.genKeys("10.1.2.1", "10.1.2.60", 1, 6000);
        logger.info("gen keys:" + bench.getKeySize() + " done\n begin test ....\n");
        //bench.testWithMap();
        bench.testWithSSDB();
        bench.testWithRedis();
        logger.info("begin test ....\n");
    }

    void byteTest() {
        byte[] bytes = new byte[] { -127, -128, -1, 0, 1, 2, 3, 100, 126, 127 };
        int len = bytes.length;
        char[] chars = new char[len];
        for (int i = 0; i < len; i++) {
            chars[i] = (char)(bytes[i] & 0xff);
        }
        System.out.println("old bytes " + Arrays.toString(bytes));
        System.out.println("chars " + new String(chars));

        byte[] newBytes = new byte[len];
        for (int i = 0; i < len; i++) {
            newBytes[i] = (byte)chars[i];
        }

        System.out.println("new bytes " + Arrays.toString(newBytes));

    }

    public void testIO() {
        new NIOScatteringandGathering().createFiles("/tmp/tmp.txt");
    }

	/**
	 * @param args
	 */
	public static void main(String[] args) {

        Example e = new Example();
        //e.byteTest();
        //e.RefCopyTest();
        //e.AnnotationTest();
        //e.FileIteratorTest("/home/wenxueliu/dic.txt");
        //e.interclassTest();
        //e.ThrowTest();
        //e.HashMapTest();
        //e.HashSetVsArrayList();
        //e.ReferenceTest();
        //e.ArrayListToString() ;
        //e.StringCompare();
        //e.ListHashMap();
        //e.listLoopTest();
        //e.ThreadSynTest();
        //e.ThreadLockTest();
        //e.DemoExecutorTest();
        //e.LinkedBlockingQueueExampleTest();
        //e.ThreadBasicTest();
        //e.testNode();
        //e.ThreadLocalTest();
        //PerformanceTest.testVar();
        //e.ThreadLocalErrorTest();
        //e.ThreadLocalPlusTest();
        //e.ClassLoaderTest();
        //e.pdfboxTest();
        //e.ObjectSizeTest();
        //e.benchTest();
        //e.testDery();
        //e.testZookeeper();
        //e.testZKMap();
        //e.testSystemCopyOf();
        //e.testForIn();
        //e.testHttp();
        //e.testExecutors();
        //e.testCmdLineExecutor();
        //e.testString();
        //e.testIO();
        //e.testConfig();
        //e.testSystemProperty();
        //e.testReloadLog();
        //e.testSystemProperty();
        long begin = System.currentTimeMillis();
        e.lightWeightCmd("ls");
        long end = System.currentTimeMillis();
        System.out.println("take time" + (end - begin));
	}

    public void testSystemProperty() {
        String logPath = System.getProperty("logback.configurationFile");
        logger.info("log config dir: {}", logPath);
        File f = new File(logPath);
        logger.debug("log is exist {} isFile {}", f.exists(), f.isFile());
        logger.debug("log canWrite {} getAbsolutePath {}", f.canWrite(), f.getAbsolutePath());
    }

    public void testReloadLog() {
        LogConfig.testReloadLog();
    }

    public void testConfig() {
        Resource.test();
    }

    public void testString() {
        logger.info("----- String Test ------");
        String ip1 = "192.168.1.1";
        String ip2 = "192.168.1.1/24";
        String []ret  = ip1.split("/");
        String []ret1  = ip2.split("/");
        logger.info("{} ret1 length {}, ret1[0]:{}, ret1[1]:{}", new Object[] { ip1, ret.length, ret1[0], ret1[1] });
        try {
            logger.info("{} ret length:{}, ret[0]:{}, ret[1]:{}", new Object[]{ ip2, ret1.length, ret[0], ret[1] });
        } catch (ArrayIndexOutOfBoundsException e) {
            logger.info("{} ret length:{}, ret[0]:{}, ret[1]:{}", new Object[]{ ip2, ret1.length, ret[0]});
        }

        short i = -1;
        logger.info("short -1 is {}", i & 0xFFFF);
        logger.info("short MIN_VALUE is {}", ((Short.MIN_VALUE) & 0xFFFF));

        logger.info("get mac from {} to {}", "10.9.1.106", getPseudoMacAddress("10.9.1.106"));

        String addr1 = "10.1.1.1-2:1-2,10.1.2.1:1-3,10.1.3.1-3:3,10.1.4.1:1";
        logger.info("convert {} to ", addr1);
        for (String ip : parseAddr(addr1)) {
            logger.info("ip : {}", ip);
        }

        String addr2 = "10.1.1.1:1,10.1.3.1-3:3";
        logger.info("convert {} to ", addr2);
        for (String ip : parseAddr(addr2)) {
            logger.info("ip : {}", ip);
        }

        String addr3 = "10.1.1.1:1,10.1.3.1-3:3,";
        logger.info("convert {} to ", addr3);
        for (String ip : parseAddr(addr3)) {
            logger.info("ip : {}", ip);
        }

        String addr4 = "10.1.1.1:*,10.1.3.1:3,";
        logger.info("convert {} to ", addr4);
        List<String> ipList = parseAddr(addr4);
        logger.info("ip size : {}", ipList.size());
        logger.info("ip[0]: {}", ipList.get(0));
        logger.info("ip[1]: {}", ipList.get(1));
        logger.info("ip[2]: {}", ipList.get(2));
        logger.info("ip[{}]: {}",ipList.size() - 4, ipList.get(ipList.size() - 4));
        logger.info("ip[{}]: {}",ipList.size() - 3, ipList.get(ipList.size() - 3));
        logger.info("ip[{}]: {}",ipList.size() - 2, ipList.get(ipList.size() - 2));
        logger.info("ip[{}]: {}",ipList.size() - 1, ipList.get(ipList.size() - 1));

        String addr5 = "10.1.1.*:1,10.1.3.1:3,";
        logger.info("convert {} to ", addr5);
        List<String> ipList2 = parseAddr(addr5);
        logger.info("ip size : {}", ipList2.size());
        logger.info("ip[0]: {}", ipList2.get(0));
        logger.info("ip[1]: {}", ipList2.get(1));
        logger.info("ip[2]: {}", ipList2.get(2));
        logger.info("ip[{}]: {}",ipList2.size() - 4, ipList2.get(ipList2.size() - 4));
        logger.info("ip[{}]: {}",ipList2.size() - 3, ipList2.get(ipList2.size() - 3));
        logger.info("ip[{}]: {}",ipList2.size() - 2, ipList2.get(ipList2.size() - 2));
        logger.info("ip[{}]: {}",ipList2.size() - 1, ipList2.get(ipList2.size() - 1));

        String ipPatern = "^((25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}:(([1-9])([0-9]){0,3})-(\\1))+$";
        logger.info("addr1 is match {}", addr1.matches(ipPatern));
        logger.info("addr2 is match {}", addr2.matches(ipPatern));
        logger.info("addr3 is match {}", addr3.matches(ipPatern));

    }

    /**
     * Accepts an IPv4 address and returns of string of the form xxx.xxx.xxx.xxx
     * ie 192.168.0.1
     *
     * @param ipAddress
     * @return
     */
    public static String fromIPv4Address(int ipAddress) {
        StringBuffer sb = new StringBuffer();
        int result = 0;
        for (int i = 0; i < 4; ++i) {
            result = (ipAddress >> ((3-i)*8)) & 0xff;
            sb.append(Integer.valueOf(result).toString());
            if (i != 3)
                sb.append(".");
        }
        return sb.toString();
    }

    /**
     * Accepts an IPv4 address of the form xxx.xxx.xxx.xxx, ie 192.168.0.1 and
     * returns the corresponding 32 bit integer.
     * @param ipAddress
     * @return
     */
    public static int toIPv4Address(String ipAddress) {
        if (ipAddress == null)
            throw new IllegalArgumentException("Specified IPv4 address must" +
                "contain 4 sets of numerical digits separated by periods");
        String[] octets = ipAddress.split("\\.");
        if (octets.length != 4)
            throw new IllegalArgumentException("Specified IPv4 address must" +
                "contain 4 sets of numerical digits separated by periods");

        int result = 0;
        for (int i = 0; i < 4; ++i) {
            int oct = Integer.valueOf(octets[i]);
            if (oct > 255 || oct < 0)
                throw new IllegalArgumentException("Octet values in specified" +
                        " IPv4 address must be 0 <= value <= 255");
            result |=  oct << ((3-i)*8);
        }
        return result;
    }

    /*
     * convert "10.1.1.1-2:1-2,10.1.2.1-2:2-3" to list
     *
     *  10.1.1.1-1
     *  10.1.1.1-2
     *  10.1.1.2-1
     *  10.1.1.2-2
     *  10.1.2.1-2
     *  10.1.2.1-3
     *  10.1.2.2-2
     *  10.1.2.2-3
     *
     * convert "10.1.1.1-2:*" to list
     *
     *  10.1.1.1-1024
     *  ....
     *  10.1.1.1-65535
     *  10.1.1.2-1024
     *  ....
     *  10.1.1.2-65535
     *
     * convert "10.1.1.*:*" to list
     *
     *  10.1.1.1-1024
     *  ....
     *  10.1.1.1-65535
     *  10.1.1.2-1024
     *  ....
     *  10.1.1.2-65535
     *  ...
     *  10.1.1.255-1024
     *  ....
     *  10.1.1.255-65535
     */
    private List<String> parseAddr(String addr) {
        ArrayList<String> matchAddr = new ArrayList<String>();
        if (addr.endsWith(",")) {
            addr = addr.substring(0, addr.lastIndexOf(","));
        }
        String []addrList = addr.split(",");
        for (String address : addrList) {
            String []tmpAddr = address.split(":");

            if (tmpAddr.length != 2) {
                logger.warn("address {} isn't vaild, ignore it", address);
                continue;
            }

            String ipBegin = null;
            String ipEnd = null;
            if (tmpAddr[0].endsWith("*")) {
                ipBegin = tmpAddr[0].substring(0, tmpAddr[0].lastIndexOf(".") + 1).concat("0");
                ipEnd = tmpAddr[0].substring(0, tmpAddr[0].lastIndexOf(".") + 1).concat("255");
                logger.info("ipBegin {}, ipEnd {}", ipBegin, ipEnd);
            } else {
                String []ip = tmpAddr[0].split("-");
                if (ip.length == 1) {
                    ipBegin = ip[0];
                    ipEnd = ip[0];
                } else if (ip.length == 2) {
                    ipBegin = ip[0];
                    ipEnd = ipBegin.substring(0, ipBegin.lastIndexOf(".") + 1).concat(ip[1]);
                }
            }

            if (!checkIP(ipBegin, ipEnd)) {
                continue;
            }

            String portBegin = null;
            String portEnd = null;
            if (tmpAddr[1].equals("*")) {
                portBegin = "1024";
                portEnd = "65535";
            } else {
                String []port = tmpAddr[1].split("-");
                if (port.length == 1) {
                    portBegin = port[0];
                    portEnd = port[0];
                } else if (port.length == 2) {
                    portBegin = port[0];
                    portEnd = port[1];
                }
            }

            if (!checkPort(portBegin, portEnd)) {
                continue;
            }

            for (int intIp = toIPv4Address(ipBegin); intIp <= toIPv4Address(ipEnd); intIp++) {
                int intPortBegin = Integer.parseInt(portBegin);
                int intPortEnd = Integer.parseInt(portEnd);
                if (intPortBegin <= intPortEnd) {
                    for (int intPort = intPortBegin; intPort <= intPortEnd; intPort++) {
                        matchAddr.add(new StringBuilder(fromIPv4Address(intIp)).append(DELIMITER).append(intPort).toString());
                    }
                } else {
                    for (int intPort = intPortEnd; intPort <= intPortBegin; intPort++) {
                        matchAddr.add(new StringBuilder(fromIPv4Address(intIp)).append(DELIMITER).append(intPort).toString());
                    }
                }
            }
        }
        return matchAddr;
    }

    private boolean checkIP(String ipBegin, String ipEnd) {
        try {
            if (toIPv4Address(ipBegin) > toIPv4Address(ipEnd)) {
                logger.warn("address {} {} isn't vaild, ignore it", ipBegin, ipEnd);
                return false;
            }
            return true;
        } catch (IllegalArgumentException e) {
            logger.error(e.getMessage());
            return false;
        }
    }

    private boolean checkPort(String portBegin, String portEnd) {
        int intPortBegin = Integer.parseInt(portBegin);
        int intPortEnd = Integer.parseInt(portEnd);
        if (intPortBegin <= 0 || intPortBegin > 65535) {
            return false;
        }
        if (intPortEnd <= 0 || intPortEnd > 65535) {
            return false;
        }
        return true;
    }

    public String getPseudoMacAddress(String ipStr) {
        String []ipArray = ipStr.split("\\.");
        System.out.println("get mac " + ipArray[0]);
        System.out.println("get mac " + ipArray[1]);
        System.out.println("get mac " + ipArray[2]);
        System.out.println("get mac " + ipArray[3]);
        String macStr = new StringBuilder(18)
            .append("42:35:")
            .append(String.format("%02x", Integer.valueOf(ipArray[0]))).append(":")
            .append(String.format("%02x", Integer.valueOf(ipArray[1]))).append(":")
            .append(String.format("%02x", Integer.valueOf(ipArray[2]))).append(":")
            .append(String.format("%02x", Integer.valueOf(ipArray[3])))
            .toString();
        return macStr;
    }

    public void testHttp() {
        new Client().testJson();
    }

    public void testNode() {
        ArrayList<Integer> ips = new ArrayList<Integer>();
        try {
            System.out.println(ConvertIPStrToInt("127.0.0.1") + "==" + 2130706433);
            System.out.println(ConvertIPIntToStr(2130706433) + "==" + "127.0.0.1");
            System.out.println((long)(ConvertIPStrToInt("255.255.255.255")&0xffffffff) );//+ "==" + 4294967295);

            ips.add(ConvertIPStrToInt("192.168.1.1"));
            ips.add(ConvertIPStrToInt("192.168.1.2"));
            ips.add(ConvertIPStrToInt("192.168.1.3"));
            ips.add(ConvertIPStrToInt("192.168.1.4"));
            ips.add(ConvertIPStrToInt("192.168.1.5"));
            ips.add(ConvertIPStrToInt("192.168.1.6"));
            ips.add(ConvertIPStrToInt("192.168.1.7"));
            ips.add(ConvertIPStrToInt("192.168.1.8"));
            ips.add(ConvertIPStrToInt("192.168.1.9"));
            ips.add(ConvertIPStrToInt("192.168.1.10"));
            ips.add(ConvertIPStrToInt("192.168.1.11"));
            ips.add(ConvertIPStrToInt("192.168.1.12"));
            ips.add(ConvertIPStrToInt("192.168.1.13"));
            ips.add(ConvertIPStrToInt("192.168.1.14"));
            ips.add(ConvertIPStrToInt("192.168.1.15"));
            ips.add(ConvertIPStrToInt("192.168.1.16"));
            //ips.add(ConvertIPStrToInt("192.168.1.8"));
        } catch (UnknownHostException e){
            e.printStackTrace();
        }

        //ips.add(19216812);
        //ips.add(19216813);
        Node root = new Node(2);
        Node1 root1 = new Node1(2, 32);
        for (Integer ip: ips) {
            String str = Integer.toBinaryString(ip);
            String zeroStr = "00000000000000000000000000000000";
            str = zeroStr.substring(0, (32-str.length())) + str;
            System.out.println(str + " from " + ip);
            root = insert(root, str, 0);
            root1 = insert(root1, str, 0, 32);
        }

        //HashMap<Integer, ArrayList<Integer>> tree = new HashMap<Integer, ArrayList<Integer>>();
        //dumpNodes(null, 0, tree);
        HashMap<Integer, ArrayList<String>> tree1 = new HashMap<Integer, ArrayList<String>>();
        dumpNodes(root1, 0, tree1);

        //char[] path = new char[32];
        //preOrderTraverse(root1.leftChild, path, '0', 0);
        //preOrderTraverse(root1.rightChild, path, '1', 0);

        String[] path = new String[32];
        preOrderTraverse(root1.leftChild, path, "0", 0);
        preOrderTraverse(root1.rightChild, path, "1", 0);

    }


    public class Node1 {
        private Node1 leftChild;
        private Node1 rightChild;
        private int value;
        private int index;

        Node1 (int data, int index) {
            this.leftChild = null;
            this.rightChild = null;
            this.value = data;
            this.index = index;
        }
    }

    public Node1 insert(Node1 node, String str, int level, int index) {
        if (node == null) {
            node = new Node1(0, index);
        }
        if (level == 32) {
            node.value = 1;
            return node;
        }

        //System.out.println("node.value" + node.value + "node.index:" + node.index);
        if (str.charAt(level) == '0') {
            node.leftChild = insert(node.leftChild, str, level + 1, index - 1);
        } else {
            node.rightChild= insert(node.rightChild, str, level + 1, index + 1);
        }

        if (node.leftChild != null && node.leftChild.value == 1 && node.rightChild != null && node.rightChild.value == 1) {
            node.value = 1;
        }
        return node;
    }

    public void traveNodes(Node1 node, int level, HashMap<Integer, ArrayList<String>> tree) {
        if (node == null) {
            return;
        } else {
            if (tree.get(level) == null) {
                tree.put(level, new ArrayList<String>());
                tree.get(level).add(node.value + ":" + node.index);
            } else {
                tree.get(level).add(node.value + ":" + node.index);
            }
            traveNodes(node.leftChild, level+1, tree);
            traveNodes(node.rightChild, level+1, tree);
        }
    }

    public void dumpNodes(Node1 node, int level, HashMap<Integer, ArrayList<String>> tree) {
        traveNodes(node, level, tree);
        String padding = String.format("%0" + 64 + "d", 0).replace("0"," ");
        for (int dep = 0; dep < 33; dep++) {
            StringBuilder line = new StringBuilder();
            line.append(padding);
            //System.out.println("b" + line.toString() + "end");
            for (String str : tree.get(dep)) {
                String value = str.split(":")[0];
                String index = str.split(":")[1];
                //System.out.println("value:" + value + "index" + index);
                line.setCharAt(Integer.valueOf(index),value.charAt(0));
            }
            System.out.println(line);
        }
    }

    private String binToIP(String binIP) {
        //StringBuilder strIP = new StringBuilder();
        //strIP.append(Integer.valueOf(binIP.substring(0, 8), 2) & 0xff).append(".")
        //     .append(Integer.valueOf(binIP.substring(8, 16), 2) & 0xff).append(".")
        //     .append(Integer.valueOf(binIP.substring(16, 24), 2) & 0xff).append(".")
        //     .append(Integer.valueOf(binIP.substring(24, 32), 2) & 0xff);
        //return strIP.toString();
        String strIP = new String();
        //System.out.println(binIP + Integer.valueOf("000001001", 2));
        strIP = String.valueOf(Integer.valueOf(binIP.substring(0, 8), 2) & 0xff) + ".";
        strIP += String.valueOf(Integer.valueOf(binIP.substring(8, 16), 2) & 0xff) + ".";
        strIP += String.valueOf(Integer.valueOf(binIP.substring(16, 24), 2) & 0xff) + ".";
        strIP += String.valueOf(Integer.valueOf(binIP.substring(24, 32), 2) & 0xff);
        return strIP;
    }

    public void preOrderTraverse(Node1 node,char[] path, char value, int level) {
        if (node == null || level == 32) {
            return;
        }
        path[level] = value;
        if (node.value == 1) {
            //String padding = String.format("%0" + 32 + "d", 0).replace("0","0");
            String zeroStr="00000000000000000000000000000000";
            //System.out.println(String.valueOf(path) + level);
            //System.out.println(zeroStr.substring(0, 32 - level));
            String ip = binToIP(String.valueOf(Arrays.copyOf(path, level)) + zeroStr.substring(0, 31 - level));
            System.out.println(ip);
            return;
        }
        preOrderTraverse(node.leftChild, path, '0', level + 1);
        preOrderTraverse(node.rightChild, path, '1', level + 1);
    }

    public void preOrderTraverse(Node1 node,String[] path, String value, int level) {
        if (node == null || level == 32) {
            return;
        }
        path[level] = value;
        //System.out.println("path[level]" + value + "level " + level);
        if (node.value == 1) {
            //String padding = String.format("%0" + 32 + "d", 0).replace("0","0");
            String t = "";
            for (int i = 0; i <= level; i++) {
                t += path[i];
            }
            String zeroStr="00000000000000000000000000000000";
            //System.out.println(String.valueOf(path) + level);
            System.out.println("level " + level + " " + t);
            //System.out.println(zeroStr.substring(0, 32 - level));
            String ip = binToIP(t + zeroStr.substring(0, 31 - level)) + "/" + (level+1);
            System.out.println(ip);
            return;
        }
        preOrderTraverse(node.leftChild, path, "0", level + 1);
        preOrderTraverse(node.rightChild, path, "1", level + 1);
    }




    public class Node {
        Node leftChild;
        Node rightChild;
        int value;

        Node (int data) {
            leftChild = null;
            rightChild = null;
            value = data;
        }
    }

    public Node insert(Node node, String str, int level) {
        if (node == null) {
            node = new Node(0);
        }
        if (level == 32) {
            node.value = 1;
            return node;
        }

        if (str.charAt(level) == '0') {
            node.leftChild = insert(node.leftChild, str, level+1);
        } else {
            node.rightChild= insert(node.rightChild, str, level+1);
        }

        if (node.leftChild != null && node.leftChild.value == 1 && node.rightChild != null && node.rightChild.value == 1) {
            node.value = 1;
        }
        return node;
    }

    public void traveNodes(Node node, int level, HashMap<Integer, ArrayList<Integer>> tree) {
        if (node == null) {
            if (tree.get(level) == null) {
                tree.put(level, new ArrayList<Integer>());
                tree.get(level).add(2);
            } else {
                tree.get(level).add(2);
            }
            return;
        } else {
            if (tree.get(level) == null) {
                tree.put(level, new ArrayList<Integer>());
                tree.get(level).add(node.value);
            } else {
                tree.get(level).add(node.value);
            }
            traveNodes(node.leftChild, level+1, tree);
            traveNodes(node.rightChild, level+1, tree);
        }
    }

    public void dumpNodes(Node node, int level, HashMap<Integer, ArrayList<Integer>> tree) {
        traveNodesV1(node, level, tree);
        int depth = 7;
        int len = 1 << depth;
        String padding = String.format("%0" + len + "d", 0).replace("0"," ");
        System.out.println("str:" + padding.length() + "end");
        for (int dep = 0; dep < depth - 1; dep++) {
            StringBuilder line = new StringBuilder();
            line.append(padding.substring(0, (1 << (depth - dep - 2)) - 1));
            for (Integer i:tree.get(dep)) {
                line.append(i);
                line.append(padding.substring(0, (1 << (depth - dep - 1)) - 1));
            }
            System.out.println(line);
        }
    }

    public void traveNodesV1(Node node, int level, HashMap<Integer, ArrayList<Integer>> tree) {
        if (node == null) {
            if (tree.get(level) == null) {
                tree.put(level, new ArrayList<Integer>());
                tree.get(level).add(2);
            } else {
                tree.get(level).add(2);
            }
            //return;
            if (level == 5) {
                return;
            }
            traveNodes(null, level+1, tree);
            traveNodes(null, level+1, tree);
        } else {
            if (tree.get(level) == null) {
                tree.put(level, new ArrayList<Integer>());
                tree.get(level).add(node.value);
            } else {
                tree.get(level).add(node.value);
            }
            if (level == 5) {
                return;
            }
            traveNodes(node.leftChild, level+1, tree);
            traveNodes(node.rightChild, level+1, tree);
        }
    }

    private int pack(byte[] bytes) {
      int val = 0;
      for (int i = 0; i < bytes.length; i++) {
          val <<= 8;
          val |= bytes[i] & 0xff;
        }
      return val;
    }

    // dottedStrin as 127.0.0.1
    public int ConvertIPStrToInt(String dottedString) throws UnknownHostException {
        //return pack(InetAddress.getByName(dottedString).getAddress());
        return ByteBuffer.wrap(InetAddress.getByName(dottedString).getAddress()).getInt();
    }

    private byte[] unpack(int bytes) {
      return new byte[] {
          (byte)((bytes >>> 24) & 0xff),
          (byte)((bytes >>> 16) & 0xff),
          (byte)((bytes >>>  8) & 0xff),
          (byte)((bytes       ) & 0xff)
        };
    }

    public String ConvertIPIntToStr(int packedBytes) throws UnknownHostException {
        //return InetAddress.getByAddress(unpack(packedBytes)).getHostAddress();
        return InetAddress.getByName(String.valueOf(packedBytes)).getHostAddress();
    }

    public void lightWeightCmd(String cmdStr) {
        for (int i = 0; i < 10000; i++) {
            ProcessBuilder processBuilder = new ProcessBuilder();
            Process process = null;
            StringBuilder output = new StringBuilder(10);
            processBuilder.command("/bin/bash", "-c", cmdStr);
            //logger.debug("execute {} {}", "/bin/bash -c ", cmdStr);
            processBuilder.redirectErrorStream(true);
            try {
                process = processBuilder.start();
                byte[] bytes = new byte[10];
                BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line ;
                while ((line = br.readLine()) != null) {
                    output.append(line);
                }
                process.waitFor();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (process != null) {
                int exitValue = process.exitValue();
                if (exitValue != 0) {
                    logger.error("execute {} with error {}", cmdStr, output);
                } else {
                    //if (logger.isDebugEnabled()) {
                    //    logger.debug("execute {} with output {}", cmdStr, output);
                    //}
                }
            }
        }
    }

}
