package com.bookstoreapp.repository.book;

import com.bookstoreapp.dto.book.BookSearchParameters;
import com.bookstoreapp.controller.model.Book;
import com.bookstoreapp.repository.SpecificationBuilder;
import com.bookstoreapp.repository.SpecificationProviderManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class BookSpecificationBuilder implements SpecificationBuilder<Book> {

    private final SpecificationProviderManager<Book> bookSpecificationProviderManager;

    @Override
    public Specification<Book> build(BookSearchParameters searchParameters) {
        Specification<Book> spec = Specification.where(null);

        if (searchParameters.titles() != null && searchParameters.titles().length > 0) {
            spec = spec.or(bookSpecificationProviderManager
                    .getSpecificationProvider("title")
                    .getSpecification(searchParameters.titles()));
        }

        if (searchParameters.authors() != null && searchParameters.authors().length > 0) {
            spec = spec.or(bookSpecificationProviderManager
                    .getSpecificationProvider("author")
                    .getSpecification(searchParameters.authors()));
        }

        if (searchParameters.isbn() != null) {
            spec = spec.or(bookSpecificationProviderManager
                    .getSpecificationProvider("isbn")
                    .getSpecification(searchParameters.isbn()));
        }

        return spec;
    }
}
