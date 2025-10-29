package edu.ucsb.cs156.example.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import edu.ucsb.cs156.example.entities.UCSBRecommendationRequest;
import edu.ucsb.cs156.example.errors.EntityNotFoundException;
import edu.ucsb.cs156.example.repositories.UCSBRecommendationRequestRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** This is a REST controller for UCSBDates */
@Tag(name = "UCSBRecommendationRequest")
@RequestMapping("/api/ucsbrecommendationrequest")
@RestController
@Slf4j
public class UCSBRecommendationRequestController extends ApiController {

  @Autowired UCSBRecommendationRequestRepository ucsbRecommendationRequest;

  /**
   * List all records in table
   *
   * @return an iterable of records
   */
  @Operation(summary = "List all records")
  @PreAuthorize("hasRole('ROLE_USER')")
  @GetMapping("/all")
  public Iterable<UCSBRecommendationRequest> allRecords() {
    Iterable<UCSBRecommendationRequest> records = ucsbRecommendationRequest.findAll();
    return records;
  }

  /**
   * Get a single date by id
   *
   * @param id the id of the date
   * @return a record
   */
  @Operation(summary = "Get a single record")
  @PreAuthorize("hasRole('ROLE_USER')")
  @GetMapping("")
  public UCSBRecommendationRequest getById(@Parameter(name = "id") @RequestParam Long id) {
    UCSBRecommendationRequest record =
        ucsbRecommendationRequest
            .findById(id)
            .orElseThrow(() -> new EntityNotFoundException(UCSBRecommendationRequest.class, id));

    return record;
  }

  /**
   * Create a new record
   *
   * @param requesterEmail
   * @param professorEmail
   * @param explanation
   * @param dateRequested
   * @param dateNeeded
   * @param done
   * @return the saved record
   */
  @Operation(summary = "Create a new record")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @PostMapping("/post")
  public UCSBRecommendationRequest postRecord(
      @Parameter(name = "requesterEmail") @RequestParam String requesterEmail,
      @Parameter(name = "professorEmail") @RequestParam String professorEmail,
      @Parameter(name = "explanation") @RequestParam String explanation,
      @Parameter(name = "dateNeeded") @RequestParam LocalDateTime dateNeeded,
      @Parameter(name = "done") @RequestParam Boolean done,
      @Parameter(
              name = "dateRequested",
              description =
                  "date (in iso format, e.g. YYYY-mm-ddTHH:MM:SS; see https://en.wikipedia.org/wiki/ISO_8601)")
          @RequestParam("dateRequested")
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
          LocalDateTime dateRequested)
      throws JsonProcessingException {

    // For an explanation of @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    // See: https://www.baeldung.com/spring-date-parameters

    log.info("localDateTime={}", dateRequested);

    UCSBRecommendationRequest record = new UCSBRecommendationRequest();
    record.setRequesterEmail(requesterEmail);
    record.setProfessorEmail(professorEmail);
    record.setExplanation(explanation);
    record.setDateRequested(dateRequested);
    record.setDateNeeded(dateNeeded);
    record.setDone(done);
    UCSBRecommendationRequest savedRecRequest = ucsbRecommendationRequest.save(record);

    return savedRecRequest;
  }
}
