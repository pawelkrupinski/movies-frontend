package pawel.files

import org.junit.Test
import pawel.movies.model.Movie
import pawel.movies.services.MoviesService

class MoviesServiceTest : DatabaseTest() {
    
    val moviesService = MoviesService(database)

    @Test
    fun addAMovieWithNoPath() {
        val movie = Movie(title = "My Title")
        val movie2 = Movie(title = "My Title")
        
        moviesService.add(movie)
        moviesService.add(movie2)
    }
}
