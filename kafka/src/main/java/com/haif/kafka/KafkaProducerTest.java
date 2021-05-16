package com.haif.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

/**
 * @author haif.
 * @date 2021/5/15 12:54
 */
public class KafkaProducerTest {

    public static void main(String[] args) {
        Properties properties = new Properties();
        properties.put("key.serializer","org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer","org.apache.kafka.common.serialization.StringSerializer");
        properties.put("bootstrap.servers", "192.168.40.134:9092");

        // 配置生产者实例
        KafkaProducer<String, String> producer = new KafkaProducer<>(properties);

        // 构建消息
        ProducerRecord<String, String> record = new ProducerRecord<>("topic-demo", "hello kafka");

        // 发送消息
        producer.send(record);

        producer.close();
    }
}
