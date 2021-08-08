package com.test.task.auth;

import com.test.task.exception.UserAlreadyRegisteredException;
import com.test.task.security.model.AuthResponse;
import com.test.task.security.model.LoginRequest;
import com.test.task.security.model.RefreshTokenResponse;
import com.test.task.security.model.SignUpRequest;
import com.test.task.security.oauth.TokenProvider;
import com.test.task.user.UserRepository;
import com.test.task.user.UserService;
import com.test.task.user.model.User;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;

    private final TokenProvider tokenProvider;

    private final UserRepository userRepository;

    private final UserService customUserDetailsService;

    public final PasswordEncoder passwordEncoder;

    public AuthResponse login(LoginRequest loginRequest) {
        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        final String token = tokenProvider.createAccessToken(authentication);
        final String refresh = tokenProvider.createRefreshToken(authentication);
        return new AuthResponse(token, refresh);
    }

    public AuthResponse register(SignUpRequest signUpRequest) throws UserAlreadyRegisteredException {
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new UserAlreadyRegisteredException(
                    "User with email '" + signUpRequest.getEmail() + "' is already registered."
            );
        }
        final User user = new User();
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        userRepository.save(user);
        return login(new LoginRequest(signUpRequest.getEmail(), signUpRequest.getPassword()));
    }

    public RefreshTokenResponse refreshToken(String token) {
        tokenProvider.validateToken(token);
        final UUID userId = tokenProvider.getUserIdFromToken(token);
        final UserDetails userDetails = customUserDetailsService.loadUserById(userId);
        final UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return new RefreshTokenResponse(tokenProvider.createAccessToken(authentication));
    }
}
