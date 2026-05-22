package ua.edu.university.lab10.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    public static final String COURSE_QUEUE = "course-notifications-queue";

    @Bean
    public Queue courseQueue() {
        // Создаем персистентную (durable) очередь, которая не пропадет при перезапуске RabbitMQ
        return new Queue(COURSE_QUEUE, true);
    }
}