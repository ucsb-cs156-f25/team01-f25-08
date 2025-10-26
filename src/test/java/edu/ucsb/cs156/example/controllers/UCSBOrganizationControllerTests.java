package edu.ucsb.cs156.example.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import edu.ucsb.cs156.example.ControllerTestCase;
import edu.ucsb.cs156.example.entities.UCSBOrganization;
import edu.ucsb.cs156.example.repositories.UCSBOrganizationRepository;
import edu.ucsb.cs156.example.repositories.UserRepository;
import edu.ucsb.cs156.example.testconfig.TestConfig;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

@WebMvcTest(controllers = UCSBOrganizationController.class)
@Import(TestConfig.class)
public class UCSBOrganizationControllerTests extends ControllerTestCase {
  @MockBean UCSBOrganizationRepository ucsbOrganizationRepository;

  @MockBean UserRepository userRepository;

  // @Autowired
  // ObjectMapper mapper;

  @Test
  public void logged_out_users_cannot_get_all() throws Exception {
    mockMvc
        .perform(get("/api/ucsborganization/all"))
        .andExpect(status().is(403)); // logged out users can't get all
  }

  @WithMockUser(roles = {"USER"})
  @Test
  public void logged_in_users_can_get_all() throws Exception {
    mockMvc.perform(get("/api/ucsborganization/all")).andExpect(status().is(200)); // logged
  }

  @Test
  public void logged_out_users_cannot_post() throws Exception {
    mockMvc.perform(post("/api/ucsborganization/post")).andExpect(status().is(403));
  }

  @WithMockUser(roles = {"USER"})
  @Test
  public void logged_in_regular_users_cannot_post() throws Exception {
    mockMvc
        .perform(post("/api/ucsborganization/post"))
        .andExpect(status().is(403)); // only admins can post
  }

  @WithMockUser(roles = {"USER"})
  @Test
  public void logged_in_user_can_get_all_UCSBOrganization() throws Exception {

    // arrange
    // LocalDateTime ldt1 = LocalDateTime.parse("2022-01-03T00:00:00");

    // ZonedDateTime zdt1= ZonedDateTime.parse("2022-01-03T00:00:00Z");

    UCSBOrganization ucsbOrganization1 =
        UCSBOrganization.builder()
            .orgCode("ZPR")
            .orgTranslation("ZETA PHI RHO")
            .orgTranslationShort("ZETA PHI RHO")
            .inactive(true)
            .build();

    ArrayList<UCSBOrganization> expectedUCSBOrganization = new ArrayList<>();
    expectedUCSBOrganization.add(ucsbOrganization1);

    when(ucsbOrganizationRepository.findAll()).thenReturn(expectedUCSBOrganization);

    // act
    MvcResult response =
        mockMvc.perform(get("/api/ucsborganization/all")).andExpect(status().isOk()).andReturn();

    // assert

    verify(ucsbOrganizationRepository, times(1)).findAll();
    String expectedJson = mapper.writeValueAsString(expectedUCSBOrganization);
    String responseString = response.getResponse().getContentAsString();
    assertEquals(expectedJson, responseString);
  }

  @WithMockUser(roles = {"ADMIN", "USER"})
  @Test
  public void an_admin_user_can_post_a_new_UCSBOrganization() throws Exception {
    // arrange

    // ZonedDateTime ldt1 = LocalDateTime.parse("2022-01-03T00:00:00");

    UCSBOrganization ucsbOrganization1 =
        UCSBOrganization.builder()
            .orgCode("ZPR")
            .orgTranslation("ZETA PHI RHO")
            .orgTranslationShort("ZETA PHI RHO")
            .inactive(true)
            .build();

    when(ucsbOrganizationRepository.save(eq(ucsbOrganization1))).thenReturn(ucsbOrganization1);

    // act
    MvcResult response =
        mockMvc
            .perform(
                post("/api/ucsborganization/post?orgCode=ZPR&orgTranslation=ZETA PHI RHO&orgTranslationShort=ZETA PHI RHO&inactive=true")
                    .with(csrf()))
            .andExpect(status().isOk())
            .andReturn();

    // assert
    // assertEquals(false, ucsbOrganization1.getInactive(), "inactive field should
    // match request
    // param");
    verify(ucsbOrganizationRepository, times(1)).save(eq(ucsbOrganization1));
    String expectedJson = mapper.writeValueAsString(ucsbOrganization1);
    String responseString = response.getResponse().getContentAsString();
    // assert(responseString.contains("\"inactive\":false"));
    assertEquals(expectedJson, responseString);
  }

  @Test
  public void logged_out_users_cannot_get_by_id() throws Exception {
    mockMvc
        .perform(get("/api/ucsborganization?id=7"))
        .andExpect(status().is(403)); // logged out users can't get by id
  }

  @WithMockUser(roles = {"USER"})
  @Test
  public void test_that_logged_in_user_can_get_by_id_when_the_id_does_exist() throws Exception {

    // arrange
    UCSBOrganization ucsbOrganization1 =
        UCSBOrganization.builder()
            .orgCode("ZPR")
            .orgTranslation("ZETA PHI RHO")
            .orgTranslationShort("ZETA PHI RHO")
            .inactive(true)
            .build();

    when(ucsbOrganizationRepository.findById(eq(7L))).thenReturn(Optional.of(ucsbOrganization1));

    // act
    MvcResult response =
        mockMvc.perform(get("/api/ucsborganization?id=7")).andExpect(status().isOk()).andReturn();

    // assert

    verify(ucsbOrganizationRepository, times(1)).findById(eq(7L));
    String expectedJson = mapper.writeValueAsString(ucsbOrganization1);
    String responseString = response.getResponse().getContentAsString();
    assertEquals(expectedJson, responseString);
  }

  @WithMockUser(roles = {"USER"})
  @Test
  public void test_that_logged_in_user_can_get_by_id_when_the_id_does_not_exist() throws Exception {

    // arrange

    when(ucsbOrganizationRepository.findById(eq(7L))).thenReturn(Optional.empty());

    // act
    MvcResult response =
        mockMvc
            .perform(get("/api/ucsborganization?id=7"))
            .andExpect(status().isNotFound())
            .andReturn();

    // assert

    verify(ucsbOrganizationRepository, times(1)).findById(eq(7L));
    Map<String, Object> json = responseToJson(response);
    assertEquals("EntityNotFoundException", json.get("type"));
    assertEquals("UCSBOrganization with id 7 not found", json.get("message"));
  }

  @WithMockUser(roles = {"ADMIN", "USER"})
  @Test
  public void admin_can_edit_an_existing_UCSBOrganization() throws Exception {
    // arrange

    // LocalDateTime ldt1 = LocalDateTime.parse("2022-01-03T00:00:00");
    // LocalDateTime ldt2 = LocalDateTime.parse("2023-01-03T00:00:00");

    UCSBOrganization ucsbOrganization1 =
        UCSBOrganization.builder()
            .orgCode("ZPR")
            .orgTranslation("ZETA PHI RHO")
            .orgTranslationShort("ZETA PHI RHO")
            .inactive(true)
            .build();
    UCSBOrganization editeducsbOrganization =
        UCSBOrganization.builder()
            .orgCode("ABC")
            .orgTranslation("APPLE ELEPHENT CAT")
            .orgTranslationShort("APPLE ELEPHENT CAT")
            .inactive(false)
            .build();

    String requestBody = mapper.writeValueAsString(editeducsbOrganization);

    when(ucsbOrganizationRepository.findById(eq(67L))).thenReturn(Optional.of(ucsbOrganization1));

    // act
    MvcResult response =
        mockMvc
            .perform(
                put("/api/ucsborganization?id=67")
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("utf-8")
                    .content(requestBody)
                    .with(csrf()))
            .andExpect(status().isOk())
            .andReturn();

    // assert
    verify(ucsbOrganizationRepository, times(1)).findById(67L);
    verify(ucsbOrganizationRepository, times(1)).save(editeducsbOrganization);
    String responseString = response.getResponse().getContentAsString();
    assertEquals(requestBody, responseString);
  }

  @WithMockUser(roles = {"ADMIN", "USER"})
  @Test
  public void admin_cannot_edit_UCSBOrganization_that_does_not_exist() throws Exception {
    // arrange

    UCSBOrganization ucsbOrganization1 =
        UCSBOrganization.builder()
            .orgCode("ZPR")
            .orgTranslation("ZETA PHI RHO")
            .orgTranslationShort("ZETA PHI RHO")
            .inactive(true)
            .build();

    String requestBody = mapper.writeValueAsString(ucsbOrganization1);

    when(ucsbOrganizationRepository.findById(eq(67L))).thenReturn(Optional.empty());

    // act
    MvcResult response =
        mockMvc
            .perform(
                put("/api/ucsborganization?id=67")
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("utf-8")
                    .content(requestBody)
                    .with(csrf()))
            .andExpect(status().isNotFound())
            .andReturn();

    // assert
    verify(ucsbOrganizationRepository, times(1)).findById(67L);
    Map<String, Object> json = responseToJson(response);
    assertEquals("UCSBOrganization with id 67 not found", json.get("message"));
  }

  @WithMockUser(roles = {"ADMIN", "USER"})
  @Test
  public void admin_can_delete_a_UCSBOrganization() throws Exception {
    // arrange

    UCSBOrganization ucsbOrganization1 =
        UCSBOrganization.builder()
            .orgCode("ZPR")
            .orgTranslation("ZETA PHI RHO")
            .orgTranslationShort("ZETA PHI RHO")
            .inactive(true)
            .build();

    when(ucsbOrganizationRepository.findById(eq(15L))).thenReturn(Optional.of(ucsbOrganization1));

    // act
    MvcResult response =
        mockMvc
            .perform(delete("/api/ucsborganization?id=15").with(csrf()))
            .andExpect(status().isOk())
            .andReturn();

    // assert
    verify(ucsbOrganizationRepository, times(1)).findById(15L);
    verify(ucsbOrganizationRepository, times(1)).delete(eq(ucsbOrganization1));

    Map<String, Object> json = responseToJson(response);
    assertEquals("UCSBOrganization with id 15 deleted", json.get("message"));
  }

  @WithMockUser(roles = {"ADMIN", "USER"})
  @Test
  public void admin_tries_to_delete_non_existant_UCSBOrganization_and_gets_right_error_message()
      throws Exception {
    // arrange

    when(ucsbOrganizationRepository.findById(eq(15L))).thenReturn(Optional.empty());

    // act
    MvcResult response =
        mockMvc
            .perform(delete("/api/ucsborganization?id=15").with(csrf()))
            .andExpect(status().isNotFound())
            .andReturn();

    // assert
    verify(ucsbOrganizationRepository, times(1)).findById(15L);
    Map<String, Object> json = responseToJson(response);
    assertEquals("UCSBOrganization with id 15 not found", json.get("message"));
  }
}
