package edu.ucsb.cs156.example.repositories;

import edu.ucsb.cs156.example.entities.UCSBRecommendationRequest;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/** The UCSBDateRepository is a repository for UCSBDate entities. */
@Repository
public interface UCSBRecommendationRequestRepository
    extends CrudRepository<UCSBRecommendationRequest, Long> {}
