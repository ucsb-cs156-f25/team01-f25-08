package edu.ucsb.cs156.example.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import edu.ucsb.cs156.example.entities.UCSBRecommendationRequest;
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
@Tag(name = "UCSBRecommendationRequestController") // fix
@RequestMapping("/api/ucsbRecommendationRequest") // fix
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
   * Create a new record
   *
   * @param requesterEmail done
   * @param professorEmail
   * @param explanation
   * @param dateRequested
   * @param dateNeeded
   * @param done
   * @param localDateTime done
   * @return the saved reccord
   */
  @Operation(summary = "Create a new record")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @PostMapping("/post")
  public UCSBRecommendationRequest postRecord(
      @Parameter(name = "requesterEmail") @RequestParam String requesterEmail,
      @Parameter(name = "professorEmail") @RequestParam String professorEmail,
      @Parameter(name = "explanation") @RequestParam String explanation,
      @Parameter(name = "dateRequested") @RequestParam LocalDateTime dateRequested,
      @Parameter(name = "dateNeeded") @RequestParam LocalDateTime dateNeeded,
      @Parameter(name = "done") @RequestParam Boolean done,
      @Parameter(
              name = "localDateTime",
              description =
                  "date (in iso format, e.g. YYYY-mm-ddTHH:MM:SS; see https://en.wikipedia.org/wiki/ISO_8601)")
          @RequestParam("localDateTime")
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
          LocalDateTime localDateTime)
      throws JsonProcessingException {

    // For an explanation of @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    // See: https://www.baeldung.com/spring-date-parameters

    log.info("localDateTime={}", localDateTime);

    UCSBRecommendationRequest record = new UCSBRecommendationRequest();
    record.setRequesterEmail(requesterEmail);
    record.setProfessorEmail(professorEmail);
    record.setExplanation(explanation);
    record.setDateRequested(dateRequested);
    record.setDateNeeded(dateNeeded);
    record.setDone(done);

    record.setLocalDateTime(localDateTime);

    UCSBRecommendationRequest savedRecRequest = ucsbRecommendationRequest.save(record);

    return savedRecRequest;
  }
}
