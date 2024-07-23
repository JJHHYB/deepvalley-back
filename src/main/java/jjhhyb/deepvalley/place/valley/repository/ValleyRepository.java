package jjhhyb.deepvalley.place.valley.repository;

import jjhhyb.deepvalley.place.valley.Valley;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface ValleyRepository extends JpaRepository<Valley, Long>, CustomizedValleyRepository {

    Optional<Valley> findByUuid(String uuid);
}
