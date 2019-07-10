# Storage

## Connection Minio
## Notification RabbitMQ


## Gradle

### Build without test 

gradle build -x test

### Build test

gradle build test

### Run

gradle bootRun

## Docker 

### Create image
gradle build docker -x test

### Run container
docker-compose up -d

## URL
http://${host}:${port}/swagger-ui.html
