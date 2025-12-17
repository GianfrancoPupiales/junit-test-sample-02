package epn.edu.ec.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
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
        long cakeId = 1L;
        Cake existingCake = Cake.builder().id(cakeId).title("Old Title").build();
        UpdateCakeRequest updateRequest = new UpdateCakeRequest();
        updateRequest.setTitle("Updated Chocolate Cake");
        updateRequest.setDescription("Updated delicious chocolate cake");

        when(cakeRepository.findById(cakeId)).thenReturn(Optional.of(existingCake));
        // Simular que el objeto ya actualizado
        when(cakeRepository.save(any(Cake.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // ACT
        cakeService.updateCake(cakeId, updateRequest);

        // ASSERT
        verify(cakeRepository).findById(cakeId);
        verify(cakeRepository).save(argThat(cake ->
                cake.getTitle().equals("Updated Chocolate Cake") &&
                        cake.getDescription().equals("Updated delicious chocolate cake")
        ));
    }

    @Test
    public void deleteCake_ShouldRemoveExistingCake() {
        // ARRANGE
        long cakeId = 1L;
        Cake cakeToDelete = Cake.builder().id(cakeId).title("To be deleted").build();
        when(cakeRepository.findById(cakeId)).thenReturn(Optional.of(cakeToDelete));

        // ACT
        cakeService.deleteCake(cakeId);

        // ASSERT
        verify(cakeRepository).findById(cakeId);
        verify(cakeRepository).delete(cakeToDelete);
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