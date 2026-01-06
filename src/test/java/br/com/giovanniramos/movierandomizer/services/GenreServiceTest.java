package br.com.giovanniramos.movierandomizer.services;

import br.com.giovanniramos.movierandomizer.services.usecases.AddGenreUseCase;
import br.com.giovanniramos.movierandomizer.services.usecases.ListGenresUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static br.com.giovanniramos.movierandomizer.mocks.GenreMock.genreModelMock;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GenreServiceTest {

    @Mock
    private AddGenreUseCase addGenreUseCase;

    @Mock
    private ListGenresUseCase listGenresUseCase;

    @InjectMocks
    private GenreService genreService;

    @Test
    void shouldAddGenre() {
        final var genreModelMock = genreModelMock();

        when(addGenreUseCase.execute(any())).thenReturn(genreModelMock);

        final var genreModel = assertDoesNotThrow(() -> genreService.addGenre(genreModelMock));

        assertEquals(genreModelMock.getId(), genreModel.getId());
        assertEquals(genreModelMock.getName(), genreModel.getName());
        assertEquals(genreModelMock.getDescription(), genreModel.getDescription());

        verify(addGenreUseCase, times(1)).execute(any());
    }

    @Test
    void shouldListGenres() {
        final var genreModelMock = genreModelMock();
        when(listGenresUseCase.execute()).thenReturn(List.of(genreModelMock));

        final var genreModel = assertDoesNotThrow(() -> genreService.listGenres()).getFirst();

        assertEquals(genreModelMock.getId(), genreModel.getId());
        assertEquals(genreModelMock.getName(), genreModel.getName());
        assertEquals(genreModelMock.getDescription(), genreModel.getDescription());

        verify(listGenresUseCase, times(1)).execute();
    }
}