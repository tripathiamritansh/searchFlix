package amritansh.tripathi.com.searchflix.domain.usecases

import amritansh.tripathi.com.searchflix.network.Movie
import io.reactivex.Observable
import io.reactivex.Single

/**
 * Created by amritanshtripathi on 3/5/18.
 */
interface GetPopularMoviesUseCase {
    fun getPopularMovieUseCase(page: Int = 1): Observable<List<Movie>>
}