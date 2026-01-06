package br.com.giovanniramos.movierandomizer.services.usecases;

import br.com.giovanniramos.movierandomizer.entities.LastMovieDrawEntity;
import br.com.giovanniramos.movierandomizer.models.MovieModel;
import br.com.giovanniramos.movierandomizer.repositories.LastMovieDrawRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class MovieDrawUseCase {
    private static final SecureRandom RANDOM = new SecureRandom();

    private final GetMovieDetailsUseCase getMovieDetailsUseCase;
    private final LastMovieDrawRepository lastMovieDrawRepository;

    public MovieModel execute(final Set<String> moviesId) {
        final var randomIndex = RANDOM.nextInt(moviesId.size());
        final var movieId = new ArrayList<>(moviesId).get(randomIndex);
        final var movieModel = getMovieDetailsUseCase.execute(movieId);

        saveLastMovieDraw(movieModel);

        return movieModel;
    }

    private void saveLastMovieDraw(final MovieModel movieModel) {
        lastMovieDrawRepository.findFirstBy().ifPresentOrElse(lastMovieDrawEntity ->
                        lastMovieDrawRepository.save(lastMovieDrawEntity.toBuilder()
                                .lastDrawMovieId(movieModel.getId())
                                .build()),
                () -> lastMovieDrawRepository.save(LastMovieDrawEntity.builder()
                        .lastDrawMovieId(movieModel.getId())
                        .build()));
    }
}
