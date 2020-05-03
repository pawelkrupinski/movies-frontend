package pawel.movies.files

import com.mongodb.client.MongoDatabase
import org.litote.kmongo.`in`
import org.litote.kmongo.getCollection
import org.litote.kmongo.setValue
import pawel.movies.db.DB
import pawel.movies.model.Movie
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.streams.toList

interface ScanDirectory {
    fun scan(): List<Path>;
}

val Vuze_Directory = "/Users/pawel/Documents/Vuze Downloads"

class ScanForMovies(val directory: String = Vuze_Directory) : ScanDirectory {
    private val movieExtensions = setOf("mkv", "avi", "mp4")

    private fun isMovieFile(path: Path) = movieExtensions.contains(path.toFile().extension)

    override fun scan(): List<Path> {
        val files = Files.walk(Paths.get(directory))
            .filter { path -> Files.isRegularFile(path) }.toList()

        return files.filter(::isMovieFile)
    }
}

class UpdateMovies(val scanForMovies: ScanForMovies, 
                   val database: MongoDatabase) {

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
            .forEach { movie -> collection.insertOne(movie) }

        val missingFiles = existingMovies.filter { !it.missing }.map { it.path } - movies.map { it.path }

        collection.updateMany(
            Movie::path `in` missingFiles,
            setValue(Movie::missing, true)
        )
    }
}


fun main() {
    val database = DB().createProd()

    UpdateMovies(ScanForMovies(), database).update()
}

