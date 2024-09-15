package com.mensageria.ProducerConsumer;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

public class Cliente {

    private static final String TOPIC_REQUEST = "CompraRequest";
    private static final String TOPIC_RESPONSE = "CompraResponse";
    private static final String BOOTSTRAP_SERVERS = "localhost:9092";
    private static final String GROUP_ID = "cliente-grupo";
    private static final List<String> respostasRecebidas = new ArrayList<>();

    public static void main(String[] args) {
        JFrame frame = new JFrame("Sistema de Compra");
        frame.setSize(600, 250);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTextField textoCompra = new JTextField(20);
        JLabel resultadoLabel = new JLabel("Status da Compra: Aguardando...");
        JLabel statusLabel = new JLabel("Resultado: ");

        JButton enviarButton = new JButton("Enviar Compra");
        JButton limparButton = new JButton("Limpar Campos");
        JButton verRespostasButton = new JButton("Ver Todas as Respostas");

        JPanel botoesPanel = new JPanel();
        botoesPanel.setLayout(new FlowLayout());
        botoesPanel.add(enviarButton);
        botoesPanel.add(limparButton);
        botoesPanel.add(verRespostasButton);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("Digite o pedido:"));
        inputPanel.add(textoCompra);
        inputPanel.add(statusLabel);

        panel.add(inputPanel, BorderLayout.CENTER);
        panel.add(botoesPanel, BorderLayout.SOUTH);
        panel.add(resultadoLabel, BorderLayout.NORTH);

        frame.add(panel);
        frame.setVisible(true);

        enviarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String detalhesCompra = textoCompra.getText();
                if (!detalhesCompra.isEmpty()) {
                    enviarCompra(detalhesCompra);
                    statusLabel.setText("Enviando pedido...");
                }
            }
        });

        limparButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textoCompra.setText("");
                statusLabel.setText("Resultado:");
                resultadoLabel.setText("Status da Compra: Aguardando...");
            }
        });

        verRespostasButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!respostasRecebidas.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, String.join("\n", respostasRecebidas), "Respostas Recebidas", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(frame, "Nenhuma resposta recebida ainda.", "Respostas Recebidas", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        new Thread(() -> receberResposta(statusLabel, resultadoLabel)).start();
    }

    private static void enviarCompra(String detalhes) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        try (KafkaProducer<String, String> producer = new KafkaProducer<>(props)) {
            ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC_REQUEST, detalhes);
            producer.send(record);
            System.out.println("Compra enviada: " + detalhes); // Verificação
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void receberResposta(JLabel statusLabel, JLabel resultadoLabel) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)) {
            consumer.subscribe(Collections.singletonList(TOPIC_RESPONSE));

            while (true) {
                consumer.poll(java.time.Duration.ofMillis(100)).forEach(record -> {
                    String resposta = record.value();
                    System.out.println("Resposta recebida: " + resposta); // Verificação

                    respostasRecebidas.add(resposta);

                    SwingUtilities.invokeLater(() -> {
                        resultadoLabel.setText("Status da Compra: " + (resposta.contains("aprovada") ? "Aprovada" : "Rejeitada"));
                        statusLabel.setText("Resultado: " + resposta);
                    });
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
