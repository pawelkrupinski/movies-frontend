package pawel.movies.files

import com.mongodb.client.MongoDatabase
import org.litote.kmongo.`in`
import org.litote.kmongo.getCollection
import pawel.movies.db.DB
import pawel.movies.model.Movie
import pawel.movies.services.MoviesService

fun main() {
    val database = DB().createProd()

    UpdateMovies(ScanForMovies(), database).update()
}

class UpdateMovies(val scanForMovies: ScanForMovies,
                   val database: MongoDatabase) {
  
    val movieService = MoviesService(database)

    fun update() {
        val collection = database.getCollection<Movie>("movies")
        val existingMovies = collection.find().toSet()

        val movies = scanForMovies.scan()
            .map { path ->
                Movie(
                    path = path.toString(),
                    title = path.toFile().nameWithoutExtension,
                    added = path.toFile().lastModified()
                )
            }

        movies
            .filter { movie -> !existingMovies.any { m -> m.path == movie.path } }
            .forEach { movie -> movieService.add(movie) }

        val missingFiles = existingMovies.filter { !it.missing }.map { it.path } - 
                movies.map { it.path }

        collection.updateMany(
            Movie::path `in` missingFiles,
            org.litote.kmongo.setValue(Movie::missing, true)
        )
    }
}