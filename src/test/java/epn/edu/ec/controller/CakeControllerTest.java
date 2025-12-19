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

@WebMvcTest(value = CakeController.class, excludeAutoConfiguration = {SecurityAutoConfiguration.class})
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
            cakeId, "Mock Cake", "Mock cake description"
    );
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
        CakeResponse cakeResponse = CakeResponse.builder().
                id(2L)
                .title("New Cake")
                .description("New cake description")
                .build();

        when(cakeService.createCake(createCakeRequest)).thenReturn(cakeResponse);

        // ACT
        ResultActions result = mockMvc.perform(post("/cakes")
                .content("application/json")
                .content(objectMapper.writeValueAsString(createCakeRequest)));
        // ASSERT

        result.andExpect(status().isCreated());
        result.andExpect(content().contentType("application/json"));
        result.andExpect(content().json(mapper.writeValueAsString(cakeResponse)));
    }
}
