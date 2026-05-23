@echo off
echo ===================================================================
echo Інсталяція та запуск платформи корпоративного навчання «LMS-API»
echo ===================================================================
echo [Крок 1] Компіляція коду та пакування артефакту...
call .\mvnw clean package -DskipTests

echo [Крок 2] Запуск інфраструктури (PostgreSQL, RabbitMQ, App) у Docker...
docker compose down
docker compose up --build
pause