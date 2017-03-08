package com.navercorp.pinpoint.web.dao.elasticsearch;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;

/**
 * Created by root on 2/21/17.
 */
public class ESBaseDao {
    public Client getClient() {
        Client client = null;
        try {
            Settings settings = Settings.settingsBuilder()
                    .put("cluster.name", "xelk1")
                    .put("network.host", "10.62.100.142")
                    .put("client.transport.ping_timeout", "120s")
                    .put("node.name", "node-client").build();

            client = TransportClient.builder().settings(settings).build()
                    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("10.62.100.142"), 9300));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return client;
    }


    public Long XLong(Object object) {
        if(object instanceof Integer) {
            return ((Integer) object).longValue();
        }

        return (Long) object;
    }

    public long timeParse(String time) {
        long retTime = -1L;
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS Z");

            String sDate = time.replace("Z", " UTC");
            retTime = format.parse(sDate).getTime();
        } catch (Exception ex) {
            System.out.println(ex);
        }


        return retTime;

    }
}
