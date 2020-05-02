package pawel.movies.main

import com.mongodb.client.MongoDatabase
import pawel.movies.db.DB
import pawel.movies.files.ScanForMovies
import pawel.movies.files.UpdateMovies
import pawel.movies.files.Vuze_Directory
import pawel.movies.services.MovieClean
import pawel.movies.model.createGson
import pawel.movies.services.DbTokenService
import pawel.movies.services.MoviesService
import spark.Filter
import spark.kotlin.*

fun main() {
    main(8000)
}

fun main(
    port: Int = 8000,
    directory: String = Vuze_Directory,
    database: MongoDatabase = DB().createProd()
): Http {
    
    port(port)
    val http = ignite()

    val gson = createGson()

    val tokenService = DbTokenService(database)
    val movieClean = MovieClean(tokenService)
    val updateMovies = UpdateMovies(ScanForMovies(directory), database)
    val moviesService = MoviesService(database, movieClean)

    if (!moviesService.hasMovies()) {
        updateMovies.update()
    }

    get("/movies") {
        response.type("application/json")
        val movies = moviesService.findAll()
        gson.toJson(movies)
    }

    get("/movie/:id") {
        response.type("application/json")
        val id = request.params(":id")
        val movie = moviesService.findById(id)
        gson.toJson(movie)
    }
    
    delete("/movie/:id") {
        response.type("application/json")
        val id = request.params(":id")
        moviesService.delete(id)
        "{}"
    }
    
    put("/movie") {
        response.type("application/json")

        val json = gson.fromJson(request.body(), MutableMap::class.java)

        val id = json["id"] as String
        val title = json["title"] as String
        val poster = json["poster"] as String?
        val imdbId = json["imdbId"] as String?
        val year = yearAsInt(json["year"])

        val existingMovie = moviesService.findById(id)!!
        val edited = existingMovie.edited

        if (!edited) {
            tokenService.createTokensForEditing(existingMovie, title)
        }

        moviesService.update(id, title, year, poster, imdbId)

        val result = moviesService.findById(id)
        val jsonResponseBody = gson.toJson(result)
        jsonResponseBody
    }

    get("/tokens") {
        gson.toJson(tokenService.tokens())
    }

    delete("/token/:token") {
        tokenService.delete(request.params("token"))
        gson.toJson(tokenService.tokens())
    }

    post("scan") {
        updateMovies.update()
    }

    options("/*") {
        "OK"
    }

    before(Filter { _, response ->
        response.header("Access-Control-Allow-Origin", "*")
        response.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE")
    })

    return http
}

private fun yearAsInt(yearObj: Any?): Int? =
    if (yearObj != null) {
        when (yearObj) {
            is Double -> yearObj.toInt()
            is String -> yearObj.toInt()
            else -> throw RuntimeException("Unsupported type")
        }
    } else null                      




