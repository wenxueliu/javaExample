package org.wenxueliu.http;

import java.util.List;
import java.io.StringWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;


import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.entity.StringEntity;
import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
import org.apache.http.HttpStatus;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.entity.ContentType;

/*
 * demo for apache httpclient
 */

public class Client {

    CloseableHttpClient httpClient;

    public Client() {
        this.httpClient = HttpClients.createDefault();
    }

    public HttpEntity get(String url, String path) {
        HttpGet httpGet = new HttpGet(url + path);
        CloseableHttpResponse res = null;
        HttpEntity entity = null;
        try {
            res = this.httpClient.execute(httpGet);
            if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                entity = res.getEntity();
                Charset charset = ContentType.getOrDefault(entity).getCharset();
                System.out.println(EntityUtils.toString(entity, charset));
            } else {
                System.out.println(res.getStatusLine());
            }
        } catch (UnsupportedEncodingException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            if (res != null) {
                try {
                    res.close();
                } catch (IOException e) {
                }
            }
        }
        return entity;
    }

    public void put(String url, String path, List<BasicNameValuePair> pairList) {
        HttpPut httpPut = new HttpPut(url + path);
        CloseableHttpResponse res = null;
        try {
            UrlEncodedFormEntity s = new UrlEncodedFormEntity(pairList, "utf-8");
            httpPut.setEntity(s);

            res = httpClient.execute(httpPut);
            if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = res.getEntity();
                Charset charset = ContentType.getOrDefault(entity).getCharset();
                System.out.println(EntityUtils.toString(entity, charset));
                EntityUtils.consume(s);
            }
        } catch (UnsupportedEncodingException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            if (res != null) {
                try {
                    res.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public void put(String url, String path, String postBody) {
        HttpPut httpPut = new HttpPut(url + path);
        CloseableHttpResponse res = null;
        try {
            StringEntity s = new StringEntity(postBody);
            s.setContentEncoding("UTF-8");
		    s.setContentType("application/json");
            httpPut.setEntity(s);

            res = httpClient.execute(httpPut);
            if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = res.getEntity();
                Charset charset = ContentType.getOrDefault(entity).getCharset();
                System.out.println(EntityUtils.toString(entity, charset));
                EntityUtils.consume(s);
            }
        } catch (UnsupportedEncodingException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            if (res != null) {
                try {
                    res.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public void post(String url, String path, List<BasicNameValuePair> pairList) {
        CloseableHttpResponse res = null;
        try {
            HttpPost httpPost = new HttpPost(url + path);
            UrlEncodedFormEntity s = new UrlEncodedFormEntity(pairList, "utf-8");
            httpPost.setEntity(s);

            res = httpClient.execute(httpPost);
            if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = res.getEntity();
                Charset charset = ContentType.getOrDefault(entity).getCharset();
                System.out.println(EntityUtils.toString(entity, charset));
                EntityUtils.consume(s);
            }
        } catch (UnsupportedEncodingException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            if (res != null) {
                try {
                    res.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public void post(String url, String path, String postBody) {
        CloseableHttpResponse res = null;
        try {
            HttpPost httpPost = new HttpPost(url + path);
            StringEntity s = new StringEntity(postBody);
            s.setContentEncoding("UTF-8");
		    s.setContentType("application/json");
            httpPost.setEntity(s);

            res = httpClient.execute(httpPost);
            if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = res.getEntity();
                Charset charset = ContentType.getOrDefault(entity).getCharset();
                System.out.println(EntityUtils.toString(entity, charset));
                EntityUtils.consume(s);
            }
            //List <NameValuePair> nvps = new ArrayList <NameValuePair>();
            //nvps.add(new BasicNameValuePair("username", "vip"));
            //nvps.add(new BasicNameValuePair("password", "secret"));
            //httpPost.setEntity(new UrlEncodedFormEntity(nvps));
        } catch (UnsupportedEncodingException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            if (res != null) {
                try {
                    res.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public String genJsonClusterId(String clusterId) {
        JsonFactory jf = new JsonFactory();
        StringWriter writer = new StringWriter();
        try {
            JsonGenerator jg = jf.createGenerator(writer);
            jg.writeStartObject();
            jg.writeFieldName("id");
            jg.writeString(clusterId);
            jg.writeEndObject();
            jg.close();
            return writer.toString();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public void testJson() {
        System.out.println(genJsonClusterId("1234"));
        put("http://127.0.0.1:8080/", "vm/lb/cluster/json", genJsonClusterId("1234"));
        get("http://127.0.0.1:8080/", "vm/lb/cluster/json");
    }
}
