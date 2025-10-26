package edu.ucsb.cs156.example.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import edu.ucsb.cs156.example.entities.MenuItemReview;
import edu.ucsb.cs156.example.repositories.MenuItemReviewRepository;
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

/** This is a REST controller for MenuItemReview */
@Tag(name = "MenuItemReview")
@RequestMapping("/api/menuitemreview")
@RestController
@Slf4j
public class MenuItemReviewController extends ApiController {

  @Autowired MenuItemReviewRepository menuItemReviewRepository;

  /**
   * List all Menu Item Reviews
   *
   * @return an iterable of MenuItemReviews
   */
  @Operation(summary = "List all menu item reviews")
  @PreAuthorize("hasRole('ROLE_USER')")
  @GetMapping("/all")
  public Iterable<MenuItemReview> allMenuItemReviews() {
    Iterable<MenuItemReview> menuitemreview = menuItemReviewRepository.findAll();
    return menuitemreview;
  }

  /**
   * Create a new menu item review
   *
   * @param itemid the id in the UCSBDiningCommonsMenuItems table of a menu item
   * @param revieweremail the email of the reviewer
   * @param stars 0 to 5 stars
   * @param datereviewed the timestamp of the review
   * @param comments the comments in the review
   * @return the saved menu item review
   */
  @Operation(summary = "Create a new menu item review")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @PostMapping("/post")
  public MenuItemReview postMenuItemReview(
      @Parameter(name = "itemId") @RequestParam long itemId,
      @Parameter(name = "reviewerEmail") @RequestParam String reviewerEmail,
      @Parameter(name = "stars") @RequestParam int stars,
      @Parameter(name = "dateReviewed")
          @RequestParam("dateReviewed")
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
          LocalDateTime dateReviewed,
      @Parameter(name = "comments") @RequestParam String comments)
      throws JsonProcessingException {

    // For an explanation of @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    // See: https://www.baeldung.com/spring-date-parameters

    log.info("dateReviewed={}", dateReviewed);

    MenuItemReview menuitemreview = new MenuItemReview();
    menuitemreview.setItemId(itemId);
    menuitemreview.setReviewerEmail(reviewerEmail);
    menuitemreview.setStars(stars);
    menuitemreview.setDateReviewed(dateReviewed);
    menuitemreview.setComments(comments);

    MenuItemReview savedMenuItemReview = menuItemReviewRepository.save(menuitemreview);

    return savedMenuItemReview;
  }
}
