package com.gopinath.library_management.controller;

import com.gopinath.library_management.entity.IssueRecord;
import com.gopinath.library_management.service.IssueService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/issues")
public class IssueController {

    private final IssueService issueService;

    public IssueController(IssueService issueService) {
        this.issueService = issueService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public IssueRecord issueBook(@RequestParam Long bookId, @RequestParam Long memberId) {
        return issueService.issueBook(bookId, memberId);
    }

    @PutMapping("/{id}/return")
    public IssueRecord returnBook(@PathVariable Long id) {
        return issueService.returnBook(id);
    }

    @GetMapping
    public List<IssueRecord> getAllRecords() {
        return issueService.getAllRecords();
    }

    @GetMapping("/active")
    public List<IssueRecord> getActiveRecords() {
        return issueService.getActiveRecords();
    }

    @GetMapping("/member/{memberId}")
    public List<IssueRecord> getRecordsByMember(@PathVariable Long memberId) {
        return issueService.getRecordsByMember(memberId);
    }
}