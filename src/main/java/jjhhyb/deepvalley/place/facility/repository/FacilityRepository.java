package jjhhyb.deepvalley.place.facility.repository;

import jjhhyb.deepvalley.place.facility.Facility;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FacilityRepository extends JpaRepository<Facility, Long>, CustomizedFacilityRepository {
}
