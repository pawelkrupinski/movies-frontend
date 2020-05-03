package pawel.movies.services

import com.mongodb.client.MongoDatabase
import org.litote.kmongo.*
import org.litote.kmongo.id.WrappedObjectId
import pawel.movies.model.Movie

class MoviesService(database: MongoDatabase) {
    
    private val moviesCollection = database.getCollection<Movie>("movies")
    private val tokenService = DbTokenService(database)
    private val movieClean = MovieClean()
    
    fun hasMovies(): Boolean = moviesCollection.find().cursor().hasNext()

    fun findDeleted(): List<Movie> = moviesCollection
        .find(Movie::removed eq true).toList().map(clean())
    
    fun findAll(): List<Movie> = moviesCollection
        .find(Movie::removed eq false).toList().map(clean())
    
    fun findById(id: String): Movie? = 
        moviesCollection
            .findOneById(movieId(id))
            ?.let(clean())

    fun update(id: String,
               title: String,
               year: Int?,
               poster: String?,
               imdbId: String?,
               removed: Boolean?
               ) {
        
        val listOfSets = mutableListOf(
            Movie::title setTo title,
            Movie::year setTo year,
            Movie::edited setTo true,
            Movie::poster setTo poster,
            Movie::imdbId setTo imdbId)

        removed.let { listOfSets.add(Movie::removed setTo it) }
        
        moviesCollection.updateOne(
            Movie::id eq movieId(id),
                    
            set(*listOfSets.toTypedArray())
        )
    }

    fun delete(id: String) = delete(listOf(id))

    fun delete(ids: Iterable<String>) {
        moviesCollection.updateMany(
            Movie::id `in` ids.map(::movieId),
            set(Movie::removed setTo true)
        )
    }
    
    fun clean(): (Movie) -> Movie {
        val tokens = tokenService.tokens()
        return { movie -> movieClean.clean(movie, tokens)}
    }
}

fun movieId(id: String) = WrappedObjectId<Movie>(id)