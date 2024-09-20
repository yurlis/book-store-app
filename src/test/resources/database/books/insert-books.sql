INSERT INTO books (id, title, author, isbn, price, description, cover_image, is_deleted)
VALUES (1, 'Test Book Title 1', 'Test Book Author 1', '5678901234', 101, 'Test Book Description 1', 'cover1.jpg', FALSE);
INSERT INTO books_categories (book_id, category_id) VALUES (1, 1);

INSERT INTO books (id, title, author, isbn, price, description, cover_image, is_deleted)
VALUES (2, 'Test Book Title 2', 'Test Book Author 2', '3456789012', 102, 'Test Book Description 2', 'cover2.jpg', FALSE);
INSERT INTO books_categories (book_id, category_id) VALUES (2, 1);

INSERT INTO books (id, title, author, isbn, price, description, cover_image, is_deleted)
VALUES (3, 'Test Book Title 3', 'Test Book Author 3', '1234567890', 103, 'Test Book Description 3', 'cover3.jpg', FALSE);
INSERT INTO books_categories (book_id, category_id) VALUES (3, 2);
