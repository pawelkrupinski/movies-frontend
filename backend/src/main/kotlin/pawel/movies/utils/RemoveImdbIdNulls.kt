package pawel.movies.utils

import org.litote.kmongo.eq
import org.litote.kmongo.getCollection
import org.litote.kmongo.unset
import pawel.movies.db.DB
import pawel.movies.model.Movie

fun main() {
    val database = DB().createProd()
    val moviesCollection = database.getCollection<Movie>("movies")

    moviesCollection.find(Movie::imdbId eq null)
        .forEach { movie ->
            moviesCollection.updateOne(
                Movie::id eq movie.id,
                unset(Movie::imdbId)
            )
        }
}