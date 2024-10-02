DELETE
FROM order_items
WHERE order_id = 1;

DELETE
FROM orders
WHERE id = 1;

DELETE
FROM books_categories
WHERE book_id IN (1, 2, 3);

DELETE
FROM categories
WHERE id IN (1, 2);

DELETE
FROM books
WHERE id IN (1, 2, 3);

DELETE
FROM users_roles
WHERE user_id = 1;

DELETE
FROM users
WHERE id IN (1, 2, 3);