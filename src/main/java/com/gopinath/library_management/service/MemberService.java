package com.gopinath.library_management.service;

import com.gopinath.library_management.entity.Member;
import com.gopinath.library_management.repository.MemberRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import com.gopinath.library_management.repository.IssueRecordRepository;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final IssueRecordRepository issueRepository;

    public MemberService(MemberRepository memberRepository,
                         IssueRecordRepository issueRepository) {
        this.memberRepository = memberRepository;
        this.issueRepository = issueRepository;
    }

    public Member addMember(Member member) {
        if (member.getName() == null || member.getName().isBlank()
                || member.getEmail() == null || member.getEmail().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Name and email are required");
        }
        if (memberRepository.existsByEmail(member.getEmail())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Email already registered");
        }
        member.setId(null);
        return memberRepository.save(member);
    }

    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    public Member getMemberById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Member not found with id " + id));
    }

    public Member updateMember(Long id, Member updated) {
        Member existing = getMemberById(id);
        existing.setName(updated.getName());
        existing.setPhone(updated.getPhone());
        return memberRepository.save(existing);
    }

    public void deleteMember(Long id) {
        Member existing = getMemberById(id);
        if (issueRepository.existsByMemberId(id)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Cannot delete: this member has issue history");
        }
        memberRepository.delete(existing);
    }
}