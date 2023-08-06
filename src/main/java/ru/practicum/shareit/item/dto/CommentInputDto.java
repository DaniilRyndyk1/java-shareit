package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@NoArgsConstructor
@Data
public class CommentInputDto {
    @Size(max = 2048)
    @NotBlank
    private String text;
}
