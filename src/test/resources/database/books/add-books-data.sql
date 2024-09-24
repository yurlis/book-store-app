INSERT INTO categories (id, name, description, is_deleted)
VALUES (1, 'Test Category 1', 'category description', 0);

INSERT INTO categories (id, name, description, is_deleted)
VALUES (2, 'Test Category 2', 'category description', 0);

INSERT INTO books (id, title, author, isbn, price, description, cover_image, is_deleted)
VALUES (1, 'Test Book Title 1', 'Test Book Author 1', '5678901234', 100.00, 'Test Book Description 1', 'Test Book Cover Image URL 1', FALSE);
INSERT INTO books_categories (book_id, category_id) VALUES (1, 1);

INSERT INTO books (id, title, author, isbn, price, description, cover_image, is_deleted)
VALUES (2, 'Test Book Title 2', 'Test Book Author 2', '3456789012', 200.00, 'Test Book Description 2', 'Test Book Cover Image URL 2', FALSE);
INSERT INTO books_categories (book_id, category_id) VALUES (2, 1);

INSERT INTO books (id, title, author, isbn, price, description, cover_image, is_deleted)
VALUES (3, 'Test Book Title 3', 'Test Book Author 3', '1234567890', 300.00, 'Test Book Description 3', 'Test Book Cover Image URL 3', FALSE);
INSERT INTO books_categories (book_id, category_id) VALUES (3, 2);
