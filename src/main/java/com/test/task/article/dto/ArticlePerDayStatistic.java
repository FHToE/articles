package com.test.task.article.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ArticlePerDayStatistic {
    private String date;
    private Integer articlesPublished;
}
