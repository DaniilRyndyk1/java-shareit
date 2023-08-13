package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemInputDto;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> get(Long id, Long userId) {
        return get("/" + id, userId);
    }

    public ResponseEntity<Object> getAll(Integer from, Integer size, Long userId) {
        return get("?from=" + from + "&size=" + size, userId);
    }

    public ResponseEntity<Object> delete(Long id) {
        return delete("/" + id);
    }

    public ResponseEntity<Object> create(ItemInputDto dto, Long userId) {
        return post("", userId, dto);
    }

    public ResponseEntity<Object> change(ItemInputDto dto, Long id, Long userId) {
        return patch("/" + id, userId, dto);
    }

    public ResponseEntity<Object> search(String text, Integer from, Integer size) {
        return get("/search?text=" + text + "&from=" + from + "&size=" + size, 0L);
    }

    public ResponseEntity<Object> createComment(CommentDto dto, Long itemId, Long userId) {
        return post("/" + itemId + "/comment", userId, dto);
    }
}
