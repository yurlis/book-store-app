package com.bookstoreapp.repository.book.spec;

import com.bookstoreapp.model.Book;
import com.bookstoreapp.repository.SpecificationProvider;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class TitleSpecificationProvider implements SpecificationProvider<Book> {
    @Override
    public String getKey() {
        return "title";
    }

//    We are looking for full compliance
//    @Override
//    public Specification<Book> getSpecification(String[] params) {
//        return (root, query, criteriaBuilder) -> root.get("title").in(Arrays.stream(params).toArray());
//    }


    // We are looking for a partial match
    @Override
    public Specification<Book> getSpecification(String[] params) {
        return (root, query, criteriaBuilder) -> {
            if (params.length == 0) {
                return criteriaBuilder.conjunction();
            }
            Predicate predicate = criteriaBuilder.disjunction();
            for (String param : params) {
                predicate = criteriaBuilder.or(predicate, criteriaBuilder.like(root.get("title"), "%" + param + "%"));
            }
            return predicate;
        };
    }

    @Override
    public Specification<Book> getSpecification(String param) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("title"), param);
    }
}
