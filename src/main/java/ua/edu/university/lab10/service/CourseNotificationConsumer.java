package ua.edu.university.lab10.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import ua.edu.university.lab10.config.RabbitMqConfig;

@Component
@Slf4j
public class CourseNotificationConsumer {

    // Аннотация указывает Spring автоматически слушать целевую очередь
    @RabbitListener(queues = RabbitMqConfig.COURSE_QUEUE)
    public void consumeCourseNotification(String message) {
        log.info("<== [Consumer] Сообщение успешно получено из RabbitMQ!");
        log.info("[ФОНОВАЯ ЗАДАЧА] Надсилаємо email-сповіщення про новий курс... Текст события: {}", message);

        // Здесь в реальном продакшене вызывается почтовый сервис JavaMailSender
    }
}