package com.bookstoreapp.repository;

import org.springframework.data.jpa.domain.Specification;

public interface SpecificationProvider<T> {
    String getKey();

    Specification<T> getSpecification(String[] params);

    Specification<T> getSpecification(String param);
}
