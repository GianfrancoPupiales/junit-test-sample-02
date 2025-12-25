package epn.edu.ec.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import epn.edu.ec.model.cake.CakeResponse;
import epn.edu.ec.model.cake.CakesResponse;
import epn.edu.ec.model.cake.CreateCakeRequest;
import epn.edu.ec.service.CakeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import epn.edu.ec.exception.CakeNotFoundException;
import epn.edu.ec.model.cake.UpdateCakeRequest;
import java.util.Collections;

@WebMvcTest(value = CakeController.class, excludeAutoConfiguration = { SecurityAutoConfiguration.class })
@ActiveProfiles("test")
public class CakeControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper mapper;

        @MockitoBean
        private CakeService cakeService;

        private final long cakeId = 1L;
        private final CakeResponse mockCakeResponse = new CakeResponse(
                        cakeId, "Mock Cake", "Mock cake description");
        @Autowired
        private ObjectMapper objectMapper;

        @Test
        public void getCakes_shouldReturnListOfCakes() throws Exception {
                // ARRANGE
                CakesResponse cakesResponse = new CakesResponse(List.of(mockCakeResponse));
                when(cakeService.getCakes()).thenReturn(cakesResponse);

                // ACT
                ResultActions result = mockMvc.perform(get("/cakes")
                                .contentType("application/json"));

                // ASSERT
                result.andExpect(status().isOk());
                result.andExpect(content().contentType("application/json"));
                result.andExpect(content().json(mapper.writeValueAsString(cakesResponse)));

                verify(cakeService, times(1)).getCakes();
        }

        @Test
        public void createCake_shouldReturnCreatedCake() throws Exception {
                // ARRANGE
                // req
                CreateCakeRequest createCakeRequest = CreateCakeRequest.builder()
                                .title("New Cake")
                                .description("New cake description")
                                .build();

                // resp
                CakeResponse cakeResponse = CakeResponse.builder().id(2L)
                                .title("New Cake")
                                .description("New cake description")
                                .build();

                when(cakeService.createCake(createCakeRequest)).thenReturn(cakeResponse);

                // ACT
                ResultActions result = mockMvc.perform(post("/cakes")
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(createCakeRequest)));
                // ASSERT

                result.andExpect(status().isCreated());
                result.andExpect(content().contentType("application/json"));
                result.andExpect(content().json(mapper.writeValueAsString(cakeResponse)));
        }

        @Test
        public void getCakes_shouldReturnEmptyList() throws Exception {
                // ARRANGE
                CakesResponse cakesResponse = new CakesResponse(Collections.emptyList());
                when(cakeService.getCakes()).thenReturn(cakesResponse);

                // ACT
                ResultActions result = mockMvc.perform(get("/cakes")
                                .contentType("application/json"));

                // ASSERT
                result.andExpect(status().isOk());
                result.andExpect(content().contentType("application/json"));
                result.andExpect(content().json(mapper.writeValueAsString(cakesResponse)));

                verify(cakeService, times(1)).getCakes();
        }

        @Test
        public void getCakeById_shouldReturnCake() throws Exception {
                // ARRANGE
                when(cakeService.getCakeById(cakeId)).thenReturn(mockCakeResponse);

                // ACT
                ResultActions result = mockMvc.perform(get("/cakes/{id}", cakeId)
                                .contentType("application/json"));

                // ASSERT
                result.andExpect(status().isOk());
                result.andExpect(content().contentType("application/json"));
                result.andExpect(content().json(mapper.writeValueAsString(mockCakeResponse)));

                verify(cakeService, times(1)).getCakeById(cakeId);
        }

        @Test
        public void getCakeById_shouldReturnNotFound() throws Exception {
                // ARRANGE
                long nonExistentId = 99L;
                doThrow(new CakeNotFoundException()).when(cakeService).getCakeById(nonExistentId);

                // ACT
                ResultActions result = mockMvc.perform(get("/cakes/{id}", nonExistentId)
                                .contentType("application/json"));

                // ASSERT
                result.andExpect(status().isNotFound());

                verify(cakeService, times(1)).getCakeById(nonExistentId);
        }

        @Test
        public void updateCake_shouldUpdateCake() throws Exception {
                // ARRANGE
                UpdateCakeRequest updateCakeRequest = new UpdateCakeRequest();
                updateCakeRequest.setTitle("Updated Cake");
                updateCakeRequest.setDescription("Updated description");

                // ACT
                ResultActions result = mockMvc.perform(put("/cakes/{id}", cakeId)
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(updateCakeRequest)));

                // ASSERT
                result.andExpect(status().isNoContent());

                verify(cakeService, times(1)).updateCake(eq(cakeId), eq(updateCakeRequest));
        }

        @Test
        public void updateCake_shouldReturnNotFound() throws Exception {
                // ARRANGE
                long nonExistentId = 99L;
                UpdateCakeRequest updateCakeRequest = new UpdateCakeRequest();
                updateCakeRequest.setTitle("Updated Cake");
                updateCakeRequest.setDescription("Updated description");

                doThrow(new CakeNotFoundException()).when(cakeService).updateCake(eq(nonExistentId),
                                any(UpdateCakeRequest.class));

                // ACT
                ResultActions result = mockMvc.perform(put("/cakes/{id}", nonExistentId)
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(updateCakeRequest)));

                // ASSERT
                result.andExpect(status().isNotFound());

                verify(cakeService, times(1)).updateCake(eq(nonExistentId), any(UpdateCakeRequest.class));
        }

        @Test
        public void deleteCake_shouldDeleteCake() throws Exception {
                // ACT
                ResultActions result = mockMvc.perform(delete("/cakes/{id}", cakeId)
                                .contentType("application/json"));

                // ASSERT
                result.andExpect(status().isNoContent());

                verify(cakeService, times(1)).deleteCake(cakeId);
        }
}
