package br.com.giovanniramos.movierandomizer.services;

import br.com.giovanniramos.movierandomizer.services.usecases.AddMovieUseCase;
import br.com.giovanniramos.movierandomizer.services.usecases.GetMovieDetailsUseCase;
import br.com.giovanniramos.movierandomizer.services.usecases.LastWatchedMovieUseCase;
import br.com.giovanniramos.movierandomizer.services.usecases.MovieDrawUseCase;
import br.com.giovanniramos.movierandomizer.services.usecases.RemoveMovieUseCase;
import br.com.giovanniramos.movierandomizer.services.usecases.ShowMovieListUseCase;
import br.com.giovanniramos.movierandomizer.services.usecases.UpdateMovieCoverUseCase;
import br.com.giovanniramos.movierandomizer.services.usecases.UpdateMovieUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static br.com.giovanniramos.movierandomizer.mocks.MovieMock.movieModelMock;
import static br.com.giovanniramos.movierandomizer.mocks.MovieMock.movieModelWithMovieCoverMock;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MovieServiceTest {

    @Mock
    private AddMovieUseCase addMovieUseCase;

    @Mock
    private ShowMovieListUseCase showMovieListUseCase;

    @Mock
    private GetMovieDetailsUseCase getMovieDetailsUseCase;

    @Mock
    private MovieDrawUseCase movieDrawUseCase;

    @Mock
    private UpdateMovieUseCase updateMovieUseCase;

    @Mock
    private LastWatchedMovieUseCase lastWatchedMovieUseCase;

    @Mock
    private UpdateMovieCoverUseCase updateMovieCoverUseCase;

    @Mock
    private RemoveMovieUseCase removeMovieUseCase;

    @Mock
    private MultipartFile multiPartFile;

    @InjectMocks
    private MovieService movieService;

    @Test
    void shouldAddMovie() {
        final var movieModelMock = movieModelWithMovieCoverMock(multiPartFile);

        when(addMovieUseCase.execute(any())).thenReturn(movieModelMock);

        final var movieModel = assertDoesNotThrow(() -> movieService.addMovie(movieModelMock));

        assertEquals(movieModelMock.getName(), movieModel.getName());
        assertEquals(movieModelMock.getGenres().size(), movieModel.getGenres().size());
        assertEquals(movieModelMock.getMovieCoverId(), movieModel.getMovieCoverId());
        assertEquals(movieModelMock.getMovieType(), movieModel.getMovieType());
        assertEquals(movieModelMock.getIsFirstTimeWatching(), movieModel.getIsFirstTimeWatching());
        assertEquals(movieModelMock.getAddedBy(), movieModel.getAddedBy());
        assertEquals(movieModelMock.getNote(), movieModel.getNote());
        assertEquals(movieModelMock.getComments(), movieModel.getComments());
        assertEquals(movieModelMock.getDuration(), movieModel.getDuration());
        assertEquals(movieModelMock.getCreatedAt(), movieModel.getCreatedAt());
        assertEquals(movieModelMock.getMovieCoverUrl(), movieModel.getMovieCoverUrl());
    }

    @Test
    void shouldShowMovieList() {
        final var movieModelMock = movieModelMock();
        final var pageable = PageRequest.of(0, 10);

        when(showMovieListUseCase.execute(any(), any())).thenReturn(new PageImpl<>(List.of(movieModelMock), pageable, 1));

        final var movieModels = assertDoesNotThrow(() -> movieService.showMovieList(movieModelMock, pageable));
        final var movieModel = movieModels.getContent().getFirst();

        assertEquals(1, movieModels.getTotalElements());
        assertEquals(0, movieModels.getPageable().getPageNumber());
        assertEquals(10, movieModels.getPageable().getPageSize());
        assertEquals(1, movieModels.getTotalPages());
        assertEquals(movieModelMock.getName(), movieModel.getName());
        assertEquals(movieModelMock.getGenres().size(), movieModel.getGenres().size());
        assertEquals(movieModelMock.getMovieCoverId(), movieModel.getMovieCoverId());
        assertEquals(movieModelMock.getMovieType(), movieModel.getMovieType());
        assertEquals(movieModelMock.getIsFirstTimeWatching(), movieModel.getIsFirstTimeWatching());
        assertEquals(movieModelMock.getAddedBy(), movieModel.getAddedBy());
        assertEquals(movieModelMock.getNote(), movieModel.getNote());
        assertEquals(movieModelMock.getComments(), movieModel.getComments());
        assertEquals(movieModelMock.getDuration(), movieModel.getDuration());
        assertEquals(movieModelMock.getCreatedAt(), movieModel.getCreatedAt());
        assertEquals(movieModelMock.getMovieCoverUrl(), movieModel.getMovieCoverUrl());
    }

    @Test
    void shouldGetMovieDetails() {
        final var movieModelMock = movieModelMock();

        when(getMovieDetailsUseCase.execute(any())).thenReturn(movieModelMock);

        final var movieModel = assertDoesNotThrow(() -> movieService.getMovieDetails(UUID.randomUUID().toString()));

        assertEquals(movieModelMock.getName(), movieModel.getName());
        assertEquals(movieModelMock.getGenres().size(), movieModel.getGenres().size());
        assertEquals(movieModelMock.getMovieCoverId(), movieModel.getMovieCoverId());
        assertEquals(movieModelMock.getMovieType(), movieModel.getMovieType());
        assertEquals(movieModelMock.getIsFirstTimeWatching(), movieModel.getIsFirstTimeWatching());
        assertEquals(movieModelMock.getAddedBy(), movieModel.getAddedBy());
        assertEquals(movieModelMock.getNote(), movieModel.getNote());
        assertEquals(movieModelMock.getComments(), movieModel.getComments());
        assertEquals(movieModelMock.getDuration(), movieModel.getDuration());
        assertEquals(movieModelMock.getCreatedAt(), movieModel.getCreatedAt());
        assertEquals(movieModelMock.getMovieCoverUrl(), movieModel.getMovieCoverUrl());
    }

    @Test
    void shouldDrawMovie() {
        final var movieModelMock = movieModelMock();

        when(movieDrawUseCase.execute(any())).thenReturn(movieModelMock);

        final var movieModel = assertDoesNotThrow(() -> movieService.movieDraw(Set.of(movieModelMock.getId(), UUID.randomUUID().toString())));

        assertEquals(movieModelMock.getName(), movieModel.getName());
        assertEquals(movieModelMock.getGenres().size(), movieModel.getGenres().size());
        assertEquals(movieModelMock.getMovieCoverId(), movieModel.getMovieCoverId());
        assertEquals(movieModelMock.getMovieType(), movieModel.getMovieType());
        assertEquals(movieModelMock.getIsFirstTimeWatching(), movieModel.getIsFirstTimeWatching());
        assertEquals(movieModelMock.getAddedBy(), movieModel.getAddedBy());
        assertEquals(movieModelMock.getNote(), movieModel.getNote());
        assertEquals(movieModelMock.getComments(), movieModel.getComments());
        assertEquals(movieModelMock.getDuration(), movieModel.getDuration());
        assertEquals(movieModelMock.getCreatedAt(), movieModel.getCreatedAt());
        assertEquals(movieModelMock.getMovieCoverUrl(), movieModel.getMovieCoverUrl());
    }

    @Test
    void shouldUpdateMovie() {
        final var movieModelMock = movieModelMock();

        when(updateMovieUseCase.execute(any())).thenReturn(movieModelMock);

        final var movieModel = assertDoesNotThrow(() -> movieService.updateMovie(movieModelMock));

        assertEquals(movieModelMock.getName(), movieModel.getName());
        assertEquals(movieModelMock.getGenres().size(), movieModel.getGenres().size());
        assertEquals(movieModelMock.getMovieCoverId(), movieModel.getMovieCoverId());
        assertEquals(movieModelMock.getMovieType(), movieModel.getMovieType());
        assertEquals(movieModelMock.getIsFirstTimeWatching(), movieModel.getIsFirstTimeWatching());
        assertEquals(movieModelMock.getAddedBy(), movieModel.getAddedBy());
        assertEquals(movieModelMock.getNote(), movieModel.getNote());
        assertEquals(movieModelMock.getComments(), movieModel.getComments());
        assertEquals(movieModelMock.getDuration(), movieModel.getDuration());
        assertEquals(movieModelMock.getCreatedAt(), movieModel.getCreatedAt());
        assertEquals(movieModelMock.getMovieCoverUrl(), movieModel.getMovieCoverUrl());
    }

    @Test
    void shouldUpdateMovieCover() {
        assertDoesNotThrow(() -> movieService.updateMovieCover(UUID.randomUUID().toString(), multiPartFile));

        verify(updateMovieCoverUseCase, times(1)).execute(any(), any());
    }

    @Test
    void shouldReturnLastWatchedMovie() {
        final var movieModelMock = movieModelMock();

        when(lastWatchedMovieUseCase.execute()).thenReturn(movieModelMock);

        final var movieModel = assertDoesNotThrow(() -> movieService.lastWatchedMovie());

        assertEquals(movieModelMock.getName(), movieModel.getName());
        assertEquals(movieModelMock.getGenres().size(), movieModel.getGenres().size());
        assertEquals(movieModelMock.getMovieCoverId(), movieModel.getMovieCoverId());
        assertEquals(movieModelMock.getMovieType(), movieModel.getMovieType());
        assertEquals(movieModelMock.getIsFirstTimeWatching(), movieModel.getIsFirstTimeWatching());
        assertEquals(movieModelMock.getAddedBy(), movieModel.getAddedBy());
        assertEquals(movieModelMock.getNote(), movieModel.getNote());
        assertEquals(movieModelMock.getComments(), movieModel.getComments());
        assertEquals(movieModelMock.getDuration(), movieModel.getDuration());
        assertEquals(movieModelMock.getCreatedAt(), movieModel.getCreatedAt());
        assertEquals(movieModelMock.getMovieCoverUrl(), movieModel.getMovieCoverUrl());
    }

    @Test
    void shouldRemoveMovie() {
        assertDoesNotThrow(() -> movieService.removeMovie(UUID.randomUUID().toString()));

        verify(removeMovieUseCase, times(1)).execute(any());
    }
}