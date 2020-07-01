package io.treasure.utils;

import com.gexin.fastjson.JSON;
import org.apache.http.Consts;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Test;
import org.springframework.boot.configurationprocessor.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

public class PushTest {


    @Test
    public void test(){
        getToken(null,null,null);
    }


    public String getToken(String domain, String clientId, String clientSecret) {
        String token = null;
        try {
            HttpPost post = new HttpPost(domain + "/oauth2/v1/token");
            JSONObject keyString = new JSONObject();
            keyString.put("client_id", "18893***83957248");
            keyString.put("client_secret", "B15B497B44E080EBE2C4DE4E74930***52409516B2A1A5C8F0FCD2C579A8EB14");
            keyString.put("grant_type", "client_credentials");
            StringEntity entity = new StringEntity(keyString.toString(), Charset.forName("UTF-8"));
            entity.setContentEncoding("UTF-8");
            entity.setContentType("application/json");
            post.setEntity(entity);
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpResponse response = httpClient.execute(post);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                BufferedReader br =
                        new BufferedReader(new InputStreamReader(response.getEntity().getContent(), Consts.UTF_8));
                String result = br.readLine();
                System.out.println("getToken:"+result);

//                JSONObject object = JSON.parseObject(result);
//                token = object.getString("access_token");
            }
            post.releaseConnection();
            httpClient.close();
        } catch (Exception e) {
        }
        return token;
    }
}
