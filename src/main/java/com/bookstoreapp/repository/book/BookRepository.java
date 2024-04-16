package com.bookstoreapp.repository.book;

import com.bookstoreapp.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {
    @Query("SELECT b FROM Book b LEFT JOIN FETCH b.categories c "
            + "WHERE :categoryId IN (SELECT cat.id FROM b.categories cat)")
    List<Book> findAllByCategoryId(Long categoryId, Pageable pageable);
}
