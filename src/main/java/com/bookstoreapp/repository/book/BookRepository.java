package com.bookstoreapp.repository.book;

import com.bookstoreapp.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {
    @Query("SELECT b FROM Book b LEFT JOIN FETCH b.categories c "
            + "WHERE :categoryId IN (SELECT cat.id FROM b.categories cat)")
    List<Book> findAllByCategoryId(Long categoryId, Pageable pageable);

    @Query("SELECT b FROM Book b LEFT JOIN FETCH b.categories")
    List<Book> findAllWithCategories(Pageable pageable);

    // for Docker-compose section.
    // When running the application in the IDE,

    @Query("FROM Book b LEFT JOIN FETCH b.categories с WHERE b.id = :id")
    Optional<Book> findById(Long id);
}
