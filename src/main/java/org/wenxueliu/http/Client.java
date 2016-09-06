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
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.entity.StringEntity;
import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
import org.apache.http.HttpStatus;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.entity.ContentType;

import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;


/*
 * demo for apache httpclient
 */

public class Client {

    CloseableHttpClient httpClient;

    public Client() {
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(100);

        this.httpClient = HttpClients.custom()
                .setConnectionManager(cm)
                .build();
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
                    System.out.println("IOException " + e.getMessage());
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
                    System.out.println("IOException " + e.getMessage());
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
                    System.out.println("IOException " + e.getMessage());
                }
            }
        }
    }

    public String post(String url, String path, List<BasicNameValuePair> pairList) {
        CloseableHttpResponse res = null;
        String response = null;
        try {
            HttpPost httpPost = new HttpPost(url + path);
            UrlEncodedFormEntity s = new UrlEncodedFormEntity(pairList, "utf-8");
            httpPost.setEntity(s);

            res = httpClient.execute(httpPost);
            if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = res.getEntity();
                Charset charset = ContentType.getOrDefault(entity).getCharset();
                response = EntityUtils.toString(entity, charset);
                System.out.println(response);
                EntityUtils.consume(s);
                EntityUtils.consume(entity);
                System.out.println(response);
                return response;
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
                    System.out.println("IOException " + e.getMessage());
                }
            }
        }
        return response;
    }

    public String post(String url, String path, String postBody) {
        CloseableHttpResponse res = null;
        String response = null;
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
                response = EntityUtils.toString(entity, charset);
                EntityUtils.consume(s);
                EntityUtils.consume(entity);
                System.out.println(response);
            }
        } catch (UnsupportedEncodingException e) {
            System.out.println("UnsupportedEncodingException " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IOException " + e.getMessage());
        } finally {
            if (res != null) {
                try {
                    res.close();
                } catch (IOException e) {
                    System.out.println("IOException " + e.getMessage());
                }
            }
        }
        return response;
    }

    public void delete(String url, String path) {
        CloseableHttpResponse res = null;
        try {
            HttpDelete httpDelete = new HttpDelete(url + path);

            res = httpClient.execute(httpDelete);
            if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = res.getEntity();
                Charset charset = ContentType.getOrDefault(entity).getCharset();
                System.out.println(EntityUtils.toString(entity, charset));
                EntityUtils.consume(entity);
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
                    System.out.println("IOException " + e.getMessage());
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

    public String genRegister(String id, String moduleName) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode obj = mapper.createObjectNode();
        obj.put("id", id);
        obj.put("name", moduleName);
        obj.toString();
        return obj.toString();
    }

    public RetBody signin(String type) {
        String res = post("http://10.9.1.41:10000/", "/uap/v0.3/modulemanagement/module/" + type, genRegister("module_lbl4", "controller"));
        return toRetBody(res);
    }

    private RetBody toRetBody(String body) {
        ObjectMapper mapper = new ObjectMapper();
        RetBody item = new RetBody();

        System.out.println("response" + res);
        if (res == null) {
            return null;
        }
        JsonNode tree = null;
        try {
            tree = mapper.readTree(res);
        } catch (IOException e) {
            System.out.println("IOException " + e);
            return null;
        }

        JsonNode code = tree.path("code");
        if (code.isMissingNode()) {
            //LOG error
        } else {
            item.code = code.asInt();
        }
        if (item.code >= 0) {
            JsonNode message = tree.path("message");
            if (!message.isMissingNode()) {
                item.message = message.asText();
            } else {
                //LOG ERROR
            }
            JsonNode result = tree.path("result");
            if (result.isMissingNode()) {
                return item;
            }

            JsonNode name = result.path("name");
            if (!name.isMissingNode() && name.isTextual()) {
                item.result.name = name.asText();
            }
            JsonNode id = result.path("id");
            if (!id.isMissingNode() && id.isTextual()) {
                item.result.id = id.asText();
            }
            JsonNode priority = result.path("priority");
            if (!priority.isMissingNode() && priority.isTextual()) {
                item.result.priority = priority.asText();
            }
            JsonNode status = result.path("status");
            if (!status.isMissingNode() && status.isTextual()) {
                item.result.status = status.asText();
            }
            JsonNode register_expire = result.path("register_expire");
            if (!register_expire.isMissingNode() && register_expire.isTextual()) {
                item.result.register_expire = register_expire.asText();
            }
            JsonNode signup_expire = result.path("signup_expire");
            if (!signup_expire.isMissingNode() && signup_expire.isTextual()) {
                item.result.signup_expire = signup_expire.asText();
            }
            JsonNode last_register_time = result.path("last_register_time");
            if (!last_register_time.isMissingNode() && last_register_time.isTextual()) {
                item.result.last_register_time = signup_expire.asText();
            }
            JsonNode last_signup_time = result.path("last_signup_time");
            if (!last_signup_time.isMissingNode() && last_signup_time.isTextual()) {
                item.result.last_signup_time = last_signup_time.asText();
            }
        } else {
            JsonNode message = tree.path("message");
            if (!message.isMissingNode() && message.isTextual()) {
                item.message = message.asText();
            } else {
                //LOG ERROR
            }
            JsonNode cause = tree.path("cause");
            if (!cause.isMissingNode() && cause.isTextual()) {
                item.cause = cause.asText();
            } else {
                //LOG ERROR
            }
        }
        return item;
    }

    public void setRetBody(RetBody item, String key, String value) {
        if (key.trim().isEmpty() || value.trim().isEmpty()) {
            return;
        }
	    if (key.equals("code")) {
	        item.code = Integer.parseInt(value);
	        return;
	    }
	    if (key.equals("cause")) {
	        item.cause = value;
	        return;
	    }
	    if (key.equals("message")) {
	        item.message= value;
	        return;
	    }
    }

    public void testJson() {
        //System.out.println(genJsonClusterId("1234"));
        //put("http://127.0.0.1:8080/", "wm/cluster/self", genJsonClusterId("1234"));
        //get("http://127.0.0.1:8080/", "wm/lb/cluster/json");
        //delete("http://127.0.0.1:8080/", "wm/lb/cluster/json");
        //get("http://127.0.0.1:8080/", "wm/lb/cluster/json");
        System.out.println(signin("register"));
        System.out.println(signin("signup"));
    }

    public class RetBody {
        public int code;
        public String cause;
        public String message;
        public Result result = new Result();
    }

    public class Result {
        public String id;
        public String name;
        public String priority;
        public String status;
        public String register_expire;
        public String signup_expire;
        public String last_register_time;
        public String last_signup_time;
    }
}
