INSERT INTO users (id, email, first_name, last_name, password, is_deleted)
VALUES (17, 'testuser@example.com', 'TestUserName', 'TestUserLastName', 'pasword123', 0);

INSERT INTO users_roles (user_id, role_id) values (17, (SELECT id FROM roles WHERE role = 'ROLE_USER'));
INSERT INTO users_roles (user_id, role_id) values (17, (SELECT id FROM roles WHERE role = 'ROLE_ADMIN'));
