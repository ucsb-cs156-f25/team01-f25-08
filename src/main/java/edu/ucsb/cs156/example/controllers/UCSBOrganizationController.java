package edu.ucsb.cs156.example.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import edu.ucsb.cs156.example.entities.UCSBOrganization;
import edu.ucsb.cs156.example.errors.EntityNotFoundException;
import edu.ucsb.cs156.example.repositories.UCSBOrganizationRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** This is a REST controller for UCSBOrganization */
@Tag(name = "UCSBOrganization")
@RequestMapping("/api/ucsborganization")
@RestController
@Slf4j
public class UCSBOrganizationController extends ApiController {

  @Autowired UCSBOrganizationRepository ucsbOrganizationRepository;

  /**
   * List all UCSB dates
   *
   * @return an iterable of UCSBDate
   */
  @Operation(summary = "List all ucsb organizations")
  @PreAuthorize("hasRole('ROLE_USER')")
  @GetMapping("/all")
  public Iterable<UCSBOrganization> allUCSBOrganization() {
    Iterable<UCSBOrganization> ucsbOrganization = ucsbOrganizationRepository.findAll();
    return ucsbOrganization;
  }

  /**
   * Create a new UCSBOrganization
   *
   * @param orgCode the UCSBOrganization orgCode
   * @param orgTranslationShort the UCSBOrganization orgTranslationShort
   * @param orgTranslation the UCSBOrganization orgTranslation
   * @param inactive the UCSBOrganization inactive
   * @return the saved UCSBOrganization
   */
  @Operation(summary = "Create a new UCSBOrganization")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @PostMapping("/post")
  public UCSBOrganization postUCSBOrganization(
      @Parameter(name = "orgCode") @RequestParam String orgCode,
      @Parameter(name = "orgTranslationShort") @RequestParam String orgTranslationShort,
      @Parameter(name = "orgTranslation") @RequestParam String orgTranslation,
      @Parameter(name = "inactive") @RequestParam Boolean inactive)
      // @Parameter(
      // name = "UCSBOrganizationTime",
      // description =
      // "date (in iso format, e.g. YYYY-mm-ddTHH:MM:SSZ; see
      // https://en.wikipedia.org/wiki/ISO_8601)")
      // @RequestParam("UCSBOrganizationTime")
      // @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
      // ZonedDateTime UCSBOrganizationTime)
      throws JsonProcessingException {

    // For an explanation of @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    // See: https://www.baeldung.com/spring-date-parameters

    // log.info("UCSBOrganizationTime={}", UCSBOrganizationTime);

    UCSBOrganization ucsbOrganization = new UCSBOrganization();
    ucsbOrganization.setOrgCode(orgCode);
    ucsbOrganization.setOrgTranslation(orgTranslation);
    ucsbOrganization.setOrgTranslationShort(orgTranslationShort);
    ucsbOrganization.setInactive(inactive);

    UCSBOrganization savedUCSBOrganization = ucsbOrganizationRepository.save(ucsbOrganization);

    return savedUCSBOrganization;
  }

  /**
   * Get a single UCSBOrganization by id
   *
   * @param id the id of the date
   * @return a UCSBOrganization
   */
  @Operation(summary = "Get a single UCSBOrganization")
  @PreAuthorize("hasRole('ROLE_USER')")
  @GetMapping("")
  public UCSBOrganization getById(@Parameter(name = "id") @RequestParam Long id) {
    UCSBOrganization ucsbOrganization =
        ucsbOrganizationRepository
            .findById(id)
            .orElseThrow(() -> new EntityNotFoundException(UCSBOrganization.class, id));

    return ucsbOrganization;
  }

  /**
   * Update a single ucsborganization
   *
   * @param id id of the ucsb organization to update
   * @param incoming the new ucsb organization
   * @return the updated ucsb organization object
   */
  @Operation(summary = "Update a single UCSBOrganization")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @PutMapping("")
  public UCSBOrganization updateUCSBOrganization(
      @Parameter(name = "id") @RequestParam Long id,
      @RequestBody @Valid UCSBOrganization incoming) {

    UCSBOrganization ucsbOrganization =
        ucsbOrganizationRepository
            .findById(id)
            .orElseThrow(() -> new EntityNotFoundException(UCSBOrganization.class, id));

    ucsbOrganization.setOrgCode(incoming.getOrgCode());
    ucsbOrganization.setOrgTranslation(incoming.getOrgTranslation());
    ucsbOrganization.setOrgTranslationShort(incoming.getOrgTranslationShort());
    ucsbOrganization.setInactive(incoming.getInactive());

    ucsbOrganizationRepository.save(ucsbOrganization);

    return ucsbOrganization;
  }
}
