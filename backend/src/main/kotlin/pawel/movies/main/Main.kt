package pawel.movies.main

import com.mongodb.client.MongoDatabase
import org.eclipse.jetty.server.Request
import org.litote.kmongo.eq
import org.litote.kmongo.findOneById
import org.litote.kmongo.getCollection
import pawel.movies.db.DB
import pawel.movies.files.ScanForMovies
import pawel.movies.files.UpdateMovies
import pawel.movies.files.Vuze_Directory
import pawel.movies.model.Movie
import pawel.movies.model.Poster
import pawel.movies.model.createGson
import pawel.movies.services.DbTokenService
import pawel.movies.services.MoviesService
import pawel.movies.services.movieId
import spark.Filter
import spark.kotlin.*
import java.io.ByteArrayOutputStream
import javax.servlet.MultipartConfigElement


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
    val updateMovies = UpdateMovies(ScanForMovies(directory), database)
    val moviesService = MoviesService(database)

    if (!moviesService.hasMovies()) {
        updateMovies.update()
    }

    get("/movies") {
        response.type("application/json")
        val movies = moviesService.findAll()
        gson.toJson(movies)
    }

    get("/movies/deleted") {
        response.type("application/json")
        val movies = moviesService.findDeleted()
        gson.toJson(movies)
    }

    get("/movie/:id") {
        response.type("application/json")
        val id = request.params(":id")
        val movie = moviesService.findById(movieId(id))
        gson.toJson(movie)
    }
    
    delete("/movie/:id") {
        response.type("application/json")
        val id = request.params(":id")
        moviesService.delete(id)
        "{}"
    }
    
    delete("/movies") {
        response.type("application/json")
        val body = request.body()
        val ids = gson.fromJson<List<String>>(body, List::class.java).toList()

        moviesService.delete(ids)
        "{}"
    }

    put("/movie") {
        response.type("application/json")

        val movie = gson.fromJson(request.body(), Movie::class.java)!!

        val id = movie.id

        val existingMovie = moviesService.findById(id)!!
        val edited = existingMovie.edited

        if (!edited) {
            tokenService.createTokensForEditing(existingMovie, movie.title)
        }

        moviesService.update(movie)

        val result = moviesService.findById(id)
        val jsonResponseBody = gson.toJson(result)
        jsonResponseBody
    }

    post("/movie") {
        response.type("application/json")

        val movie = gson.fromJson(request.body(), Movie::class.java)!!

        val id = moviesService.add(movie)

        val result = moviesService.findById(id)
        val jsonResponseBody = gson.toJson(result)
        jsonResponseBody
    }

    get("/tokens") {
        gson.toJson(tokenService.tokens())
    }
    
    get("/poster/:id") {
        val id = movieId(request.params("id"))
        
        val posters = database.getCollection<Poster>("posters")
        val poster = posters.findOneById(id)!!

        response.type(poster.type)
        poster.content.inputStream().copyTo(response.raw().outputStream, 8096)
        Unit
    }
    
    post("/poster/:id") {
        val rawRequest = request.raw()

        val parts = rawRequest.parts
        val part = parts.find { it.name == "files[]" }!!
        val baos = ByteArrayOutputStream()
        part.inputStream.copyTo(baos, 8096)
        val bytes: ByteArray = baos.toByteArray()
        val id = movieId(request.params("id"))
        val poster = Poster(id, part.contentType, bytes)

        val posters = database.getCollection<Poster>("posters")
        
        posters.deleteOne(Poster::id eq id)
        posters.insertOne(poster)
        moviesService.markAddedPoster(id)
        
        "{}"       
    }

    delete("/token/:token") {
        tokenService.delete(request.params("token"))
        gson.toJson(tokenService.tokens())
    }

    post("scan") {
        updateMovies.update()
        "{}"
    }

    options("/*") {
        "OK"
    }

    before(Filter { request, response ->
        val rawRequest = request.raw()
        if (rawRequest.getContentType() != null && rawRequest.getContentType().startsWith("multipart/form-data")) {
            val MULTI_PART_CONFIG = MultipartConfigElement(System.getProperty("java.io.tmpdir"))
            rawRequest.setAttribute(Request.__MULTIPART_CONFIG_ELEMENT, MULTI_PART_CONFIG);
        }
        response.header("Access-Control-Allow-Origin", "*")
        response.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE")
    })

    return http
}                     




