
-- ADMIN INICIAL
-- username: admin
-- password: admin123 (bcrypt)


INSERT INTO users (username, password)
SELECT
    'admin',
    '$2a$12$xcEfEGwD7gsgOh002h9UzeK.kJefgEY2gwcqJghiSc1i4zbodwgMW'
WHERE NOT EXISTS (
    SELECT 1 FROM users WHERE username = 'admin'
);

INSERT INTO user_roles (user_id, role)
SELECT u.id, 'ROLE_ADMIN'
FROM users u
WHERE u.username = 'admin'
AND NOT EXISTS (
    SELECT 1
    FROM user_roles ur
    WHERE ur.user_id = u.id
      AND ur.role = 'ROLE_ADMIN'
);

INSERT INTO user_roles (user_id, role)
SELECT u.id, 'ROLE_USER'
FROM users u
WHERE u.username = 'admin'
AND NOT EXISTS (
    SELECT 1
    FROM user_roles ur
    WHERE ur.user_id = u.id
      AND ur.role = 'ROLE_USER'
);