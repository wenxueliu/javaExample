package org.wenxueliu.config;

import java.io.InputStream;
import java.net.URL;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.FileNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Resource {

    private static Logger LOG = LoggerFactory.getLogger(Resource.class);

    /*
     * 相对于 classpath 路径
     */
    public void getResourceByClassLoader(String path) {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(path);
        if (is == null) {
            LOG.info("getResourceByClassLoader load {} error", path);
            return;
        } else {
            LOG.info("getResourceByClassLoader load {} successfully", path);
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String s = null;
        try {
            while ((s = br.readLine()) != null) {
                LOG.info("read line {} ", s);
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * 相对于当前类路径
     *
     */
    public void getResourceByClass(String path) {
        InputStream is = this.getClass().getResourceAsStream(path);
        if (is == null) {
            LOG.info("getResourceByClass load {} error", path);
            return;
        } else {
            LOG.info("getResourceByClass load {} successfully", path);
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String s = null;
        try {
            while ((s = br.readLine()) != null) {
                LOG.info("read line {} ", s);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * 只能获取路径
     *
     */
    public void getAbsoluateResource(String path) {
        URL fileURL = this.getClass().getResource(path);
        if (fileURL == null) {
            LOG.info("getAbsoluateResource load {} error", path);
            return;
        } else {
            LOG.info("getAbsoluateResource load {} successfully", path);
        }
        LOG.info(fileURL.getFile());
    }

    /*
     * IDE 才有用, 基于项目根路径
     *
     */
    public void getRelativeResource(String path) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            String s = null;
            LOG.info("getRelativeResource load {} successfully", path);
            while ((s = br.readLine()) != null) {
                LOG.info("read line {} ", s);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void test() {
        Resource r = new Resource();
        r.getResourceByClassLoader("test.properties");
        r.getResourceByClassLoader("/resources/test.properties");
        r.getResourceByClassLoader("/src/main/resources/test.properties");
        r.getResourceByClass("resources.properties");
        r.getResourceByClass("test.properties");
        r.getResourceByClass("/org/wenxueliu/config/resources.properties");
        r.getResourceByClass("/resources/test.properties");
        r.getResourceByClass("resources/test.properties");
        r.getResourceByClass("main/resources/test.properties");
        r.getResourceByClass("/main/resources/test.properties");
        r.getAbsoluateResource("/");
        r.getRelativeResource("src/main/resources/test.properties");
    }
}
