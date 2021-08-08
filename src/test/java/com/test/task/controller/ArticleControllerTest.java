package com.test.task.controller;

import com.test.task.IntegrationBaseTest;
import com.test.task.article.ArticleRepository;
import com.test.task.article.dto.ArticleDto;
import com.test.task.auth.AuthService;
import com.test.task.exception.ResourceNotFoundException;
import com.test.task.role.RoleRepository;
import com.test.task.role.model.Role;
import com.test.task.role.model.RoleName;
import com.test.task.security.model.AuthResponse;
import com.test.task.security.model.LoginRequest;
import com.test.task.user.UserRepository;
import com.test.task.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpMethod;

import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ArticleControllerTest extends IntegrationBaseTest {

    private static final String ARTICLE_CONTROLLER_ENDPOINT = "/api/article";
    private static final String AUTH_CONTROLLER_ENDPOINT = "/api/auth";

    private static User ADMIN;
    private static User USER;
    private static final String USER_PASSWORD = "user_password";
    private static final String ADMIN_PASSWORD = "admin_password";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private AuthService authService;

    @BeforeEach
    public void cleanUpDb() {
        List<Role> roles = roleRepository.findAll();
        for (Role role: roles) {
            role.setUsers(new HashSet<>());
            roleRepository.save(role);
        }
        userRepository.deleteAll();
        articleRepository.deleteAll();
        Role adminRole = roles.stream()
                .filter(r -> r.getRoleName() == RoleName.ADMIN)
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Role", "name", RoleName.ADMIN.name()));
        Role userRole = roles.stream()
                .filter(r -> r.getRoleName() == RoleName.USER)
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Role", "name", RoleName.USER.name()));
        ADMIN = userRepository.save(User.builder()
                .email("admin@mail.com")
                .password(authService.passwordEncoder.encode(ADMIN_PASSWORD))
                .build());
        USER = userRepository.save(User.builder()
                .email("user@mail.com")
                .password(authService.passwordEncoder.encode(USER_PASSWORD))
                .build());
        userRole.getUsers().add(USER);
        userRole.getUsers().add(ADMIN);
        adminRole.getUsers().add(ADMIN);
        roleRepository.save(userRole);
        roleRepository.save(adminRole);
    }

    @Test
    public void authenticatedAdminShouldHaveAccessToAllEndpoints() {
        HttpHeaders headers = getHeadersWithJwt(ADMIN.getEmail(), ADMIN_PASSWORD);
        ArticleDto articleDto = ArticleDto.builder()
                .title("title")
                .datePublished("2021-12-12")
                .content("content")
                .author("author")
                .build();
        HttpEntity<ArticleDto> entity = new HttpEntity<>(articleDto, headers);
        ResponseEntity<String> response = restTemplate.exchange(ARTICLE_CONTROLLER_ENDPOINT, HttpMethod.POST, entity, String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        response = restTemplate.exchange(ARTICLE_CONTROLLER_ENDPOINT, HttpMethod.GET, entity, String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        response = restTemplate.exchange(ARTICLE_CONTROLLER_ENDPOINT + "/statistic", HttpMethod.GET, entity, String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void authenticatedAdminShouldHaveAccessToNonAdminEndpoints() {
        HttpHeaders headers = getHeadersWithJwt(USER.getEmail(), USER_PASSWORD);
        ArticleDto articleDto = ArticleDto.builder()
                .title("title")
                .datePublished("2021-12-12")
                .content("content")
                .author("author")
                .build();
        HttpEntity<ArticleDto> entity = new HttpEntity<>(articleDto, headers);
        ResponseEntity<String> response = restTemplate.exchange(ARTICLE_CONTROLLER_ENDPOINT, HttpMethod.POST, entity, String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        response = restTemplate.exchange(ARTICLE_CONTROLLER_ENDPOINT, HttpMethod.GET, entity, String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void authenticatedAdminShouldNotHaveAccessToAdminEndpoints() {
        HttpHeaders headers = getHeadersWithJwt(USER.getEmail(), USER_PASSWORD);

        HttpEntity<?> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(ARTICLE_CONTROLLER_ENDPOINT + "/statistic", HttpMethod.GET, entity, String.class);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    public void whenCreateArticleWithBadParameters_thenBadRequestReturned() {
        HttpHeaders headers = getHeadersWithJwt(ADMIN.getEmail(), ADMIN_PASSWORD);
        ArticleDto badArticleDto = ArticleDto.builder()
                .title("title")
                .content("content")
                .author("author")
                .build();
        HttpEntity<ArticleDto> entity = new HttpEntity<>(badArticleDto, headers);
        ResponseEntity<String> response = restTemplate.exchange(ARTICLE_CONTROLLER_ENDPOINT, HttpMethod.POST, entity, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    private HttpHeaders getHeadersWithJwt(String email, String password) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        LoginRequest loginRequest = LoginRequest.builder()
                .email(email)
                .password(password)
                .build();
        HttpEntity<LoginRequest> signInEntity = new HttpEntity<>(loginRequest, headers);
        ResponseEntity<AuthResponse> response = restTemplate.exchange(AUTH_CONTROLLER_ENDPOINT + "/login", HttpMethod.POST, signInEntity, AuthResponse.class);
        headers.add("Authorization", "Bearer " + response.getBody().getAccessToken());
        return headers;
    }
}
