package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.databind.JsonMappingException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@JsonTest
public class CommentDtoJsonTest {

    @Autowired
    private JacksonTester<CommentDto> json;

    @Test
    public void testSerialize() throws Exception {
        CommentDto comment = CommentDto.builder()
                .id(1L)
                .text("Test comment")
                .itemId(100L)
                .authorId(50L)
                .authorName("John Doe")
                .created(LocalDateTime.of(2023, 10, 30, 12, 30, 45))
                .build();

        assertThat(this.json.write(comment)).hasJsonPathStringValue("@.text");
        assertThat(this.json.write(comment)).extractingJsonPathStringValue("@.text")
                .isEqualTo("Test comment");
        assertThat(this.json.write(comment)).extractingJsonPathStringValue("@.created")
                .isEqualTo("2023-10-30T12:30:45");
    }

    @Test
    public void testDeserialize() throws Exception {
        String content = "{\"id\":1,\"text\":\"Test comment\",\"itemId\":100,\"authorId\":50,\"authorName\":\"John Doe\",\"created\":\"2023-10-30T12:30:45\"}";

        CommentDto comment = this.json.parseObject(content);

        assertThat(comment).isNotNull();
        assertThat(comment.getId()).isEqualTo(1L);
        assertThat(comment.getText()).isEqualTo("Test comment");
        assertThat(comment.getCreated()).isEqualTo(LocalDateTime.of(2023, 10, 30, 12, 30, 45));
    }

    @Test
    public void testInvalidDateFormat() throws Exception {
        String contentWithInvalidDate = "{\"id\":1,\"text\":\"Test comment\",\"itemId\":100,\"authorId\":50,\"authorName\":\"John Doe\",\"created\":\"30-10-2023 12:30:45\"}";

        JsonMappingException exception = assertThrows(JsonMappingException.class, () -> {
            CommentDto comment = this.json.parseObject(contentWithInvalidDate);
        });

        assertThat(exception.getMessage()).contains("created");
    }
}