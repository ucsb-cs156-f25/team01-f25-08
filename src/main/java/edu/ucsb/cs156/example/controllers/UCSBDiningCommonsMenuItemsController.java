package edu.ucsb.cs156.example.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import edu.ucsb.cs156.example.entities.UCSBDiningCommonsMenuItems;
import edu.ucsb.cs156.example.errors.EntityNotFoundException;
import edu.ucsb.cs156.example.repositories.UCSBDiningCommonsMenuItemsRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** This is a REST controller for UCSBDiningCommonsMenuItems */
@Tag(name = "UCSBDiningCommonsMenuItems")
@RequestMapping("/api/ucsbdiningcommonsmenuitems")
@RestController
@Slf4j
public class UCSBDiningCommonsMenuItemsController extends ApiController {

  @Autowired UCSBDiningCommonsMenuItemsRepository ucsbDiningCommonsMenuItemsRepository;

  /**
   * List all UCSB dining commons menu items
   *
   * @return an iterable of UCSBDiningCommonsMenuItems
   */
  @Operation(summary = "List all ucsb dining commons menu items")
  @PreAuthorize("hasRole('ROLE_USER')")
  @GetMapping("/all")
  public Iterable<UCSBDiningCommonsMenuItems> allUCSBDiningCommonsMenuItems() {
    Iterable<UCSBDiningCommonsMenuItems> items = ucsbDiningCommonsMenuItemsRepository.findAll();
    return items;
  }

  /**
   * Get a single menu item by id
   *
   * @param id the id of the menu item
   * @return a UCSBDiningCommonsMenuItems
   */
  @Operation(summary = "Get a single dining commons menu item")
  @PreAuthorize("hasRole('ROLE_USER')")
  @GetMapping("")
  public UCSBDiningCommonsMenuItems getById(@Parameter(name = "id") @RequestParam Long id) {
    UCSBDiningCommonsMenuItems ucsbDiningCommonsMenuItems =
        ucsbDiningCommonsMenuItemsRepository
            .findById(id)
            .orElseThrow(() -> new EntityNotFoundException(UCSBDiningCommonsMenuItems.class, id));

    return ucsbDiningCommonsMenuItems;
  }

  /**
   * Create a new menu item
   *
   * @param diningCommonsCode the dining commons code
   * @param name the name of the menu item
   * @param station the station
   * @return the saved ucsbdiningcommonsmenuitems
   */
  @Operation(summary = "Create a new dining commons menu item")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @PostMapping("/post")
  public UCSBDiningCommonsMenuItems postUCSBDiningCommonsMenuItems(
      @Parameter(name = "diningCommonsCode") @RequestParam String diningCommonsCode,
      @Parameter(name = "name") @RequestParam String name,
      @Parameter(name = "station") @RequestParam String station)
      throws JsonProcessingException {

    UCSBDiningCommonsMenuItems items = new UCSBDiningCommonsMenuItems();
    items.setDiningCommonsCode(diningCommonsCode);
    items.setName(name);
    items.setStation(station);

    UCSBDiningCommonsMenuItems savedItems = ucsbDiningCommonsMenuItemsRepository.save(items);

    return savedItems;
  }

  /**
   * Update a single menu item
   *
   * @param id id of the menu item to update
   * @param incoming the new menu item
   * @return the updated menu item object
   */
  @Operation(summary = "Update a single dining commons menu item")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @PutMapping("")
  public UCSBDiningCommonsMenuItems updateUCSBDiningCommonsMenuItems(
      @Parameter(name = "id") @RequestParam Long id,
      @RequestBody @Valid UCSBDiningCommonsMenuItems incoming) {

    UCSBDiningCommonsMenuItems ucsbDiningCommonsMenuItems =
        ucsbDiningCommonsMenuItemsRepository
            .findById(id)
            .orElseThrow(() -> new EntityNotFoundException(UCSBDiningCommonsMenuItems.class, id));

    ucsbDiningCommonsMenuItems.setDiningCommonsCode(incoming.getDiningCommonsCode());
    ucsbDiningCommonsMenuItems.setName(incoming.getName());
    ucsbDiningCommonsMenuItems.setStation(incoming.getStation());

    ucsbDiningCommonsMenuItemsRepository.save(ucsbDiningCommonsMenuItems);

    return ucsbDiningCommonsMenuItems;
  }

  /**
   * Delete a UCSBDiningComonsMenuItems
   *
   * @param id the id of the menu item to delete
   * @return a message indicating the menu item was deleted
   */
  @Operation(summary = "Delete a UCSBDiningCommonsMenuItems")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @DeleteMapping("")
  public Object deleteUCSBDiningCommonsMenuItems(@Parameter(name = "id") @RequestParam Long id) {
    UCSBDiningCommonsMenuItems ucsbDiningCommonsMenuItems =
        ucsbDiningCommonsMenuItemsRepository
            .findById(id)
            .orElseThrow(() -> new EntityNotFoundException(UCSBDiningCommonsMenuItems.class, id));

    ucsbDiningCommonsMenuItemsRepository.delete(ucsbDiningCommonsMenuItems);
    return genericMessage("UCSBDiningCommonsMenuItems with id %s deleted".formatted(id));
  }
}
