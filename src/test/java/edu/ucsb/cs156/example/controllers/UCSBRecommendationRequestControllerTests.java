package edu.ucsb.cs156.example.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import edu.ucsb.cs156.example.ControllerTestCase;
import edu.ucsb.cs156.example.entities.UCSBRecommendationRequest;
import edu.ucsb.cs156.example.repositories.UCSBRecommendationRequestRepository;
import edu.ucsb.cs156.example.repositories.UserRepository;
import edu.ucsb.cs156.example.testconfig.TestConfig;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

@WebMvcTest(controllers = UCSBRecommendationRequestController.class)
@Import(TestConfig.class)
public class UCSBRecommendationRequestControllerTests extends ControllerTestCase {
  @MockBean UCSBRecommendationRequestRepository ucsbRecommendationRequest;

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

    when(ucsbRecommendationRequest.findAll()).thenReturn(requestsList);

    // act
    MvcResult response =
        mockMvc
            .perform(get("/api/ucsbrecommendationrequest/all"))
            .andExpect(status().isOk())
            .andReturn();

    // assert

    verify(ucsbRecommendationRequest, times(1)).findAll();
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

    when(ucsbRecommendationRequest.save(eq(record1))).thenReturn(record1);

    // act
    MvcResult response =
        mockMvc
            .perform(
                post("/api/ucsbrecommendationrequest/post?requesterEmail=sriya.vollala@gmail.com&professorEmail=sriyavollala@ucsb.edu&explanation=rara&dateNeeded=2022-01-03T00:00:00&dateRequested=2022-01-03T00:00:00&done=true")
                    .with(csrf()))
            .andExpect(status().isOk())
            .andReturn();

    // assert
    verify(ucsbRecommendationRequest, times(1)).save(record1);
    String expectedJson = mapper.writeValueAsString(record1);
    String responseString = response.getResponse().getContentAsString();
    assertEquals(expectedJson, responseString);
  }

  @Test
  public void logged_out_users_cannot_get_by_id() throws Exception {
    mockMvc
        .perform(get("/api/ucsbrecommendationrequest?id=7"))
        .andExpect(status().is(403)); // logged out users can't get by id
  }

  @WithMockUser(roles = {"USER"})
  @Test
  public void test_that_logged_in_user_can_get_by_id_when_the_id_exists() throws Exception {

    // arrange
    LocalDateTime ldt = LocalDateTime.parse("2022-01-03T00:00:00");

    UCSBRecommendationRequest record1 =
        UCSBRecommendationRequest.builder()
            .requesterEmail("sriya.vollala@gmail.com")
            .professorEmail("sriyavollala@ucsb.edu")
            .explanation("rara")
            .dateNeeded(ldt)
            .dateRequested(ldt)
            .done(true)
            .build();

    when(ucsbRecommendationRequest.findById(eq(7L))).thenReturn(Optional.of(record1));

    // act
    MvcResult response =
        mockMvc
            .perform(get("/api/ucsbrecommendationrequest?id=7"))
            .andExpect(status().isOk())
            .andReturn();

    // assert

    verify(ucsbRecommendationRequest, times(1)).findById(eq(7L));
    String expectedJson = mapper.writeValueAsString(record1);
    String responseString = response.getResponse().getContentAsString();
    assertEquals(expectedJson, responseString);
  }

  @WithMockUser(roles = {"USER"})
  @Test
  public void test_that_logged_in_user_can_get_by_id_when_the_id_does_not_exist() throws Exception {

    // arrange

    when(ucsbRecommendationRequest.findById(eq(7L))).thenReturn(Optional.empty());

    // act
    MvcResult response =
        mockMvc
            .perform(get("/api/ucsbrecommendationrequest?id=7"))
            .andExpect(status().isNotFound())
            .andReturn();

    // assert

    verify(ucsbRecommendationRequest, times(1)).findById(eq(7L));
    Map<String, Object> json = responseToJson(response);
    assertEquals("EntityNotFoundException", json.get("type"));
    assertEquals("UCSBRecommendationRequest with id 7 not found", json.get("message"));
  }

  @WithMockUser(roles = {"ADMIN", "USER"})
  @Test
  public void admin_can_edit_an_existing_ucsbrequest() throws Exception {

    LocalDateTime ldt1 = LocalDateTime.parse("2022-01-03T00:00:00");
    LocalDateTime ldt2 = LocalDateTime.parse("2023-01-04T00:00:00");

    UCSBRecommendationRequest record1 =
        UCSBRecommendationRequest.builder()
            .requesterEmail("sriya.vollala@gmail.com")
            .professorEmail("sriyavollala@ucsb.edu")
            .explanation("rara")
            .dateNeeded(ldt1)
            .dateRequested(ldt1)
            .done(true)
            .build();

    UCSBRecommendationRequest record2 =
        UCSBRecommendationRequest.builder()
            .requesterEmail("s@gmail.com")
            .professorEmail("s@ucsb.edu")
            .explanation("yaya")
            .dateNeeded(ldt2)
            .dateRequested(ldt2)
            .done(false)
            .build();

    String requestBody = mapper.writeValueAsString(record2);

    when(ucsbRecommendationRequest.findById(eq(67L))).thenReturn(Optional.of(record1));

    // act
    MvcResult response =
        mockMvc
            .perform(
                put("/api/ucsbrecommendationrequest?id=67")
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("utf-8")
                    .content(requestBody)
                    .with(csrf()))
            .andExpect(status().isOk())
            .andReturn();

    verify(ucsbRecommendationRequest, times(1)).findById(67L);
    verify(ucsbRecommendationRequest, times(1)).save(record2);
    String responseString = response.getResponse().getContentAsString();
    assertEquals(requestBody, responseString);
  }

  @WithMockUser(roles = {"ADMIN", "USER"})
  @Test
  public void admin_cannot_edit_ucsbrequest_that_does_not_exist() throws Exception {
    // arrange

    LocalDateTime ldt1 = LocalDateTime.parse("2022-01-03T00:00:00");
    UCSBRecommendationRequest record2 =
        UCSBRecommendationRequest.builder()
            .requesterEmail("sriya.vollala@gmail.com")
            .professorEmail("sriyavollala@ucsb.edu")
            .explanation("rara")
            .dateNeeded(ldt1)
            .dateRequested(ldt1)
            .done(true)
            .build();

    String requestBody = mapper.writeValueAsString(record2);

    when(ucsbRecommendationRequest.findById(eq(67L))).thenReturn(Optional.empty());

    // act
    MvcResult response =
        mockMvc
            .perform(
                put("/api/ucsbrecommendationrequest?id=67")
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("utf-8")
                    .content(requestBody)
                    .with(csrf()))
            .andExpect(status().isNotFound())
            .andReturn();

    // assert
    verify(ucsbRecommendationRequest, times(1)).findById(67L);
    Map<String, Object> json = responseToJson(response);
    assertEquals("UCSBRecommendationRequest with id 67 not found", json.get("message"));
  }
}
