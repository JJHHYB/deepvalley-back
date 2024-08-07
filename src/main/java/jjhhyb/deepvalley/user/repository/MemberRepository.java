package jjhhyb.deepvalley.user.repository;

import jjhhyb.deepvalley.user.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByLoginEmail(String loginEmail);
    Optional<Member> findByName(String name);
    Optional<Member> findByLoginEmailAndPassword(String loginEmail, String password);
}

// memberId, name, loginEmail, password, profileImageUrl, createdDate, loginDate