package com.example.parcial.parcial2.services;

import com.example.parcial.parcial2.domain.dtos.MovementRequestDto;
import com.example.parcial.parcial2.domain.entities.Book;
import com.example.parcial.parcial2.domain.entities.Lector;
import com.example.parcial.parcial2.domain.entities.Movement;
import com.example.parcial.parcial2.domain.entities.MovementType;
import com.example.parcial.parcial2.repositories.BookRepository;
import com.example.parcial.parcial2.repositories.LectorRepository;
import com.example.parcial.parcial2.repositories.MovementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;


@Service
public class MovementService {

    private final MovementRepository movementRepository;
    private final LectorRepository lectorRepository;
    private final BookRepository bookRepository;

    public MovementService(MovementRepository movementRepository,
                           LectorRepository lectorRepository,
                           BookRepository bookRepository) {
        this.movementRepository = movementRepository;
        this.lectorRepository = lectorRepository;
        this.bookRepository = bookRepository;
    }

    @Transactional
    public Movement borrowBook(MovementRequestDto dto) {
        Lector lector = getLector(dto);
        Book book = getBook(dto);

        if (hasActiveLoan(lector, book)) {
            throw new IllegalStateException("Lector already borrowed this book");
        }

        if (!book.isAvailable() || book.getAvailableCount() == 0) {
            throw new IllegalStateException("Book is not available");
        }

        book.setAvailableCount(book.getAvailableCount() - 1);
        book.setAvailable(book.getAvailableCount() > 0);
        bookRepository.save(book);

        return movementRepository.save(createMovement(lector, book, MovementType.BORROWING));
    }

    @Transactional
    public Movement returnBook(MovementRequestDto dto) {
        Lector lector = getLector(dto);
        Book book = getBook(dto);

        if (!hasActiveLoan(lector, book)) {
            throw new IllegalStateException("Lector has not borrowed this book");
        }

        book.setAvailableCount(book.getAvailableCount() + 1);
        book.setAvailable(true);
        bookRepository.save(book);

        return movementRepository.save(createMovement(lector, book, MovementType.RETURN));
    }

    private Lector getLector(MovementRequestDto dto) {
        return lectorRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Lector not found"));
    }

    private Book getBook(MovementRequestDto dto) {
        return bookRepository.findByIsbn(dto.getIsbn())
                .orElseThrow(() -> new IllegalArgumentException("Book not found"));
    }

    private boolean hasActiveLoan(Lector lector, Book book) {
        return movementRepository.findFirstByLectorIdAndBookIdOrderByTimestampDesc(lector.getId(), book.getId())
                .map(movement -> movement.getType() == MovementType.BORROWING)
                .orElse(false);
    }

    private Movement createMovement(Lector lector, Book book, MovementType type) {
        Movement movement = new Movement();
        movement.setLector(lector);
        movement.setBook(book);
        movement.setTimestamp(Instant.now());
        movement.setType(type);
        return movement;
    }
}
