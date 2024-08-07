package jjhhyb.deepvalley.suggest;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SuggestImageRepository extends JpaRepository<SuggestImage, SuggestImageId> {
    List<SuggestImage> findBySuggest_SuggestId(Long suggestId);
}