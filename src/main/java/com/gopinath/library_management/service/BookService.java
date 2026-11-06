package com.gopinath.library_management.service;

import com.gopinath.library_management.entity.Book;
import com.gopinath.library_management.repository.BookRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.gopinath.library_management.repository.IssueRecordRepository;
import java.util.List;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final IssueRecordRepository issueRepository;

    public BookService(BookRepository bookRepository,
                       IssueRecordRepository issueRepository) {
        this.bookRepository = bookRepository;
        this.issueRepository = issueRepository;
    }

    public Book addBook(Book book) {
        book.setId(null);
        book.setAvailable(true);
        return bookRepository.save(book);
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Book getBookById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Book not found with id " + id));
    }

    public Book updateBook(Long id, Book updated) {
        Book existing = getBookById(id);
        existing.setTitle(updated.getTitle());
        existing.setAuthor(updated.getAuthor());
        existing.setIsbn(updated.getIsbn());
        existing.setCategory(updated.getCategory());
        return bookRepository.save(existing);
    }

    public void deleteBook(Long id) {
        Book existing = getBookById(id);
        if (issueRepository.existsByBookId(id)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Cannot delete: this book has issue history");
        }
        bookRepository.delete(existing);
    }

    public List<Book> searchBooks(String keyword) {
        return bookRepository
                .findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(keyword, keyword);
    }

    public List<Book> getAvailableBooks() {
        return bookRepository.findByAvailableTrue();
    }
}