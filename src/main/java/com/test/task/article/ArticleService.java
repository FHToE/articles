package com.test.task.article;

import com.test.task.article.dto.ArticleDto;
import com.test.task.article.dto.ArticlePerDayStatistic;
import com.test.task.article.model.Article;
import com.test.task.security.oauth.UserPrincipal;
import com.test.task.user.UserRepository;
import com.test.task.user.model.User;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class ArticleService {
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;

    public ResponseEntity<?> addArticle(ArticleDto dto, UserPrincipal userPrincipal) {
        if (articleRepository.findByTitle(dto.getTitle()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(String.format("Article with title %s already exists", dto.getTitle()));
        }
        LocalDate datePublished;
        try {
            datePublished = LocalDate.parse(dto.getDatePublished());
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body("Bad date format");
        }
        User user = userRepository.getById(userPrincipal.getId());
        Article article = Article.builder()
                .author(dto.getAuthor())
                .content(dto.getContent())
                .title(dto.getTitle())
                .publishedAt(datePublished)
                .user(user)
                .build();
        articleRepository.save(article);
        return ResponseEntity.ok(String.format("Article '%s' created!", article.getTitle()));
    }

    public List<ArticleDto> getArticles(int page, int size) {
        Page<Article> articlesPage = articleRepository.findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "publishedAt")));
        return articlesPage.get().map(ArticleMapper::articleToArticleDto).collect(Collectors.toList());
    }

    public List<ArticlePerDayStatistic> getAdminStatistic() {
        LocalDate date = LocalDate.now();
        List<ArticlePerDayStatistic> statistics = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            ArticlePerDayStatistic statistic = ArticlePerDayStatistic.builder()
                    .date(date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                    .articlesPublished(articleRepository.countArticleByPublishedAt(date))
                    .build();
            statistics.add(statistic);
            date = date.minusDays(1);
        }
        return statistics;
    }
}
