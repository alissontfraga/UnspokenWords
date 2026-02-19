package com.alissontfraga.unspokenwords.unit;

import com.alissontfraga.unspokenwords.entity.User;
import com.alissontfraga.unspokenwords.enums.Role;
import com.alissontfraga.unspokenwords.security.JwtUtil;
import com.auth0.jwt.interfaces.DecodedJWT;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setup() {
        jwtUtil = new JwtUtil();

        // Injeta manualmente os valores do @Value
        ReflectionTestUtils.setField(jwtUtil, "jwtSecret", "test-secret");
        ReflectionTestUtils.setField(jwtUtil, "expiration", 60000L);

        // cobre o @PostConstruct
        jwtUtil.init();
    }

    @Test
    void shouldGenerateAndDecodeTokenSuccessfully() {

        User user = new User();
        user.setUsername("alisson");
        user.setPassword("123");
        user.setRoles(Set.of(Role.ROLE_USER));

        String token = jwtUtil.generateToken(user);

        assertNotNull(token);

        DecodedJWT decoded = jwtUtil.decode(token);

        // cobre extractUsername
        assertEquals("alisson", jwtUtil.extractUsername(decoded));

        // cobre extractRoles com valor
        List<String> roles = jwtUtil.extractRoles(decoded);

        assertEquals(1, roles.size());
        assertEquals("ROLE_USER", roles.get(0));
    }

    @Test
    void shouldReturnEmptyRolesWhenUserHasNoRoles() {

        User user = new User();
        user.setUsername("noRoles");
        user.setPassword("123");
        user.setRoles(Set.of()); // sem roles

        String token = jwtUtil.generateToken(user);

        DecodedJWT decoded = jwtUtil.decode(token);

        // cobre branch do Optional.orElse(List.of())
        List<String> roles = jwtUtil.extractRoles(decoded);

        assertNotNull(roles);
        assertTrue(roles.isEmpty());
    }

    @Test
    void shouldThrowExceptionWhenTokenIsInvalid() {

        assertThrows(Exception.class, () -> {
            jwtUtil.decode("invalid.token.here");
        });
    }
}
