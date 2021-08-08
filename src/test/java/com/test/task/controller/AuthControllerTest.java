package com.test.task.controller;

import com.test.task.IntegrationBaseTest;
import com.test.task.role.RoleRepository;
import com.test.task.role.model.Role;
import com.test.task.security.model.AuthResponse;
import com.test.task.security.model.LoginRequest;
import com.test.task.security.model.SignUpRequest;
import com.test.task.user.UserRepository;
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

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;


public class AuthControllerTest extends IntegrationBaseTest {

    private static final String AUTH_CONTROLLER_ENDPOINT = "/api/auth";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @BeforeEach
    public void cleanUpDb() {
        List<Role> roles = roleRepository.findAll();
        for (Role role: roles) {
            role.setUsers(new HashSet<>());
            roleRepository.save(role);
        }
        userRepository.deleteAll();
    }

    @Test
    public void whenLoginWithWrongCredentials_theReturnUnauthorized() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        LoginRequest loginRequest = LoginRequest.builder()
                .email("test@mail.com")
                .password("testPassword")
                .build();
        HttpEntity<LoginRequest> entity = new HttpEntity<>(loginRequest, headers);
        ResponseEntity<String> response = restTemplate.exchange(AUTH_CONTROLLER_ENDPOINT + "/login", HttpMethod.POST, entity, String.class);
        assertEquals(HttpStatus.UNAUTHORIZED,response.getStatusCode());
    }

    @Test
    public void whenSignUp_theReturnJwtTokens() {
        HttpHeaders headers = new HttpHeaders();
        String testEmail = "test@mail.com";
        headers.setContentType(MediaType.APPLICATION_JSON);
        SignUpRequest signUpRequest = SignUpRequest.builder()
                .email(testEmail)
                .password("testPassword")
                .build();
        HttpEntity<SignUpRequest> entity = new HttpEntity<>(signUpRequest, headers);
        ResponseEntity<AuthResponse> response = restTemplate.exchange(AUTH_CONTROLLER_ENDPOINT + "/signup", HttpMethod.POST, entity, AuthResponse.class);
        assertEquals(HttpStatus.OK,response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(userRepository.existsByEmail(testEmail));
        assertNotNull(response.getBody().getAccessToken());
        assertNotNull(response.getBody().getRefreshToken());
    }

    @Test
    public void whenSignInWithValidCredentials_theReturnJwtTokens() {
        HttpHeaders headers = new HttpHeaders();
        String testEmail = "test@mail.com";
        String testPassword = "testPassword";
        headers.setContentType(MediaType.APPLICATION_JSON);
        SignUpRequest signUpRequest = SignUpRequest.builder()
                .email(testEmail)
                .password(testPassword)
                .build();
        HttpEntity<SignUpRequest> signUpEntity = new HttpEntity<>(signUpRequest, headers);
        restTemplate.exchange(AUTH_CONTROLLER_ENDPOINT + "/signup", HttpMethod.POST, signUpEntity, AuthResponse.class);

        LoginRequest loginRequest = LoginRequest.builder()
                .email(testEmail)
                .password(testPassword)
                .build();
        HttpEntity<LoginRequest> signInEntity = new HttpEntity<>(loginRequest, headers);

        ResponseEntity<AuthResponse> response = restTemplate.exchange(AUTH_CONTROLLER_ENDPOINT + "/login", HttpMethod.POST, signInEntity, AuthResponse.class);
        assertEquals(HttpStatus.OK,response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getAccessToken());
        assertNotNull(response.getBody().getRefreshToken());
    }

    @Test
    public void whenSignUpWithBadCredentials_theReturnBadRequest() {
        HttpHeaders headers = new HttpHeaders();
        String testEmail = "test%mail.com";
        String testPassword = "1";
        headers.setContentType(MediaType.APPLICATION_JSON);
        SignUpRequest signUpRequest = SignUpRequest.builder()
                .email(testEmail)
                .password(testPassword)
                .build();
        HttpEntity<SignUpRequest> signUpEntity = new HttpEntity<>(signUpRequest, headers);
        ResponseEntity<AuthResponse>  response = restTemplate.exchange(AUTH_CONTROLLER_ENDPOINT + "/signup", HttpMethod.POST, signUpEntity, AuthResponse.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}
