package com.navercorp.pinpoint.web.policy;

public interface KafkaProperties {
    String kafkaIp = System.getenv("Kafka_IP") == null ? "127.0.0.1" : System.getenv("Kafka_IP");
    String kafkaPort = System.getenv("Kafka_ZK") == null ? "2181" : System.getenv("Kafka_ZK");
    String zkConnect = kafkaIp + ":" + kafkaPort;
    String topic = "policy_event";
    String metadata_broker = kafkaIp + ":9092";
    String serializer_class = "kafka.serializer.StringEncoder";
}