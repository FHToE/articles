package com.test.task.auth;

import com.test.task.exception.SingleMessageResponse;
import com.test.task.exception.UserAlreadyRegisteredException;
import com.test.task.exception.ValidationMessageCreator;
import com.test.task.security.model.LoginRequest;
import com.test.task.security.model.SignUpRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
@Slf4j
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest, Errors validationResult) {
        if (validationResult.hasErrors()) {
            return ResponseEntity.badRequest()
                    .body(new SingleMessageResponse(
                                    ValidationMessageCreator.createString(validationResult, " ")
                            )
                    );
        }
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest, Errors validationResult) {
        if (validationResult.hasErrors()) {
            return ResponseEntity.badRequest()
                    .body(new SingleMessageResponse(
                                    ValidationMessageCreator.createString(validationResult, " ")
                            )
                    );
        }
        try {
            return ResponseEntity.ok(authService.register(signUpRequest));
        } catch (UserAlreadyRegisteredException ex) {
            return ResponseEntity.badRequest().body(new SingleMessageResponse(ex.getMessage()));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestHeader("Refresh-token") String token) {
        try {
            return ResponseEntity.ok(authService.refreshToken(token));
        } catch (Exception ex) {
            return new ResponseEntity<>(new SingleMessageResponse(ex.getMessage()), HttpStatus.UNAUTHORIZED);
        }
    }
}
