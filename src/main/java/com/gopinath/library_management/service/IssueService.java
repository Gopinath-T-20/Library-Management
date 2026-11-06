package com.gopinath.library_management.service;

import com.gopinath.library_management.entity.IssueRecord;
import com.gopinath.library_management.entity.Member;
import com.gopinath.library_management.entity.Book;
import com.gopinath.library_management.repository.BookRepository;
import com.gopinath.library_management.repository.IssueRecordRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class IssueService {

    private static final int MAX_BOOKS_PER_MEMBER = 3;
    private static final int LOAN_DAYS = 14;
    private static final double FINE_PER_DAY = 5.0;

    private final IssueRecordRepository issueRepository;
    private final BookRepository bookRepository;
    private final BookService bookService;
    private final MemberService memberService;

    public IssueService(IssueRecordRepository issueRepository,
                        BookRepository bookRepository,
                        BookService bookService,
                        MemberService memberService) {
        this.issueRepository = issueRepository;
        this.bookRepository = bookRepository;
        this.bookService = bookService;
        this.memberService = memberService;
    }

    @Transactional
    public IssueRecord issueBook(Long bookId, Long memberId) {
        Book book = bookService.getBookById(bookId);
        Member member = memberService.getMemberById(memberId);

        if (!book.isAvailable()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Book is already issued");
        }
        long current = issueRepository.countByMemberIdAndReturnDateIsNull(memberId);
        if (current >= MAX_BOOKS_PER_MEMBER) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Member has reached the limit of " + MAX_BOOKS_PER_MEMBER + " books");
        }

        book.setAvailable(false);
        bookRepository.save(book);

        IssueRecord record = new IssueRecord();
        record.setBook(book);
        record.setMember(member);
        record.setIssueDate(LocalDate.now());
        record.setDueDate(LocalDate.now().plusDays(LOAN_DAYS));
        return issueRepository.save(record);
    }

    @Transactional
    public IssueRecord returnBook(Long issueId) {
        IssueRecord record = issueRepository.findById(issueId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Issue record not found with id " + issueId));

        if (record.getReturnDate() != null) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Book already returned");
        }

        LocalDate today = LocalDate.now();
        record.setReturnDate(today);

        long daysLate = ChronoUnit.DAYS.between(record.getDueDate(), today);
        record.setFine(daysLate > 0 ? daysLate * FINE_PER_DAY : 0);

        Book book = record.getBook();
        book.setAvailable(true);
        bookRepository.save(book);

        return issueRepository.save(record);
    }

    public List<IssueRecord> getAllRecords() {
        return issueRepository.findAll();
    }

    public List<IssueRecord> getActiveRecords() {
        return issueRepository.findByReturnDateIsNull();
    }

    public List<IssueRecord> getRecordsByMember(Long memberId) {
        memberService.getMemberById(memberId);
        return issueRepository.findByMemberId(memberId);
    }
}