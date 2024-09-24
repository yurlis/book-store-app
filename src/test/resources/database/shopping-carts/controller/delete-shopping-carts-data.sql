DELETE
FROM books_categories
WHERE book_id IN (1, 2, 3);

DELETE
FROM categories
WHERE id IN (1, 2);

DELETE
FROM cart_items
WHERE shopping_cart_id = 1;

DELETE
FROM shopping_carts
WHERE id = 1;

DELETE
FROM books
WHERE id IN (1, 2, 3);

DELETE
FROM users_roles
WHERE user_id = 1;

DELETE
FROM users
WHERE id = 1;