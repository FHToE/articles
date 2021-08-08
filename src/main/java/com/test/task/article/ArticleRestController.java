package com.test.task.article;

import com.test.task.article.dto.ArticleDto;
import com.test.task.article.dto.ArticlePerDayStatistic;
import com.test.task.role.model.RoleName;
import com.test.task.security.oauth.UserPrincipal;
import com.test.task.user.model.CurrentUser;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import java.util.List;

@RestController
@RequestMapping("/api/article")
@AllArgsConstructor
@Slf4j
@Validated
public class ArticleRestController {
    private final ArticleService articleService;

    @PostMapping
    public ResponseEntity<?> addArticle(@Valid @RequestBody ArticleDto articleDto, @CurrentUser UserPrincipal user) {
        return articleService.addArticle(articleDto, user);
    }

    @GetMapping(params = { "page", "size" })
    public List<ArticleDto> getArticles(@RequestParam("page") int page, @RequestParam("size") @Max(10) int size) {
        return articleService.getArticles(page, size);
    }

    @GetMapping("/statistic")
    public List<ArticlePerDayStatistic> getAdminStatistic(@CurrentUser UserPrincipal user) {
        if (!user.getRoles().contains(RoleName.ADMIN)) {
            throw new AccessDeniedException("Allowed admins only");
        }
        return articleService.getAdminStatistic();
    }
}
