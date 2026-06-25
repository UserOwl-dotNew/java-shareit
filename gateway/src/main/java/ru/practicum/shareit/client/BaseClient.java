package ru.practicum.shareit.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Component
public class BaseClient {

    private final RestTemplate restTemplate;

    public BaseClient(@Value("${shareit-server.url:http://server:9090}") String serverUrl) {
        this.restTemplate = new RestTemplateBuilder()
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                .build();
    }

    public ResponseEntity<Object> post(String path, Long userId, Object body) {
        return sendRequest(HttpMethod.POST, path, userId, body);
    }

    public ResponseEntity<Object> post(String path, Object body) {
        return sendRequest(HttpMethod.POST, path, null, body);
    }

    public ResponseEntity<Object> patch(String path, Long userId, Object body) {
        return sendRequest(HttpMethod.PATCH, path, userId, body);
    }

    public ResponseEntity<Object> patch(String path, Long userId) {
        return sendRequest(HttpMethod.PATCH, path, userId, null);
    }

    public ResponseEntity<Object> get(String path, Long userId) {
        return sendRequest(HttpMethod.GET, path, userId, null);
    }

    public ResponseEntity<Object> get(String path) {
        return sendRequest(HttpMethod.GET, path, null, null);
    }

    public ResponseEntity<Object> delete(String path, Long userId) {
        return sendRequest(HttpMethod.DELETE, path, userId, null);
    }

    private ResponseEntity<Object> sendRequest(HttpMethod method, String path, Long userId, Object body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (userId != null) {
            headers.set("X-Sharer-User-Id", String.valueOf(userId));
        }

        HttpEntity<Object> requestEntity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Object> response = restTemplate.exchange(path, method, requestEntity, Object.class);

            // Если статус не 2xx, возвращаем ответ с тем же статусом
            if (!response.getStatusCode().is2xxSuccessful()) {
                return ResponseEntity.status(response.getStatusCode())
                        .body(response.getBody());
            }

            return ResponseEntity.status(response.getStatusCode())
                    .body(response.getBody());
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            // Перехватываем ошибки 4xx и возвращаем их как есть
            return ResponseEntity.status(e.getStatusCode())
                    .body(e.getResponseBodyAsString());
        } catch (org.springframework.web.client.HttpServerErrorException e) {
            // Перехватываем ошибки 5xx
            return ResponseEntity.status(e.getStatusCode())
                    .body(e.getResponseBodyAsString());
        } catch (Exception e) {
            // Другие ошибки
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"Internal Server Error\", \"message\": \"" + e.getMessage() + "\"}");
        }
    }
}