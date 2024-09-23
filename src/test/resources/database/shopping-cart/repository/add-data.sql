INSERT INTO users (id, email, first_name, last_name, password, is_deleted)
VALUES (17, 'testuser@example.com', 'TestUserName', 'TestUserLastName', 'password123', 0);

INSERT INTO shopping_carts (id, user_id) VALUES (17, 17);

INSERT INTO cart_items (id, book_id, quantity, is_deleted, shopping_cart_id) VALUES (15, 1, 5, 0, 17);
INSERT INTO cart_items (id, book_id, quantity, is_deleted, shopping_cart_id) VALUES (16, 2, 6, 0, 17);
INSERT INTO cart_items (id, book_id, quantity, is_deleted, shopping_cart_id) VALUES (17, 5, 7, 0, 17);