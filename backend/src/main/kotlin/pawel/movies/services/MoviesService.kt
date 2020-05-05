package pawel.movies.services

import com.mongodb.client.MongoDatabase
import com.mongodb.client.result.InsertOneResult
import org.bson.BsonDocument
import org.bson.BsonDocumentWrapper
import org.bson.BsonObjectId
import org.litote.kmongo.*
import org.litote.kmongo.id.WrappedObjectId
import pawel.movies.model.Movie

class MoviesService(val database: MongoDatabase) {
    
    private val moviesCollection = database.getCollection<Movie>("movies")
    private val tokenService = DbTokenService(database)
    private val movieClean = MovieClean()
    
    fun hasMovies(): Boolean = moviesCollection.find().cursor().hasNext()

    fun findDeleted(): List<Movie> = moviesCollection
        .find(Movie::removed eq true).toList().map(clean())
    
    fun findAll(): List<Movie> = moviesCollection
        .find(Movie::removed eq false).toList().map(clean())

    fun findById(id: Id<Movie>): Movie? = 
        moviesCollection.findOneById(id)?.let(clean())


    fun add(movie: Movie): Id<Movie> {
        val movieToInsert = decorateAdded(movie)

        val result = insertWithUniqieValuesRemoved(movieToInsert)

        return WrappedObjectId((result.insertedId as BsonObjectId).value)
    }

    private fun insertWithUniqieValuesRemoved(movie: Movie): InsertOneResult {
        val codecRegistry = moviesCollection.codecRegistry
        val documentWrapper = BsonDocumentWrapper.asBsonDocument(movie, codecRegistry)
        if (movie.path == null) {
            documentWrapper.remove("path")
        }
        if (movie.imdbId == null) {
            documentWrapper.remove("imdbId")
        }
        
        return moviesCollection
            .withDocumentClass<BsonDocument>()
            .insertOne(documentWrapper)
    }

    private fun decorateAdded(movie: Movie): Movie {
        val movieWithId = addIdIfMissing(movie)
        return addAddedTimeIfMissing(movieWithId)
    }

    private fun addAddedTimeIfMissing(movieWithId: Movie): Movie =
        if (movieWithId.added == 0L) 
            movieWithId.copy(added = System.currentTimeMillis()) 
        else movieWithId 
    
    private fun addIdIfMissing(movie: Movie): Movie =
        if (movie.id == null) movie.copy(id = newId()) 
        else movie

    fun update(movie: Movie) {
        moviesCollection.updateOne(
            Movie::id eq movie.id,
            set(Movie::title setTo movie.title,
                Movie::year setTo movie.year,
                Movie::edited setTo true,
                Movie::poster setTo movie.poster,
                Movie::imdbId setTo movie.imdbId,
                Movie::removed setTo movie.removed
            )
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