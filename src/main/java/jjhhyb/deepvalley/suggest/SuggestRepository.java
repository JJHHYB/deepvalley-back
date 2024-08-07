package jjhhyb.deepvalley.suggest;

import jjhhyb.deepvalley.user.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SuggestRepository extends JpaRepository<Suggest, Long> {
    List<Suggest> findAllByPlace_Uuid(String uuid);
    List<Suggest> findAllByMember_loginEmail(String uuid);
    Optional<Suggest> findByUuid(String uuid);
    List<Suggest> findByVisitedDateAfter(LocalDate date);
    void deleteByMember(Member member);
}
