package com.gopinath.library_management.repository;

import com.gopinath.library_management.entity.IssueRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IssueRecordRepository extends JpaRepository<IssueRecord, Long> {

    long countByMemberIdAndReturnDateIsNull(Long memberId);

    List<IssueRecord> findByReturnDateIsNull();

    List<IssueRecord> findByMemberId(Long memberId);

    boolean existsByBookId(Long bookId);

    boolean existsByMemberId(Long memberId);
}