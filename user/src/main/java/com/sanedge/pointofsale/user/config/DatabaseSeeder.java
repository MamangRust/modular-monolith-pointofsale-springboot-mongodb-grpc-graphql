package com.sanedge.pointofsale.user.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.sanedge.pointofsale.pb.role.ApiResponseRole;
import com.sanedge.pointofsale.pb.role.FindByNameRoleRequest;
import com.sanedge.pointofsale.pb.role.RoleQueryServiceGrpc;
import com.sanedge.pointofsale.user.models.User;
import com.sanedge.pointofsale.user.models.UserRole;
import com.sanedge.pointofsale.user.repository.UserCommandRepository;
import com.sanedge.pointofsale.user.repository.UserQueryRepository;
import com.sanedge.pointofsale.user.repository.UserRoleRepository;

import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true", matchIfMissing = true)
public class DatabaseSeeder implements CommandLineRunner {

    private final UserQueryRepository userQueryRepository;
    private final UserCommandRepository userCommandRepository;
    private final UserRoleRepository userRoleRepository;
    private final RoleQueryServiceGrpc.RoleQueryServiceBlockingStub roleQueryServiceBlockingStub;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userQueryRepository.count() > 0) {
            return;
        }

        Integer adminRoleId = resolveRoleId("ROLE_ADMIN");
        Integer userRoleId = resolveRoleId("ROLE_USER");

        if (adminRoleId != null) {
            seedUser("admin", "Admin", "User", "admin@sanedge.com", adminRoleId);
            log.info("Admin user seeded.");
        } else {
            log.warn("Skipping admin user seed: ROLE_ADMIN not found");
        }

        if (userRoleId != null) {
            seedUser("user", "John", "Doe", "user@sanedge.com", userRoleId);
            log.info("Default user seeded.");
        } else {
            log.warn("Skipping default user seed: ROLE_USER not found");
        }
    }

    private void seedUser(String username, String firstname, String lastname, String email, Integer roleId) {
        User user = new User();
        user.setUsername(username);
        user.setFirstname(firstname);
        user.setLastname(lastname);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode("password"));
        user = userCommandRepository.save(user);

        UserRole userRole = new UserRole();
        userRole.setUserId(user.getUserId());
        userRole.setRoleId(String.valueOf(roleId));
        userRoleRepository.save(userRole);
    }

    private Integer resolveRoleId(String name) {
        try {
            ApiResponseRole res = roleQueryServiceBlockingStub.findByName(
                    FindByNameRoleRequest.newBuilder().setName(name).build());
            if (!res.hasData()) {
                return null;
            }
            return res.getData().getRoleId();
        } catch (StatusRuntimeException e) {
            log.warn("Skipping seed role {}: {}", name, e.getStatus().getCode());
            return null;
        }
    }
}
