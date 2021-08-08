package com.test.task.article.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ArticleDto {
    @NotNull
    @Size(max=100)
    private String title;

    @NotNull
    private String author;

    @NotNull
    private String content;

    @NotNull
    @Pattern(regexp="(\\d{4}-\\d{2}-\\d{2})")
    private String datePublished;
}
