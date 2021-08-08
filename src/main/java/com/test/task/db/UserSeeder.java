package com.test.task.db;

import com.test.task.auth.AuthService;
import com.test.task.exception.ResourceNotFoundException;
import com.test.task.role.model.RoleName;
import com.test.task.role.RoleRepository;
import com.test.task.user.UserRepository;
import com.test.task.role.model.Role;
import com.test.task.user.model.User;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@AllArgsConstructor
@Slf4j
public class UserSeeder {
    private final JdbcTemplate jdbcTemplate;
    private final UserRepository userRepository;
    private final AuthService authService;
    private final RoleRepository roleRepository;

    @EventListener
    @Transactional
    public void seed(ContextRefreshedEvent event) {
        List<User> u = jdbcTemplate.query("SELECT * FROM users", (resultSet, rowNum) -> null);
        if(u == null || u.size() <= 0) {
            seedUsers();
        }
    }

    private void seedUsers() {
        List<Role> roles = roleRepository.findAll();
        Role adminRole = roles.stream()
                .filter(r -> r.getRoleName() == RoleName.ADMIN)
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Role", "name", RoleName.ADMIN.name()));
        Role userRole = roles.stream()
                .filter(r -> r.getRoleName() == RoleName.USER)
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Role", "name", RoleName.USER.name()));
        User admin = userRepository.save(User.builder()
                .email("admin@mail.com")
                .password(authService.passwordEncoder.encode("admin_password"))
                .build());
        User user = userRepository.save(User.builder()
                .email("user@mail.com")
                .password(authService.passwordEncoder.encode("user_password"))
                .build());
        userRole.getUsers().add(user);
        userRole.getUsers().add(admin);
        adminRole.getUsers().add(admin);
        roleRepository.save(userRole);
        roleRepository.save(adminRole);
        log.info("Test users created!");
    }
}
