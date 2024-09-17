DELETE FROM books_categories
WHERE book_id IN
      (SELECT id FROM books WHERE title = 'Test Book Title 3'
                              AND author = 'Test Book Author 3'
                              AND isbn = '1234567890'
                              AND price = 103);

DELETE FROM books
WHERE title = 'Test Book Title 3'
  AND author = 'Test Book Author 3'
  AND isbn = '1234567890'
  AND price = 103;
