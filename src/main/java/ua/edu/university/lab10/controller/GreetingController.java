package ua.edu.university.lab10.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Первый REST-контроллер приложения.
 */
@RestController
public class GreetingController {

    // 1. Простой GET-ендпоінт, що повертає стабільний рядок
    @GetMapping("/hello")
    public String sayHello() {
        return "Hello, World!";
    }

    // 2. Персоналізований GET-ендпоінт, що приймає параметр запиту (@RequestParam)
    @GetMapping("/greet")
    public String sayPersonalGreeting(@RequestParam(value = "name", defaultValue = "Guest") String name) {
        return "Hello, " + name + "!";
    }
}