package br.com.giovanniramos.movierandomizer.mappers;

import br.com.giovanniramos.movierandomizer.controllers.requests.MovieCreateRequest;
import br.com.giovanniramos.movierandomizer.controllers.requests.MovieListParamsRequest;
import br.com.giovanniramos.movierandomizer.controllers.requests.MovieUpdateRequest;
import br.com.giovanniramos.movierandomizer.entities.MovieEntity;
import br.com.giovanniramos.movierandomizer.models.MovieModel;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static br.com.giovanniramos.movierandomizer.enums.MovieTypeEnum.MOVIE;
import static br.com.giovanniramos.movierandomizer.mappers.MovieMapper.MOVIE_MAPPER;
import static br.com.giovanniramos.movierandomizer.mocks.GenreMock.genreModelMock;
import static br.com.giovanniramos.movierandomizer.mocks.MovieMock.movieCreateRequestMock;
import static br.com.giovanniramos.movierandomizer.mocks.MovieMock.movieEntityMock;
import static br.com.giovanniramos.movierandomizer.mocks.MovieMock.movieListParamsRequestMock;
import static br.com.giovanniramos.movierandomizer.mocks.MovieMock.movieModelMock;
import static br.com.giovanniramos.movierandomizer.mocks.MovieMock.movieUpdateRequestMock;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MovieMapperTest {

    @ParameterizedTest
    @CsvSource(value = { "0", "1" })
    void shouldMapToMoviePageResponseFromPage(final int pageNumber) {
        final var movieModelPage = new PageImpl<>(List.of(movieModelMock(), movieModelMock()), PageRequest.of(pageNumber, 1), 2);
        final var movieResponsePageResponse = MOVIE_MAPPER.mapToMoviePageResponseFromPage(movieModelPage);

        assertEquals(movieModelPage.getTotalPages(), movieResponsePageResponse.totalPages());
        assertEquals(movieModelPage.getTotalPages(), movieResponsePageResponse.totalPages());
        assertEquals(movieModelPage.getPageable().getPageNumber(), movieResponsePageResponse.pageNumber());
        assertEquals(movieModelPage.getPageable().getPageSize(), movieResponsePageResponse.pageSize());
        assertEquals(movieModelPage.getTotalElements(), movieResponsePageResponse.totalElements());
        assertEquals(movieModelPage.isFirst(), movieResponsePageResponse.first());
        assertEquals(movieModelPage.isLast(), movieResponsePageResponse.last());
        assertEquals(movieModelPage.getContent().size(), movieResponsePageResponse.content().size());
    }

    @Test
    void shouldMapToMovieResponseFromMovieModel() {
        final var movieModel = movieModelMock();
        final var movieResponse = MOVIE_MAPPER.mapToMovieResponseFromMovieModel(movieModel);

        assertEquals(movieModel.getId(), movieResponse.id());
        assertEquals(movieModel.getName(), movieResponse.name());
        assertEquals(movieModel.getMovieCoverUrl(), movieResponse.movieCoverUrl());
        assertEquals(movieModel.getGenres().size(), movieResponse.genres().size());
        assertEquals(movieModel.getMovieType(), movieResponse.movieType());
        assertEquals(movieModel.getIsFirstTimeWatching(), movieResponse.isFirstTimeWatching());
        assertEquals(movieModel.getAddedBy(), movieResponse.addedBy());
        assertEquals(movieModel.getNote(), movieResponse.note());
        assertEquals(movieModel.getComments(), movieResponse.comments());
        assertEquals(movieModel.getDuration(), movieResponse.duration());
        assertEquals(movieModel.getCreatedAt(), movieResponse.createdAt());
    }

    @Test
    void shouldMapToUpdateMovieResponseFromMovieModel() {
        final var movieModel = movieModelMock();
        final var updateMovieResponse = MOVIE_MAPPER.mapToUpdateMovieResponseFromMovieModel(movieModel);

        assertEquals(movieModel.getId(), updateMovieResponse.id());
        assertEquals(movieModel.getName(), updateMovieResponse.name());
        assertEquals(movieModel.getGenres().size(), updateMovieResponse.genres().size());
        assertEquals(movieModel.getMovieType(), updateMovieResponse.movieType());
        assertEquals(movieModel.getIsFirstTimeWatching(), updateMovieResponse.isFirstTimeWatching());
        assertEquals(movieModel.getAddedBy(), updateMovieResponse.addedBy());
        assertEquals(movieModel.getNote(), updateMovieResponse.note());
        assertEquals(movieModel.getComments(), updateMovieResponse.comments());
        assertEquals(movieModel.getDuration(), updateMovieResponse.duration());
        assertEquals(movieModel.getCreatedAt(), updateMovieResponse.createdAt());
    }

    @Test
    void shouldMapToMovieModelFromMovieRequest() {
        final var movieCreateRequest = movieCreateRequestMock();
        final var movieModel = MOVIE_MAPPER.mapToMovieModelFromMovieRequest(movieCreateRequest);

        assertNull(movieModel.getId());
        assertNull(movieModel.getMovieCoverId());
        assertNotNull(movieModel.getCreatedAt());
        assertEquals(movieCreateRequest.name(), movieModel.getName());
        assertEquals(movieCreateRequest.movieCover(), movieModel.getMovieCover());
        assertEquals(movieCreateRequest.genres().size(), movieModel.getGenres().size());
        assertEquals(movieCreateRequest.movieType(), movieModel.getMovieType());
        assertEquals(movieCreateRequest.isFirstTimeWatching(), movieModel.getIsFirstTimeWatching());
        assertEquals(movieCreateRequest.addedBy(), movieModel.getAddedBy());
        assertEquals(movieCreateRequest.note(), movieModel.getNote());
        assertEquals(movieCreateRequest.comments(), movieModel.getComments());
        assertEquals(movieCreateRequest.duration(), movieModel.getDuration());
    }

    @Test
    void shouldMapToMovieModelFromMovieRequestWithIsFirstTimeWatchingDefaultValueFalse() {
        final var movieCreateRequest = new MovieCreateRequest(
                "Name",
                "Description",
                new MockMultipartFile("movieCover", "cover.jpg", MediaType.IMAGE_JPEG_VALUE, "file".getBytes()),
                Set.of(genreModelMock().getName()),
                MOVIE,
                null,
                "Tester",
                10,
                "Comment",
                LocalTime.of(1, 30)
        );
        final var movieModel = MOVIE_MAPPER.mapToMovieModelFromMovieRequest(movieCreateRequest);

        assertNull(movieModel.getId());
        assertNull(movieModel.getMovieCoverId());
        assertNotNull(movieModel.getCreatedAt());
        assertEquals(movieCreateRequest.name(), movieModel.getName());
        assertEquals(movieCreateRequest.movieCover(), movieModel.getMovieCover());
        assertEquals(movieCreateRequest.genres().size(), movieModel.getGenres().size());
        assertEquals(movieCreateRequest.movieType(), movieModel.getMovieType());
        assertEquals(false, movieModel.getIsFirstTimeWatching());
        assertEquals(movieCreateRequest.addedBy(), movieModel.getAddedBy());
        assertEquals(movieCreateRequest.note(), movieModel.getNote());
        assertEquals(movieCreateRequest.comments(), movieModel.getComments());
        assertEquals(movieCreateRequest.duration(), movieModel.getDuration());
    }

    @Test
    void shouldMapToMovieModelFromMovieEntity() {
        final var movieEntity = movieEntityMock();
        final var movieModel = MOVIE_MAPPER.mapToMovieModelFromMovieEntity(movieEntity);

        assertNull(movieModel.getMovieCover());
        assertEquals(movieEntity.getName(), movieModel.getName());
        assertEquals(movieEntity.getGenres().size(), movieModel.getGenres().size());
        assertEquals(movieEntity.getMovieCoverId(), movieModel.getMovieCoverId());
        assertEquals(movieEntity.getMovieType(), movieModel.getMovieType());
        assertEquals(movieEntity.getIsFirstTimeWatching(), movieModel.getIsFirstTimeWatching());
        assertEquals(movieEntity.getAddedBy(), movieModel.getAddedBy());
        assertEquals(movieEntity.getNote(), movieModel.getNote());
        assertEquals(movieEntity.getComments(), movieModel.getComments());
        assertEquals(movieEntity.getDuration(), movieModel.getDuration());
        assertEquals(movieEntity.getCreatedAt(), movieModel.getCreatedAt());
    }

    @Test
    void shouldMapToMovieEntityFromMovieModel() {
        final var movieModel = movieModelMock();
        final var movieEntity = MOVIE_MAPPER.mapToMovieEntityFromMovieModel(movieModel);

        assertEquals(movieModel.getId(), movieEntity.getId());
        assertEquals(movieModel.getName(), movieEntity.getName());
        assertEquals(movieModel.getMovieCoverId(), movieEntity.getMovieCoverId());
        assertEquals(movieModel.getGenres(), movieEntity.getGenres());
        assertEquals(movieModel.getMovieType(), movieEntity.getMovieType());
        assertEquals(movieModel.getIsFirstTimeWatching(), movieEntity.getIsFirstTimeWatching());
        assertEquals(movieModel.getAddedBy(), movieEntity.getAddedBy());
        assertEquals(movieModel.getNote(), movieEntity.getNote());
        assertEquals(movieModel.getComments(), movieEntity.getComments());
        assertEquals(movieModel.getDuration(), movieEntity.getDuration());
        assertEquals(movieModel.getCreatedAt(), movieEntity.getCreatedAt());
    }

    @Test
    void shouldMapToMovieModelFromMovieListParamsRequest() {
        final var movieListParamsRequest = movieListParamsRequestMock();
        final var movieModel = MOVIE_MAPPER.mapToMovieModelFromMovieListParamsRequest(movieListParamsRequest);

        assertNull(movieModel.getId());
        assertNull(movieModel.getMovieCover());
        assertNull(movieModel.getMovieCoverId());
        assertNull(movieModel.getMovieCoverUrl());
        assertNull(movieModel.getComments());
        assertNull(movieModel.getCreatedAt());
        assertEquals(movieListParamsRequest.name(), movieModel.getName());
        assertEquals(movieListParamsRequest.genres().size(), movieModel.getGenres().size());
        assertEquals(movieListParamsRequest.movieType(), movieModel.getMovieType());
        assertEquals(movieListParamsRequest.isFirstTimeWatching(), movieModel.getIsFirstTimeWatching());
        assertEquals(movieListParamsRequest.addedBy(), movieModel.getAddedBy());
        assertEquals(movieListParamsRequest.note(), movieModel.getNote());
        assertEquals(movieListParamsRequest.duration(), movieModel.getDuration());
    }

    @Test
    void shouldMapToMovieModelFromUpdateRequest() {
        final var id = UUID.randomUUID().toString();
        final var movieUpdateRequest = movieUpdateRequestMock();
        final var movieModel = MOVIE_MAPPER.mapToMovieModelFromUpdateRequest(id, movieUpdateRequest);

        assertNull(movieModel.getMovieCover());
        assertNull(movieModel.getMovieCoverId());
        assertNull(movieModel.getMovieCoverUrl());
        assertNull(movieModel.getCreatedAt());
        assertEquals(id, movieModel.getId());
        assertEquals(movieUpdateRequest.name(), movieModel.getName());
        assertEquals(movieUpdateRequest.genres().size(), movieModel.getGenres().size());
        assertEquals(movieUpdateRequest.movieType(), movieModel.getMovieType());
        assertEquals(movieUpdateRequest.isFirstTimeWatching(), movieModel.getIsFirstTimeWatching());
        assertEquals(movieUpdateRequest.addedBy(), movieModel.getAddedBy());
        assertEquals(movieUpdateRequest.note(), movieModel.getNote());
        assertEquals(movieUpdateRequest.comments(), movieModel.getComments());
        assertEquals(movieUpdateRequest.duration(), movieModel.getDuration());
    }

    @Test
    void shouldUpdateMovieEntityFromMovieModel() {
        final var movieEntity = movieEntityMock();
        final var movieModel = movieModelMock();
        final var movieEntityUpdated = MOVIE_MAPPER.updateMovieEntityFromMovieModel(movieModel, movieEntity);

        assertEquals(movieModel.getName(), movieEntityUpdated.getName());
        assertEquals(movieModel.getGenres(), movieEntityUpdated.getGenres());
        assertEquals(movieModel.getMovieType(), movieEntityUpdated.getMovieType());
        assertEquals(movieModel.getIsFirstTimeWatching(), movieEntityUpdated.getIsFirstTimeWatching());
        assertEquals(movieModel.getAddedBy(), movieEntityUpdated.getAddedBy());
        assertEquals(movieModel.getNote(), movieEntityUpdated.getNote());
        assertEquals(movieModel.getComments(), movieEntityUpdated.getComments());
        assertEquals(movieModel.getDuration(), movieEntityUpdated.getDuration());
    }

    @Test
    void shouldValidateNullCases() {
        assertNull(MOVIE_MAPPER.mapToMoviePageResponseFromPage(null));
        assertNull(MOVIE_MAPPER.mapToMoviePageResponseFromPage(new PageImpl<>(
                new ArrayList<>(), PageRequest.of(0, 10), 0)).content());
        assertNull(MOVIE_MAPPER.mapToMovieResponseFromMovieModel(null));
        assertNull(MOVIE_MAPPER.mapToMovieResponseFromMovieModel(MovieModel.builder().build()).genres());
        assertNull(MOVIE_MAPPER.mapToUpdateMovieResponseFromMovieModel(null));
        assertNull(MOVIE_MAPPER.mapToUpdateMovieResponseFromMovieModel(MovieModel.builder().build()).genres());
        assertNull(MOVIE_MAPPER.mapToMovieModelFromMovieRequest(null));
        assertNull(MOVIE_MAPPER.mapToMovieModelFromMovieRequest(new MovieCreateRequest(null, null, null, null,
                null, null, null, null, null, null)).getGenres());
        assertNull(MOVIE_MAPPER.mapToMovieModelFromMovieEntity(null));
        assertNull(MOVIE_MAPPER.mapToMovieModelFromMovieEntity(MovieEntity.builder().build()).getGenres());
        assertNull(MOVIE_MAPPER.mapToMovieEntityFromMovieModel(null));
        assertNull(MOVIE_MAPPER.mapToMovieEntityFromMovieModel(MovieModel.builder().build()).getGenres());
        assertNull(MOVIE_MAPPER.mapToMovieModelFromMovieListParamsRequest(null));
        assertNull(MOVIE_MAPPER.mapToMovieModelFromMovieListParamsRequest(new MovieListParamsRequest(null,
                null, null, null, null, null, null)).getGenres());
        assertNull(MOVIE_MAPPER.mapToMovieModelFromUpdateRequest(null, null));
        final var mapToMovieModelFromUpdateRequest = MOVIE_MAPPER
                .mapToMovieModelFromUpdateRequest(UUID.randomUUID().toString(), null);
        assertNull(mapToMovieModelFromUpdateRequest.getMovieType());
        assertNull(mapToMovieModelFromUpdateRequest.getGenres());
        assertNull(mapToMovieModelFromUpdateRequest.getIsFirstTimeWatching());
        assertNull(mapToMovieModelFromUpdateRequest.getAddedBy());
        assertNull(mapToMovieModelFromUpdateRequest.getNote());
        assertNull(mapToMovieModelFromUpdateRequest.getComments());
        assertNull(mapToMovieModelFromUpdateRequest.getDuration());
        assertNull(MOVIE_MAPPER.mapToMovieModelFromUpdateRequest(null, new MovieUpdateRequest(null, null, null,
                null, null, null, null, null, null)).getGenres());
    }

    @Test
    @SneakyThrows
    void shouldReturnContentNull() {
        final var movieModelListToMovieResponseList = MOVIE_MAPPER.getClass()
                .getDeclaredMethod("movieModelListToMovieResponseList", List.class);
        movieModelListToMovieResponseList.setAccessible(true);
        assertNull(movieModelListToMovieResponseList.invoke(MOVIE_MAPPER, (Object) null));
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldReturn0() {
        final var page = mock(Page.class);

        when(page.getPageable()).thenReturn(null);

        final var movieResponsePageResponse = MOVIE_MAPPER.mapToMoviePageResponseFromPage(page);

        assertEquals(0, movieResponsePageResponse.pageNumber());
        assertEquals(0, movieResponsePageResponse.pageSize());
    }
}