package com.navercorp.pinpoint.web.dao.elasticsearch;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.squareup.okhttp.*;

import java.util.concurrent.TimeUnit;

public class PhoenixHttpProxy {
    public final static String URL= "http://10.62.100.139:8086/phoenixolap/api/v1/query";

    public ESQueryResult getResult(String url,ESQueryCond esQueryCond) {
        ESQueryResult esQueryResult = null;
        try {
            OkHttpClient client = new OkHttpClient();
            client.setConnectTimeout(1, TimeUnit.MINUTES);
            client.setReadTimeout(5, TimeUnit.MINUTES);

            //ToDo
            String jsonData = "{agentId='cm-agent11',startTime=1488384000000,from=1,to=2}";;

            MediaType MEDIA_TYPE_JSON = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");
            System.out.println(jsonData);
            com.squareup.okhttp.RequestBody body = com.squareup.okhttp.RequestBody.create(MEDIA_TYPE_JSON, jsonData);

            Request request = new Request.Builder().post(body).url(url).build();

            Response response = client.newCall(request).execute();
            String retJson = response.body().string();
            ObjectMapper mapper = new ObjectMapper();
            SimpleModule module = new SimpleModule();

            module.addDeserializer(ESQueryResult.class, new ESQueryResultDeSerializer());
            mapper.registerModule(module);

            esQueryResult = mapper.readValue(retJson,ESQueryResult.class);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return esQueryResult;
    }


}
