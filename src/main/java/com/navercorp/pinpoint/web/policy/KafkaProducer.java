package com.navercorp.pinpoint.web.policy;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Properties;

@Service
public class KafkaProducer {

    @Autowired
    private PolicyEventService policyEventService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Scheduled(cron = "15/59 * * * * *")
    public void sendPolicyEventMessage() {
        final Producer<String, String> producer = getProducer();
        long timestamp = System.currentTimeMillis() - 1000 * 60;
        List<String> policyMessages = policyEventService.buildMessage(timestamp);
        logger.info("begin sending........");
        for (String policyMessage : policyMessages) {
            final KeyedMessage<String, String> keyedMessage = new KeyedMessage<>(KafkaProperties.topic, policyMessage);
            logger.debug("Send message: " + keyedMessage);
            producer.send(keyedMessage);
        }
        producer.close();
    }

    private Producer<String, String> getProducer() {
        Properties properties = new Properties();
        properties.put("zookeeper.connect", KafkaProperties.zkConnect);
        properties.put("metadata.broker.list", KafkaProperties.metadata_broker);
        properties.put("serializer.class", KafkaProperties.serializer_class);
        properties.put("request.required.acks", "0");
        properties.put("producer.type", "async");
        ProducerConfig producerConfig = new ProducerConfig(properties);

        logger.info("zookeeper.connect: {}", KafkaProperties.zkConnect);
        logger.info("metadata.broker.list: {}", KafkaProperties.metadata_broker);

        return new Producer<>(producerConfig);
    }
}