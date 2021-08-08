package com.test.task.article;

import com.test.task.article.dto.ArticleDto;
import com.test.task.article.model.Article;

import java.time.format.DateTimeFormatter;

public class ArticleMapper {
    public static ArticleDto articleToArticleDto(Article article) {
        if (article == null) {
            return null;
        }  else {
            return ArticleDto.builder()
                    .author(article.getAuthor())
                    .content(article.getContent())
                    .datePublished(article.getPublishedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                    .title(article.getTitle())
                    .build();
        }
    }
}
