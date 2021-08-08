package com.test.task.service;

import com.test.task.IntegrationBaseTest;
import com.test.task.article.ArticleRepository;
import com.test.task.article.ArticleService;
import com.test.task.article.dto.ArticleDto;
import com.test.task.article.dto.ArticlePerDayStatistic;
import com.test.task.article.model.Article;
import com.test.task.auth.AuthService;
import com.test.task.exception.ResourceNotFoundException;
import com.test.task.role.RoleRepository;
import com.test.task.role.model.Role;
import com.test.task.role.model.RoleName;
import com.test.task.user.UserRepository;
import com.test.task.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ArticleServiceIntegrationIntegrationTest extends IntegrationBaseTest {

    private static User USER;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private AuthService authService;

    @BeforeEach
    public void setUpDb() {
        List<Role> roles = roleRepository.findAll();
        for (Role role : roles) {
            role.setUsers(new HashSet<>());
            roleRepository.save(role);
        }
        userRepository.deleteAll();
        articleRepository.deleteAll();
        Role userRole = roles.stream()
                .filter(r -> r.getRoleName() == RoleName.USER)
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Role", "name", RoleName.USER.name()));
        USER = userRepository.save(User.builder()
                .email("user@mail.com")
                .password(authService.passwordEncoder.encode("USER_PASSWORD"))
                .build());
        userRole.getUsers().add(USER);
        roleRepository.save(userRole);
        LocalDate date = LocalDate.now();

        articleRepository.save(Article.builder()
                .user(USER)
                .title("title1")
                .publishedAt(date)
                .content("content")
                .author("author")
                .build());
        articleRepository.save(Article.builder()
                .user(USER)
                .title("title2")
                .publishedAt(date)
                .content("content")
                .author("author")
                .build());

        date = date.minusDays(2);

        articleRepository.save(Article.builder()
                .user(USER)
                .title("title3")
                .publishedAt(date)
                .content("content")
                .author("author")
                .build());
        articleRepository.save(Article.builder()
                .user(USER)
                .title("title4")
                .publishedAt(date)
                .content("content")
                .author("author")
                .build());
        articleRepository.save(Article.builder()
                .user(USER)
                .title("title5")
                .publishedAt(date)
                .content("content")
                .author("author")
                .build());

        date = date.minusDays(3);


        articleRepository.save(Article.builder()
                .user(USER)
                .title("title6")
                .publishedAt(date)
                .content("content")
                .author("author")
                .build());
    }

    @Test
    public void whenCreateArticle_ArticleAddedToDb() {
        String articleTitle = "title-test-article";
        ArticleDto dto = ArticleDto.builder()
                .title(articleTitle)
                .datePublished("2021-08-08")
                .content("content")
                .author("author")
                .build();
        articleService.addArticle(dto, USER.getUserId());
        assertTrue(articleRepository.findByTitle(articleTitle).isPresent());
    }

    @Test
    public void whenGetArticles_ArticlesReturned() {
        List<ArticleDto> articles = articleService.getArticles(1, 3);
        assertEquals(3, articles.size());

        articles = articleService.getArticles(0, 10);
        assertEquals(6, articles.size());
    }

    @Test
    public void whenGetStatistic_StatisticReturned() {
        List<ArticlePerDayStatistic> statistics = articleService.getAdminStatistic();
        assertEquals(7, statistics.size());

        LocalDate date1 = LocalDate.now();
        assertEquals(Integer.valueOf(2), statistics.stream()
                .filter(s -> s.getDate().equals(date1.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))))
                .findAny().get().getArticlesPublished());

        LocalDate date2 = date1.minusDays(1);
        assertEquals(Integer.valueOf(0), statistics.stream()
                .filter(s -> s.getDate().equals(date2.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))))
                .findAny().get().getArticlesPublished());

        LocalDate date3 = date2.minusDays(1);
        assertEquals(Integer.valueOf(3), statistics.stream()
                .filter(s -> s.getDate().equals(date3.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))))
                .findAny().get().getArticlesPublished());

        LocalDate date4 = date3.minusDays(1);
        assertEquals(Integer.valueOf(0), statistics.stream()
                .filter(s -> s.getDate().equals(date4.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))))
                .findAny().get().getArticlesPublished());

        LocalDate date5 = date4.minusDays(1);
        assertEquals(Integer.valueOf(0), statistics.stream()
                .filter(s -> s.getDate().equals(date5.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))))
                .findAny().get().getArticlesPublished());

        LocalDate date6 = date5.minusDays(1);
        assertEquals(Integer.valueOf(1), statistics.stream()
                .filter(s -> s.getDate().equals(date6.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))))
                .findAny().get().getArticlesPublished());

        LocalDate date7 = date6.minusDays(1);
        assertEquals(Integer.valueOf(0), statistics.stream()
                .filter(s -> s.getDate().equals(date7.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))))
                .findAny().get().getArticlesPublished());
    }
}
