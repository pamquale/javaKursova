package ua.edu.university.lab10.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import ua.edu.university.lab10.config.RabbitMqConfig;
import ua.edu.university.lab10.dto.CourseDto;

@Component
@RequiredArgsConstructor
@Slf4j
public class CourseNotificationProducer {

    private final RabbitTemplate rabbitTemplate;

    public void sendCourseCreationNotification(CourseDto courseDto) {
        String message = String.format("Новий курс: '%s', Інструктор: %s", courseDto.getTitle(), courseDto.getInstructor());
        log.info("==> [Producer] Отправка сообщения в очередь RabbitMQ: {}", message);

        // Асинхронная отправка сообщения в очередь
        rabbitTemplate.convertAndSend(RabbitMqConfig.COURSE_QUEUE, message);
    }
}