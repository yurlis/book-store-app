DELETE
FROM books_categories
WHERE book_id IN (1, 2, 3, 4);

DELETE
FROM categories
WHERE id IN (1, 2);

DELETE
FROM books
WHERE id IN (1, 2, 3, 4);