package com.test.task.article;

import com.test.task.article.model.Article;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public interface ArticleRepository extends JpaRepository<Article, UUID> {
    Optional<Article> findByTitle(String name);

    int countArticleByPublishedAt(LocalDate date);
}
