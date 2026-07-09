package com.example.parcial.parcial2.services;

import com.example.parcial.parcial2.domain.dtos.BookRequestDto;
import com.example.parcial.parcial2.domain.dtos.GenreCountDto;
import com.example.parcial.parcial2.domain.entities.Book;
import com.example.parcial.parcial2.domain.entities.Genre;
import com.example.parcial.parcial2.repositories.BookRepository;
import org.springframework.stereotype.Service;

import java.util.*;

import static org.springframework.util.StringUtils.hasText;


@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Book createBook(BookRequestDto dto) {
        Book book = new Book();
        updateBookFromDto(book, dto);
        book.setActive(true);
        return bookRepository.save(book);
    }

    public Book getBookById(UUID id) {
        return findBookById(id);
    }

    public List<Book> getAllBooks(String author, String genre) {
        boolean hasAuthor = hasText(author);
        boolean hasGenre = hasText(genre);

        if (hasAuthor && hasGenre) {
            return bookRepository.findByAuthorAndGenre(author, parseGenre(genre));
        }

        if (hasAuthor) {
            return bookRepository.findByAuthor(author);
        }

        if (hasGenre) {
            return bookRepository.findByGenre(parseGenre(genre));
        }

        return bookRepository.findAll();
    }

    public Book updateBook(UUID id, BookRequestDto dto) {
        Book book = findBookById(id);
        updateBookFromDto(book, dto);
        return bookRepository.save(book);
    }

    public void deleteBook(UUID id) {
        Book book = findBookById(id);
        book.setActive(false);
        bookRepository.save(book);
    }

    public List<GenreCountDto> getGenresAvailable() {
        return bookRepository.countAvailableBooksByGenre();
    }

    private Genre parseGenre(String genre) {
        if (!hasText(genre)) {
            return null;
        }
        return Genre.valueOf(genre.trim().toUpperCase(Locale.ROOT));
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private Book findBookById(UUID id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Book not found"));
    }

    private void updateBookFromDto(Book book, BookRequestDto dto) {
        book.setTitle(dto.getTitle());
        book.setAuthor(dto.getAuthor());
        book.setGenre(parseGenre(dto.getGenre()));
        book.setIsbn(dto.getIsbn());
        book.setAvailable(dto.isAvailable());
        book.setAvailableCount(dto.getAvailableCount());
    }
}

