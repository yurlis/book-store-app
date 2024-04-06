package com.bookstoreapp.repository.book;

import com.bookstoreapp.model.Book;
import com.bookstoreapp.repository.SpecificationProvider;
import com.bookstoreapp.repository.SpecificationProviderManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class BookSpecificationProviderManager implements SpecificationProviderManager<Book> {

    private final List<SpecificationProvider<Book>> bookSpecificationProviders;

    @Override
    public SpecificationProvider<Book> getSpecificationProvider(String key) {
        return bookSpecificationProviders.stream()
                .filter(p -> p.getKey().equals(key))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Can't find correct specification provider for key " + key));
    }
}
