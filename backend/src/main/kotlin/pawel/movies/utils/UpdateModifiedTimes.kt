package pawel.movies.utils

import org.litote.kmongo.eq
import org.litote.kmongo.getCollection
import pawel.movies.db.DB
import pawel.movies.model.Movie
import java.nio.file.Path
import java.nio.file.Paths

fun main() {
    val database = DB().createProd()
    val moviesCollection = database.getCollection<Movie>("movies")

    moviesCollection.find().forEach { movie ->
        moviesCollection.updateOne(
            Movie::id eq movie.id,
            org.litote.kmongo.setValue(
                Movie::added,
                modifiedTime(Paths.get(movie.path))
            )
        )
    }
}

fun modifiedTime(path: Path): Long = try {
    path.toFile().lastModified()
} catch (e: Exception) {
    0
}