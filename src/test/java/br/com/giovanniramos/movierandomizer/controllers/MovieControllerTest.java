package br.com.giovanniramos.movierandomizer.controllers;

import br.com.giovanniramos.movierandomizer.controllers.requests.MovieDrawRequest;
import br.com.giovanniramos.movierandomizer.controllers.requests.MovieUpdateRequest;
import br.com.giovanniramos.movierandomizer.enums.MovieTypeEnum;
import br.com.giovanniramos.movierandomizer.exceptions.BadRequestException;
import br.com.giovanniramos.movierandomizer.exceptions.ConflictException;
import br.com.giovanniramos.movierandomizer.exceptions.NotFoundException;
import br.com.giovanniramos.movierandomizer.handlers.GlobalExceptionHandler;
import br.com.giovanniramos.movierandomizer.services.MovieService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static br.com.giovanniramos.movierandomizer.mocks.MovieMock.movieModelMock;
import static org.hamcrest.Matchers.endsWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class MovieControllerTest {
    private static final String BASE_URL = "/v1/movies";
    private static final ObjectMapper MAPPER = new ObjectMapper().registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @Mock
    private MovieService movieService;

    @InjectMocks
    private MovieController movieController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(movieController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @SneakyThrows
    void shouldAddMovie() {
        final var movieModel = movieModelMock();

        when(movieService.addMovie(any())).thenReturn(movieModel);

        mockMvc.perform(multipart(BASE_URL)
                        .file(validFile())
                        .param("name", "Irmão Urso")
                        .param("description", "Filme sobre um caçador que vira urso")
                        .param("genres", "ADVENTURE, ANIMATION, COMEDY, FAMILY")
                        .param("movieType", "MOVIE")
                        .param("isFirstTimeWatching", "false")
                        .param("addedBy", "Giovanni Ramos")
                        .param("comments", "Muito lindo esse filme")
                        .param("note", "10")
                        .param("duration", "01:25"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", endsWith(BASE_URL.concat("/").concat(movieModel.getId()))))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").exists())
                .andExpect(jsonPath("$.description").exists())
                .andExpect(jsonPath("$.movieCoverUrl").exists())
                .andExpect(jsonPath("$.genres").exists())
                .andExpect(jsonPath("$.movieType").exists())
                .andExpect(jsonPath("$.isFirstTimeWatching").exists())
                .andExpect(jsonPath("$.addedBy").exists())
                .andExpect(jsonPath("$.note").exists())
                .andExpect(jsonPath("$.comments").exists())
                .andExpect(jsonPath("$.duration").exists())
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    @SneakyThrows
    void shouldTryToAddMovieAndReturn400WhenInvalidGenreInformed() {
        when(movieService.addMovie(any())).thenThrow(new BadRequestException("Genre invalid"));

        mockMvc.perform(multipart(BASE_URL)
                        .file(validFile())
                        .param("name", "Irmão Urso")
                        .param("description", "Filme sobre um caçador que vira urso")
                        .param("genres", "INVALID_GENRE")
                        .param("movieType", "MOVIE")
                        .param("isFirstTimeWatching", "false")
                        .param("addedBy", "Giovanni Ramos")
                        .param("comments", "Muito lindo esse filme")
                        .param("note", "10")
                        .param("duration", "01:25"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void shouldTryToAddMovieAndReturn400WhenNameIsBlank() {
        mockMvc.perform(multipart(BASE_URL)
                        .file(validFile())
                        .param("name", "")
                        .param("description", "Filme sobre um caçador que vira urso")
                        .param("genres", "ACTION")
                        .param("movieType", "MOVIE")
                        .param("isFirstTimeWatching", "true")
                        .param("addedBy", "Tester")
                        .param("note", "8")
                        .param("comments", "Comentário")
                        .param("duration", "01:25"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void shouldTryToAddMovieAndReturn400WhenMovieCoverIsMissing() {
        mockMvc.perform(multipart(BASE_URL)
                        .param("name", "Filme")
                        .param("description", "Filme sobre um caçador que vira urso")
                        .param("genres", "ACTION")
                        .param("movieType", "MOVIE")
                        .param("isFirstTimeWatching", "true")
                        .param("addedBy", "Tester")
                        .param("note", "8")
                        .param("comments", "Comentário")
                        .param("duration", "01:25"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void shouldTryToAddMovieAndReturn400WhenGenresIsEmpty() {
        mockMvc.perform(multipart(BASE_URL)
                        .file(validFile())
                        .param("name", "Filme")
                        .param("description", "Filme sobre um caçador que vira urso")
                        .param("genres", "")
                        .param("movieType", "MOVIE")
                        .param("isFirstTimeWatching", "true")
                        .param("addedBy", "Tester")
                        .param("note", "8")
                        .param("comments", "Comentário")
                        .param("duration", "01:25"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void shouldTryToAddMovieAndReturn400WhenMovieTypeIsNull() {
        mockMvc.perform(multipart(BASE_URL)
                        .file(validFile())
                        .param("name", "Filme")
                        .param("description", "Filme sobre um caçador que vira urso")
                        .param("genres", "ACTION")
                        .param("isFirstTimeWatching", "true")
                        .param("addedBy", "Tester")
                        .param("note", "8")
                        .param("comments", "Comentário")
                        .param("duration", "01:25"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void shouldTryToAddMovieAndReturn400WhenAddedByIsBlank() {
        mockMvc.perform(multipart(BASE_URL)
                        .file(validFile())
                        .param("name", "Filme")
                        .param("description", "Filme sobre um caçador que vira urso")
                        .param("genres", "ACTION")
                        .param("movieType", "MOVIE")
                        .param("isFirstTimeWatching", "true")
                        .param("addedBy", "")
                        .param("note", "8")
                        .param("comments", "Comentário")
                        .param("duration", "01:25"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void shouldTryToAddMovieAndReturn400WhenNoteIsAboveMaximum() {
        mockMvc.perform(multipart(BASE_URL)
                        .file(validFile())
                        .param("name", "Filme")
                        .param("description", "Filme sobre um caçador que vira urso")
                        .param("genres", "ACTION")
                        .param("movieType", "MOVIE")
                        .param("isFirstTimeWatching", "true")
                        .param("addedBy", "Tester")
                        .param("note", "11")
                        .param("comments", "Comentário")
                        .param("duration", "01:25"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void shouldTryToAddMovieAndReturn400WhenNoteIsBelowMinimum() {
        mockMvc.perform(multipart(BASE_URL)
                        .file(validFile())
                        .param("name", "Filme")
                        .param("description", "Filme sobre um caçador que vira urso")
                        .param("genres", "ACTION")
                        .param("movieType", "MOVIE")
                        .param("isFirstTimeWatching", "true")
                        .param("addedBy", "Tester")
                        .param("note", "-1")
                        .param("comments", "Comentário")
                        .param("duration", "01:25"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void shouldTryToAddMovieAndReturn400WhenDurationIsNull() {
        mockMvc.perform(multipart(BASE_URL)
                        .file(validFile())
                        .param("name", "Filme")
                        .param("description", "Filme sobre um caçador que vira urso")
                        .param("genres", "ACTION")
                        .param("movieType", "MOVIE")
                        .param("isFirstTimeWatching", "true")
                        .param("addedBy", "Tester")
                        .param("note", "8")
                        .param("comments", "Comentário"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void shouldTryToAddMovieAndReturn409WhenMovieAlreadyExists() {
        when(movieService.addMovie(any())).thenThrow(new ConflictException("Movie already exists"));

        mockMvc.perform(multipart(BASE_URL)
                        .file(validFile())
                        .param("name", "Irmão Urso")
                        .param("description", "Filme sobre um caçador que vira urso")
                        .param("genres", "INVALID_GENRE")
                        .param("movieType", "MOVIE")
                        .param("isFirstTimeWatching", "false")
                        .param("addedBy", "Giovanni Ramos")
                        .param("comments", "Muito lindo esse filme")
                        .param("note", "10")
                        .param("duration", "01:25"))
                .andExpect(status().isConflict());
    }

    @Test
    @SneakyThrows
    void shouldTryToAddMovieAndReturn500WhenThrowUnexpectedException() {
        when(movieService.addMovie(any())).thenThrow(new RuntimeException());

        mockMvc.perform(multipart(BASE_URL)
                        .file(validFile())
                        .param("name", "Irmão Urso")
                        .param("description", "Filme sobre um caçador que vira urso")
                        .param("genres", "INVALID_GENRE")
                        .param("movieType", "MOVIE")
                        .param("isFirstTimeWatching", "false")
                        .param("addedBy", "Giovanni Ramos")
                        .param("comments", "Muito lindo esse filme")
                        .param("note", "10")
                        .param("duration", "01:25"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void shouldShowMovieList() throws Exception {
        when(movieService.showMovieList(any(), any()))
                .thenReturn(new PageImpl<>(List.of(movieModelMock()), PageRequest.of(0, 10), 1));

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.pageNumber").value(0))
                .andExpect(jsonPath("$.pageSize").value(10))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.first").value(true))
                .andExpect(jsonPath("$.last").value(true))
                .andExpect(jsonPath("$.content[0].id").exists())
                .andExpect(jsonPath("$.content[0].name").exists())
                .andExpect(jsonPath("$.content[0].description").exists())
                .andExpect(jsonPath("$.content[0].movieCoverUrl").exists())
                .andExpect(jsonPath("$.content[0].genres").exists())
                .andExpect(jsonPath("$.content[0].movieType").exists())
                .andExpect(jsonPath("$.content[0].isFirstTimeWatching").exists())
                .andExpect(jsonPath("$.content[0].addedBy").exists())
                .andExpect(jsonPath("$.content[0].note").exists())
                .andExpect(jsonPath("$.content[0].comments").exists())
                .andExpect(jsonPath("$.content[0].duration").exists())
                .andExpect(jsonPath("$.content[0].createdAt").exists());
    }

    @Test
    @SneakyThrows
    void shouldTryToShowMovieAndReturn500WhenThrowUnexpectedException() {
        when(movieService.showMovieList(any(), any())).thenThrow(new RuntimeException());

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @SneakyThrows
    void shouldGetMovieDetails() {
        final var movieModel = movieModelMock();

        when(movieService.getMovieDetails(any())).thenReturn(movieModel);

        mockMvc.perform(get(BASE_URL.concat("/").concat(movieModel.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").exists())
                .andExpect(jsonPath("$.description").exists())
                .andExpect(jsonPath("$.movieCoverUrl").exists())
                .andExpect(jsonPath("$.genres").exists())
                .andExpect(jsonPath("$.movieType").exists())
                .andExpect(jsonPath("$.isFirstTimeWatching").exists())
                .andExpect(jsonPath("$.addedBy").exists())
                .andExpect(jsonPath("$.note").exists())
                .andExpect(jsonPath("$.comments").exists())
                .andExpect(jsonPath("$.duration").exists())
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    @SneakyThrows
    void shouldTryToGetMovieDetailsAndReturn404WhenMovieDoesNotExist() {
        when(movieService.getMovieDetails(any())).thenThrow(new NotFoundException("Movie not found"));

        mockMvc.perform(get(BASE_URL.concat("/").concat(UUID.randomUUID().toString())))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void shouldTryToGetMovieDetailsAndReturn500WhenThrowUnexpectedException() {
        when(movieService.getMovieDetails(any())).thenThrow(new RuntimeException());

        mockMvc.perform(get(BASE_URL.concat("/").concat(UUID.randomUUID().toString())))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @SneakyThrows
    void shouldMovieDraw() {
        final var movieModel = movieModelMock();

        when(movieService.movieDraw(any())).thenReturn(movieModel);

        mockMvc.perform(get(BASE_URL.concat("/draw"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MAPPER.writeValueAsString(new MovieDrawRequest(Set.of(movieModel.getId())))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").exists())
                .andExpect(jsonPath("$.description").exists())
                .andExpect(jsonPath("$.movieCoverUrl").exists())
                .andExpect(jsonPath("$.genres").exists())
                .andExpect(jsonPath("$.movieType").exists())
                .andExpect(jsonPath("$.isFirstTimeWatching").exists())
                .andExpect(jsonPath("$.addedBy").exists())
                .andExpect(jsonPath("$.note").exists())
                .andExpect(jsonPath("$.comments").exists())
                .andExpect(jsonPath("$.duration").exists())
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    @SneakyThrows
    void shouldTryToMovieDrawAndReturn400WhenMovieIdsListIsNull() {
        mockMvc.perform(get(BASE_URL.concat("/draw"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MAPPER.writeValueAsString(new MovieDrawRequest(null))))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void shouldTryToMovieDrawAndReturn400WhenMovieIdsListIsEmpty() {
        mockMvc.perform(get(BASE_URL.concat("/draw"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MAPPER.writeValueAsString(new MovieDrawRequest(new HashSet<>()))))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void shouldTryToMovieDrawAndReturn404WhenMovieIdIsNotFound() {
        when(movieService.movieDraw(any())).thenThrow(new NotFoundException("Movie not found"));

        mockMvc.perform(get(BASE_URL.concat("/draw"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MAPPER.writeValueAsString(new MovieDrawRequest(Set.of(UUID.randomUUID().toString())))))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void shouldTryToMovieDrawAndReturn500WhenThrowUnexpectedException() {
        when(movieService.movieDraw(any())).thenThrow(new RuntimeException());

        mockMvc.perform(get(BASE_URL.concat("/draw"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MAPPER.writeValueAsString(new MovieDrawRequest(Set.of(UUID.randomUUID().toString())))))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @SneakyThrows
    void shouldUpdateMovie() {
        final var movieModel = movieModelMock();
        final var movieUpdateRequest = new MovieUpdateRequest("Name", "Description", Set.of("ACTION"),
                MovieTypeEnum.MOVIE, true, "Giovanni", 10, "Test",
                LocalTime.of(1, 10));

        when(movieService.updateMovie(any())).thenReturn(movieModel);

        mockMvc.perform(put(BASE_URL.concat("/").concat(movieModel.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MAPPER.writeValueAsString(movieUpdateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").exists())
                .andExpect(jsonPath("$.description").exists())
                .andExpect(jsonPath("$.movieCoverUrl").doesNotExist())
                .andExpect(jsonPath("$.genres").exists())
                .andExpect(jsonPath("$.movieType").exists())
                .andExpect(jsonPath("$.isFirstTimeWatching").exists())
                .andExpect(jsonPath("$.addedBy").exists())
                .andExpect(jsonPath("$.note").exists())
                .andExpect(jsonPath("$.comments").exists())
                .andExpect(jsonPath("$.duration").exists())
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @SneakyThrows
    @ParameterizedTest
    @CsvSource({ "-1", "11" })
    void shouldTryToUpdateMovieAndReturn400WhenNoteIsInvalid(final Integer note) {
        final var movieUpdateRequest = new MovieUpdateRequest("Name", "Description", Set.of("ACTION"),
                MovieTypeEnum.MOVIE, true, "Giovanni", note, "Test",
                LocalTime.of(1, 10));

        mockMvc.perform(put(BASE_URL.concat("/").concat(UUID.randomUUID().toString()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MAPPER.writeValueAsString(movieUpdateRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void shouldTryToUpdateMovieAndReturn404WhenMovieNotFound() {
        final var movieUpdateRequest = new MovieUpdateRequest("Name", "Description", Set.of("ACTION"),
                MovieTypeEnum.MOVIE, true, "Giovanni", 10, "Test",
                LocalTime.of(1, 10));

        when(movieService.updateMovie(any())).thenThrow(new NotFoundException("Movie not found"));

        mockMvc.perform(put(BASE_URL.concat("/").concat(UUID.randomUUID().toString()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MAPPER.writeValueAsString(movieUpdateRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void shouldTryToUpdateMovieAndReturn500WhenThrowUnexpectedException() {
        final var movieUpdateRequest = new MovieUpdateRequest("Name", "Description", Set.of("ACTION"),
                MovieTypeEnum.MOVIE, true, "Giovanni", 10, "Test",
                LocalTime.of(1, 10));

        when(movieService.updateMovie(any())).thenThrow(new RuntimeException());

        mockMvc.perform(put(BASE_URL.concat("/").concat(UUID.randomUUID().toString()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MAPPER.writeValueAsString(movieUpdateRequest)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @SneakyThrows
    void shouldUpdateMovieCover() {
        mockMvc.perform(multipart(BASE_URL.concat("/".concat(UUID.randomUUID().toString()).concat("/cover")))
                        .file(validFile())
                        .with(request -> {
                            request.setMethod("PATCH");
                            return request;
                        }))
                .andExpect(status().isNoContent());

        verify(movieService, times(1)).updateMovieCover(any(), any());
    }

    @Test
    @SneakyThrows
    void shouldTryToUpdateMovieCoverAndReturn404WhenMovieNotFound() {
        doThrow(new NotFoundException("Movie not found")).when(movieService).updateMovieCover(any(), any());

        mockMvc.perform(multipart(BASE_URL.concat("/".concat(UUID.randomUUID().toString()).concat("/cover")))
                        .file(validFile())
                        .with(request -> {
                            request.setMethod("PATCH");
                            return request;
                        }))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void shouldTryToUpdateMovieCoverAndReturn500WhenThrowUnexpected() {
        doThrow(new RuntimeException()).when(movieService).updateMovieCover(any(), any());

        mockMvc.perform(multipart(BASE_URL.concat("/".concat(UUID.randomUUID().toString()).concat("/cover")))
                        .file(validFile())
                        .with(request -> {
                            request.setMethod("PATCH");
                            return request;
                        }))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @SneakyThrows
    void shouldReturnLastWatchedMovie() {
        when(movieService.lastWatchedMovie()).thenReturn(movieModelMock());

        mockMvc.perform(get(BASE_URL.concat("/last-watched")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").exists())
                .andExpect(jsonPath("$.description").exists())
                .andExpect(jsonPath("$.movieCoverUrl").exists())
                .andExpect(jsonPath("$.genres").exists())
                .andExpect(jsonPath("$.movieType").exists())
                .andExpect(jsonPath("$.isFirstTimeWatching").exists())
                .andExpect(jsonPath("$.addedBy").exists())
                .andExpect(jsonPath("$.note").exists())
                .andExpect(jsonPath("$.comments").exists())
                .andExpect(jsonPath("$.duration").exists())
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    @SneakyThrows
    void shouldTryToFindLastWatchedMovieAndReturn404WhenMovieNotFound() {
        when(movieService.lastWatchedMovie()).thenThrow(new NotFoundException("No movies watched yet"));

        mockMvc.perform(get(BASE_URL.concat("/last-watched")))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void shouldTryToFindLastWatchedMovieAndReturn500WhenThrowUnexpectedException() {
        when(movieService.lastWatchedMovie()).thenThrow(new RuntimeException());

        mockMvc.perform(get(BASE_URL.concat("/last-watched")))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @SneakyThrows
    void shouldRemoveMovie() {
        mockMvc.perform(delete(BASE_URL.concat("/".concat(UUID.randomUUID().toString()))))
                .andExpect(status().isNoContent());

        verify(movieService, times(1)).removeMovie(any());
    }

    @Test
    @SneakyThrows
    void shouldTryToRemoveMovieAndReturn404WhenMovieNotFound() {
        doThrow(new NotFoundException("Movie not found")).when(movieService).removeMovie(any());

        mockMvc.perform(delete(BASE_URL.concat("/".concat(UUID.randomUUID().toString()))))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void shouldTryToRemoveMovieAndReturn500WhenThrowUnexpectedException() {
        doThrow(new RuntimeException()).when(movieService).removeMovie(any());

        mockMvc.perform(delete(BASE_URL.concat("/".concat(UUID.randomUUID().toString()))))
                .andExpect(status().isInternalServerError());
    }

    private MockMultipartFile validFile() {
        return new MockMultipartFile("movieCover", "cover.jpg", MediaType.IMAGE_JPEG_VALUE,
                "file".getBytes());
    }
}