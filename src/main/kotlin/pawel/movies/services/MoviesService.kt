package pawel.movies.services

import com.mongodb.client.MongoDatabase
import org.litote.kmongo.*
import org.litote.kmongo.id.WrappedObjectId
import pawel.movies.model.Movie

class MoviesService(database: MongoDatabase, 
                    private val movieClean: MovieClean) {
    
    private val moviesCollection = database.getCollection<Movie>("movies")

    fun hasMovies(): Boolean = moviesCollection.find().cursor().hasNext()
    
    fun findAll(): List<Movie> = moviesCollection
        .find(Movie::removed eq false).toList().map(movieClean::clean)
    
    fun findById(id: String): Movie? = 
        moviesCollection
            .findOneById(movieId(id))
            ?.let(movieClean::clean)

    fun update(id: String, 
               title: String, 
               year: Int?, 
               poster: String?, 
               imdbId: String?) {
        moviesCollection.updateOne(
            Movie::id eq movieId(id),
            set(
                Movie::title setTo title,
                Movie::year setTo year,
                Movie::edited setTo true,
                Movie::poster setTo poster,
                Movie::imdbId setTo imdbId
            )
        )
    }

    fun delete(id: String) {
        moviesCollection.updateOne(
            Movie::id eq movieId(id),
            set(Movie::removed setTo true)
        )
    }
    
    private fun movieId(id: String) = WrappedObjectId<Movie>(id)
}