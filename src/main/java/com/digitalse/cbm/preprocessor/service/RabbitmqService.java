package com.digitalse.cbm.preprocessor.service;

import java.io.IOException;

import com.digitalse.cbm.rabbitmqtransferenceobjects.RTOBucket;

import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

public class RabbitmqService {
    @Autowired
    private PreprocessService ps;

    @Autowired
    private RabbitTemplate rt;

    public void sendImage(RTOBucket rtoBucket) throws AmqpException, IOException {
        rt.convertAndSend("ocr", rtoBucket);
    }

    @RabbitListener(queues = "preprocessor")
    public void listener(RTOBucket rtoBucket) throws IOException {
        rtoBucket = ps.preProcess(rtoBucket);
        sendImage(rtoBucket);
    }
}
