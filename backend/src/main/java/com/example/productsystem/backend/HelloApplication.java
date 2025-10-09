package com.example.productsystem.backend;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

/*
 * Класс конфигурации JAX-RS приложения.
 * Устанавливает базовый путь для всех REST эндпоинтов.
 */
@ApplicationPath("/api")
public class HelloApplication extends Application {

}