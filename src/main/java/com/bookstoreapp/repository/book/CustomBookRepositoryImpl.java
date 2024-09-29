package com.bookstoreapp.repository.book;

import com.bookstoreapp.model.Book;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CustomBookRepositoryImpl implements CustomBookRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Book> findAllWithCategories(Specification<Book> spec) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Book> query = cb.createQuery(Book.class);
        Root<Book> bookRoot = query.from(Book.class);

        // Виконуємо JOIN FETCH для категорій
        bookRoot.fetch("categories", JoinType.LEFT);

        // Додаємо специфікації
        if (spec != null) {
            query.where(spec.toPredicate(bookRoot, query, cb));
        }

        return entityManager.createQuery(query).getResultList();
    }
}
