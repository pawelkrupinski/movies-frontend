package pawel.files

import com.google.gson.JsonArray
import com.google.gson.JsonParser
import com.mashape.unirest.http.HttpResponse
import com.mashape.unirest.http.Unirest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.*
import pawel.movies.db.DB
import pawel.movies.services.DbTokenService
import pawel.movies.main.main
import pawel.movies.model.Movie
import pawel.movies.model.createGson
import spark.kotlin.Http
import java.net.ServerSocket

class AcceptanceTest : FileTest() {
    
    @Test
    fun deletingMovies() {
        val greenMilePath = createMovieFile("The Green Mile (1999) 720p BRRiP x264 AAC [Team Nanban]")
        val inventorPath = createMovieFile("The.Inventor.Out.For.Blood.In.Silicon.Valley.1080p.WEBRip.x264-[YTS.AM]")
        val marriageStoryPath = createMovieFile("Marriage.Story.2019.720p.WEBRip.800MB.x264-GalaxyRG.mkv")
        val spotlightPath = createMovieFile("Spotlight.2015.1080p.BRRip.x264.AAC-ETRG.mp4")

        val response = Unirest.post("$host/scan").asString()
        assertThat(response.status, `is`(200))

        val movies = fetchMovies()

        val greenMileMovie = movieByPath(movies, greenMilePath)
        val inventorMovie = movieByPath(movies, inventorPath)
        val marriageStoryMovie = movieByPath(movies, marriageStoryPath)

        val deleteResponse = Unirest.delete("$host/movie/" + greenMileMovie.id.toString()).asString()
        assertThat(deleteResponse.status, `is`(200))

        val moviesAgain = fetchMovies().map {it.title}.sorted()

        assertThat(moviesAgain, `is`(listOf(
            "The Inventor Out For Blood In Silicon Valley 1080p WEBRip x264 YTS AM",
            "Spotlight 1080p BRRip x264 AAC ETRG mp4",
            "Marriage Story 720p WEBRip 800MB x264 GalaxyRG mkv").sorted()))

        val idsToDelete = listOf(inventorMovie.id.toString(), marriageStoryMovie.id.toString())
        
        val deleteManyResponse = Unirest.delete("$host/movies")
            .body(gson.toJson(idsToDelete))
            .asString()
        assertThat(deleteManyResponse.status, `is`(200))

        
        val moviesYetAgain = fetchMovies().map {it.title}.sorted()
        assertThat(moviesYetAgain, `is`(listOf("Spotlight 1080p BRRip x264 AAC ETRG mp4").sorted()))

        
        val deletedMovies = fetchDeletedMovies().map {it.title}.sorted()
        assertThat(deletedMovies, `is`(listOf(
            "Marriage Story 720p WEBRip 800MB x264 GalaxyRG mkv", 
            "The Green Mile 720p BRRiP x264 AAC Team Nanban", 
            "The Inventor Out For Blood In Silicon Valley 1080p WEBRip x264 YTS AM"
        ).sorted()))

        val putMovieString = Unirest.put("$host/movie").body(
            gson.toJson(greenMileMovie.copy(title = "The Green Mile"))
        ).asString().body

        val putMovie = gson.fromJson(putMovieString, Movie::class.java)
        assertThat(putMovie, `is`(greenMileMovie.copy(title = "The Green Mile", edited = true)))

        val evenMoreMovies = fetchMovies().map {it.title}.sorted()
        assertThat(evenMoreMovies, `is`(listOf(
            "Spotlight 1080p BRRip ETRG mp4", 
            "The Green Mile"
        ).sorted()))

    }
    
    @Test
    fun rest() {
        val greenMilePath = createMovieFile("The Green Mile (1999) 720p BRRiP x264 AAC [Team Nanban]")
        val inventorPath = createMovieFile("The.Inventor.Out.For.Blood.In.Silicon.Valley.1080p.WEBRip.x264-[YTS.AM]")
        val marriageStoryPath = createMovieFile("Marriage.Story.2019.720p.WEBRip.800MB.x264-GalaxyRG.mkv")
        val spotlightPath = createMovieFile("Spotlight.2015.1080p.BRRip.x264.AAC-ETRG.mp4")

        val response = Unirest.post("$host/scan").asString()
        assertThat(response.status, `is`(200))

        val movies = fetchMovies()

        val greenMileMovie = movieByPath(movies, greenMilePath)
        val inventorMovie = movieByPath(movies, inventorPath)
        
        assertThat(greenMileMovie.title, `is`("The Green Mile 720p BRRiP x264 AAC Team Nanban"))
        assertThat(greenMileMovie.year, `is`(1999))
        
        assertThat(inventorMovie.title, `is`("The Inventor Out For Blood In Silicon Valley 1080p WEBRip x264 YTS AM"))
        assertThat(inventorMovie.year, `is`(null as Int?))

        val putMovieString = Unirest.put("$host/movie").body(
            gson.toJson(greenMileMovie.copy(title = "The Green Mile"))
        ).asString().body

        val putMovie = gson.fromJson(putMovieString, Movie::class.java)
        assertThat(putMovie, `is`(greenMileMovie.copy(title = "The Green Mile", edited = true)))

        val greenMileId = greenMileMovie.id
        val movieString = Unirest.get("$host/movie/$greenMileId").asString().body
        val fetchedGreenMile = gson.fromJson(movieString, Movie::class.java)
        
        assertThat(fetchedGreenMile, `is`(greenMileMovie.copy(title = "The Green Mile", edited = true)))

        val tokens = tokenService.tokens()
        
        assertThat(tokens, `is`(setOf("720p", "BRRiP", "x264", "AAC", "Team", "Nanban")))
        
        val moviesAgain = fetchMovies().map {it.title}.sorted()
        
        assertThat(moviesAgain, `is`(listOf(
            "The Green Mile", 
            "The Inventor Out For Blood In Silicon Valley 1080p WEBRip YTS AM", 
            "Spotlight 1080p BRRip ETRG mp4", 
            "Marriage Story WEBRip 800MB GalaxyRG mkv").sorted()))
    }

    private fun fetchDeletedMovies(): List<Movie> {
        val response = Unirest.get("$host/movies/deleted").asString()
        assertThat(response.status, `is`(200))

        return mapMovies(response)     
    }
    
    private fun fetchMovies(): List<Movie> {
        val response = Unirest.get("$host/movies").asString()
        assertThat(response.status, `is`(200))

        return mapMovies(response)
    }

    private fun mapMovies(response: HttpResponse<String>): List<Movie> {
        val moviesBody = response.body
        val json = JsonParser.parseString(moviesBody) as JsonArray
        val movies = json.map { gson.fromJson(it, Movie::class.java) }
        return movies
    }

    private fun movieByPath(movies: List<Movie>, path: String) = movies.first { it.path == path }

    companion object {
        val db = DB()
        val database = db.createTest()

        val gson = createGson()

        val tokenService = DbTokenService(database)

        lateinit var http: Http
        val port = ServerSocket(0).use { it.localPort }
        val host = "http://localhost:$port"
        
        @BeforeClass
        @JvmStatic
        fun startServer() {
            http = main(port, testDir, database)
        }

        @AfterClass
        @JvmStatic
        fun stopServer() {
            http.stop()
        }   
    }


    @After
    fun dropDatabase() {
        database.drop()
    }
}