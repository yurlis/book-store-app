INSERT INTO users (id, email, password, first_name, last_name, shipping_address, is_deleted)
VALUES (1, 'testuser1@test.com', 'password123', 'TestName1', 'TestLastName1', 'address1', 0);

INSERT INTO users_roles (user_id, role_id)
VALUES (1, (SELECT id FROM roles WHERE role = 'ROLE_USER'));