package com.mensageria.ProducerConsumer;

import java.util.Properties;
import java.util.Scanner;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Collections;

public class Servidor {

    private static final String TOPIC_REQUEST = "CompraRequest";
    private static final String TOPIC_RESPONSE = "CompraResponse";
    private static final String BOOTSTRAP_SERVERS = "localhost:9092";
    private static final String GROUP_ID = "servidor-grupo";

    public static void main(String[] args) {
        Properties consumerProps = new Properties();
        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        Properties producerProps = new Properties();
        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(consumerProps);
             KafkaProducer<String, String> producer = new KafkaProducer<>(producerProps)) {

            consumer.subscribe(Collections.singletonList(TOPIC_REQUEST));

            while (true) {
                consumer.poll(java.time.Duration.ofMillis(100)).forEach(record -> {
                    String compraDetails = record.value();
                    System.out.println("Recebido pedido: " + compraDetails);

                    // Simular o processamento da compra
                    String status = processarCompra(compraDetails);

                    // Enviar resposta de volta ao cliente
                    ProducerRecord<String, String> responseRecord = new ProducerRecord<>(TOPIC_RESPONSE, status);
                    producer.send(responseRecord);
                    System.out.println("Resposta enviada: " + status);
                });
            }
        }
    }

    private static String processarCompra(String detalhes) {
        // "Processamento de compra pelo servidor"
        System.out.println("Digite 1 para aprovar a compra, e 0 para rejeitar o pedido : " + detalhes);
        Scanner scan = new Scanner(System.in);
        String s = scan.nextLine();
        String resposta;
        if (s.equals("1")) {
            resposta = detalhes + "  aprovada";
        } else {
            resposta = detalhes + "  rejeitada";
        }
        return resposta;
    }
}
