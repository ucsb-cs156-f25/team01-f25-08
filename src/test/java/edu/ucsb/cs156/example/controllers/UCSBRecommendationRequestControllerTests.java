package edu.ucsb.cs156.example.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import edu.ucsb.cs156.example.ControllerTestCase;
import edu.ucsb.cs156.example.entities.UCSBRecommendationRequest;
import edu.ucsb.cs156.example.repositories.UCSBRecommendationRequestRepository;
import edu.ucsb.cs156.example.repositories.UserRepository;
import edu.ucsb.cs156.example.testconfig.TestConfig;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

@WebMvcTest(controllers = UCSBRecommendationRequestController.class)
@Import(TestConfig.class)
public class UCSBRecommendationRequestControllerTests extends ControllerTestCase {
  @MockBean UCSBRecommendationRequestRepository ucsbRecommendationRequestRepository;

  @MockBean UserRepository userRepository;

  @Test
  public void logged_out_users_cannot_get_all() throws Exception {
    mockMvc
        .perform(get("/api/ucsbrecommendationrequest/all"))
        .andExpect(status().is(403)); // logged out users can't get all
  }

  @WithMockUser(roles = {"USER"})
  @Test
  public void logged_in_users_can_get_all() throws Exception {
    mockMvc
        .perform(get("/api/ucsbrecommendationrequest/all"))
        .andExpect(status().is(200)); // logged
  }

  @Test
  public void logged_out_users_cannot_post() throws Exception {
    mockMvc.perform(post("/api/ucsbrecommendationrequest/post")).andExpect(status().is(403));
  }

  @WithMockUser(roles = {"USER"})
  @Test
  public void logged_in_regular_users_cannot_post() throws Exception {
    mockMvc
        .perform(post("/api/ucsbrecommendationrequest/post"))
        .andExpect(status().is(403)); // only admins can post
  }

  @WithMockUser(roles = {"USER"})
  @Test
  public void logged_in_user_can_get_all_requests() throws Exception {

    // arrange
    LocalDateTime ldt1 = LocalDateTime.parse("2022-01-03T00:00:00");

    UCSBRecommendationRequest record1 =
        UCSBRecommendationRequest.builder()
            .requesterEmail("sriya.vollala@gmail.com")
            .professorEmail("sriyavollala@ucsb.edu")
            .explanation("rara")
            .dateNeeded(ldt1)
            .dateRequested(ldt1)
            .done(true)
            .build();

    LocalDateTime ldt2 = LocalDateTime.parse("2022-03-11T00:00:00");

    UCSBRecommendationRequest record2 =
        UCSBRecommendationRequest.builder()
            .requesterEmail("sriya.vollala@gmail.com")
            .professorEmail("sriyavollala@ucsb.edu")
            .explanation("rara")
            .dateNeeded(ldt2)
            .dateRequested(ldt2)
            .done(true)
            .build();

    ArrayList<UCSBRecommendationRequest> requestsList = new ArrayList<>();
    requestsList.addAll(Arrays.asList(record1, record2));

    when(ucsbRecommendationRequestRepository.findAll()).thenReturn(requestsList);

    // act
    MvcResult response =
        mockMvc
            .perform(get("/api/ucsbrecommendationrequest/all"))
            .andExpect(status().isOk())
            .andReturn();

    // assert

    verify(ucsbRecommendationRequestRepository, times(1)).findAll();
    String expectedJson = mapper.writeValueAsString(requestsList);
    String responseString = response.getResponse().getContentAsString();
    assertEquals(expectedJson, responseString);
  }

  @WithMockUser(roles = {"ADMIN", "USER"})
  @Test
  public void an_admin_user_can_post_a_new_ucsbdate() throws Exception {
    // arrange

    LocalDateTime ldt1 = LocalDateTime.parse("2022-01-03T00:00:00");

    UCSBRecommendationRequest record1 =
        UCSBRecommendationRequest.builder()
            .requesterEmail("sriya.vollala@gmail.com")
            .professorEmail("sriyavollala@ucsb.edu")
            .explanation("rara")
            .dateNeeded(ldt1)
            .dateRequested(ldt1)
            .done(true)
            .build();

    when(ucsbRecommendationRequestRepository.save(eq(record1))).thenReturn(record1);

    // act
    MvcResult response =
        mockMvc
            .perform(
                post("/api/ucsbrecommendationrequest/post?requesterEmail=sriya.vollala@gmail.com&professorEmail=sriyavollala@ucsb.edu&explanation=rara&dateNeeded=2022-01-03T00:00:00&dateRequested=2022-01-03T00:00:00&done=true")
                    .with(csrf()))
            .andExpect(status().isOk())
            .andReturn();

    // assert
    verify(ucsbRecommendationRequestRepository, times(1)).save(record1);
    String expectedJson = mapper.writeValueAsString(record1);
    String responseString = response.getResponse().getContentAsString();
    assertEquals(expectedJson, responseString);
  }
}
