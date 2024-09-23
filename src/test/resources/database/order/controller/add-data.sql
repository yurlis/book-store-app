INSERT INTO users (id, email, password, first_name, last_name, shipping_address, is_deleted)
VALUES (1, 'testuser@test.com', 'password123', 'TestName1', 'TestLastName1', 'address1', 0),
       (2, 'testuser2@test.com', 'password123', 'TestName2', 'TestLastName2', 'address2', 0);

INSERT INTO books (id, title, author, isbn, price, description, cover_image, is_deleted)
VALUES (1, 'TestBook 1', 'Author 1', '123-1234567890', 199.99, 'Test Author 1', 'image1.jpg', 0),
       (2, 'TestBook 2', 'Author 1', '123-0987654321', 299.99, 'Test Author 2', 'image2.jpg', 0),
       (3, 'TestBook 3', 'Author 1', '123-1122334455', 399.99, 'Test Author 3', 'image3.jpg', 0);

INSERT INTO categories (id, name, description, is_deleted)
VALUES (1, 'TestCategory1', 'category description', 0);
INSERT INTO categories (id, name, description, is_deleted)
VALUES (2, 'TestCategory2', 'category description', 0);

INSERT INTO shopping_carts (id, user_id, is_deleted)
VALUES (1, 1, 0);

INSERT INTO cart_items (id, shopping_cart_id, book_id, quantity, is_deleted)
VALUES (1, 1, 1, 3, 0);
INSERT INTO cart_items (id, shopping_cart_id, book_id, quantity, is_deleted)
VALUES (2, 1, 2, 4, 0);

INSERT INTO users_roles (user_id, role_id)
VALUES (1, (SELECT id FROM roles WHERE role = 'ROLE_USER'));

INSERT INTO orders (id, user_id, status, total, order_date, shipping_address, is_deleted)
VALUES (1, 1, 'PENDING', 1799.66, '2024-09-20', 'shipping test address', 0);

INSERT INTO order_items (id, order_id, book_id, quantity, price, is_deleted)
VALUES (1, 1, 1, 3, 199.99, 0),
       (2, 1, 2, 4, 299.99, 0);