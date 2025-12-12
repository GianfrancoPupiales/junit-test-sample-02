package epn.edu.ec.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import epn.edu.ec.exception.CakeNotFoundException;
import epn.edu.ec.model.cake.CakeResponse;
import epn.edu.ec.model.cake.CreateCakeRequest;
import epn.edu.ec.model.cake.UpdateCakeRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import epn.edu.ec.repository.CakeRepository;
import epn.edu.ec.repository.model.Cake;
import epn.edu.ec.model.cake.CakesResponse;

@ExtendWith(MockitoExtension.class)
class CakeServiceTest {

    @Mock
    private CakeRepository cakeRepository;

    @InjectMocks
    private CakeService cakeService;

    private Cake cakeA;
    private Cake cakeB;

    @BeforeEach
    void setUp() {
        cakeA = Cake.builder()
                .id(1L)
                .title("Chocolate Cake")
                .description("Delicious chocolate cake")
                .build();

        cakeB = Cake.builder()
                .id(2L)
                .title("Vanilla Cake")
                .description("Tasty vanilla cake")
                .build();
    }

    @Test
    void getCakes_ShouldReturnAllCakesSortedByTitle() {
        // ARRANGE
        List<Cake> cakes = Arrays.asList(cakeB, cakeA);
        when(cakeRepository.findAll()).thenReturn(cakes);

        // ACT
        CakesResponse cakesResponse = cakeService.getCakes();

        // ASSERT
        assertNotNull(cakesResponse);
        assertEquals(2, cakesResponse.getCakes().size());
        assertEquals("Chocolate Cake", cakesResponse.getCakes().get(0).getTitle());
        assertEquals("Vanilla Cake", cakesResponse.getCakes().get(1).getTitle());
    }

    @Test
    public void getCakeById_ShouldReturnCake_WhenCakeExists() {
        // ARRANGE
        when(cakeRepository.findById(1L)).thenReturn(Optional.of(cakeA));

        // ACT
        CakeResponse cakeResponse = cakeService.getCakeById(1L);

        // ASSERT
        assertNotNull(cakeResponse);
        assertEquals("Chocolate Cake", cakeResponse.getTitle());
        assertEquals(1L, cakeResponse.getId());
    }

    @Test
    public void getCakeById_ShouldThrowException_WhenCakeDoesNotExist() {
        // ARRANGE
        long nonExistentCakeId = 999L;
        when(cakeRepository.findById(nonExistentCakeId)).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThrows(CakeNotFoundException.class, () -> {
            cakeService.getCakeById(nonExistentCakeId);
        });
    }

    @Test
    public void createCake_ShouldSaveAndReturnNewCake() {
        // ARRANGE
        Cake newCake = Cake.builder()
                .id(3L)
                .title("Red Velvet Cake")
                .description("Delicious Red Velvet Cake")
                .build();

        when(cakeRepository.save(any(Cake.class))).thenReturn(newCake);

        // ACT
        CreateCakeRequest createCakeRequest = CreateCakeRequest
                .builder()
                .title("Red Velvet Cake")
                .description("Delicious Red Velvet Cake")
                .build();

        CakeResponse cakeResponse = cakeService.createCake(createCakeRequest);

        // ASSERT
        assertNotNull(cakeResponse);
        assertEquals(3L, cakeResponse.getId());
        assertEquals("Red Velvet Cake", cakeResponse.getTitle());
        assertEquals("Delicious Red Velvet Cake", cakeResponse.getDescription());
    }

    @Test
    public void updateCake_ShouldUpdateExistingCake() {
        // ARRANGE
        Cake updatedCake = Cake.builder()
                .id(1L)
                .title("Updated Chocolate Cake")
                .description("Updated delicious chocolate cake")
                .build();

        when(cakeRepository.findById(1L)).thenReturn(Optional.of(cakeA));
        when(cakeRepository.save(any(Cake.class))).thenReturn(updatedCake);

        // ACT
        UpdateCakeRequest updateCakeRequest = new UpdateCakeRequest();
        updateCakeRequest.setTitle("Updated Chocolate Cake");
        updateCakeRequest.setDescription("Updated delicious chocolate cake");

        CakeResponse cakeResponse = cakeService.updateCake(1L, updateCakeRequest);

        // ASSERT
        assertNotNull(cakeResponse);
        assertEquals(1L, cakeResponse.getId());
        assertEquals("Updated Chocolate Cake", cakeResponse.getTitle());
        assertEquals("Updated delicious chocolate cake", cakeResponse.getDescription());
        verify(cakeRepository).findById(1L);
        verify(cakeRepository).save(any(Cake.class));
    }

    @Test
    public void deleteCake_ShouldRemoveExistingCake() {
        // ARRANGE
        when(cakeRepository.findById(1L)).thenReturn(Optional.of(cakeA));

        // ACT
        cakeService.deleteCake(1L);

        // ASSERT
        verify(cakeRepository).findById(1L);
        verify(cakeRepository).delete(cakeA);
    }

    @Test
    public void deleteCake_ShouldThrowException_WhenCakeDoesNotExist() {
        // ARRANGE
        long nonExistentCakeId = 999L;
        when(cakeRepository.findById(nonExistentCakeId)).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThrows(CakeNotFoundException.class, () -> {
            cakeService.deleteCake(nonExistentCakeId);
        });
    }
}