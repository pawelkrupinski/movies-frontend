package pawel.files

import com.google.gson.JsonArray
import com.google.gson.JsonParser
import com.mashape.unirest.http.Unirest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import pawel.movies.db.DB
import pawel.movies.services.DbTokenService
import pawel.movies.main.main
import pawel.movies.model.Movie
import pawel.movies.model.createGson
import spark.kotlin.Http
import java.net.ServerSocket

class AcceptanceTest : FileTest() {

    lateinit var http: Http
    val db = DB()
    val database = db.createTest()

    val port = ServerSocket(0).use { it.localPort }
    val host = "http://localhost:$port"
    
    val gson = createGson()

    val tokenService = DbTokenService(database)

    @Test
    fun rest() {
        val greenMilePath = createMovieFile("The Green Mile (1999) 720p BRRiP x264 AAC [Team Nanban]")
        val inventorPath = createMovieFile("The.Inventor.Out.For.Blood.In.Silicon.Valley.1080p.WEBRip.x264-[YTS.AM]")

        val response = Unirest.post("$host/scan").asString()
        assertThat(response.status, `is`(200))

        val moviesBody = Unirest.get("$host/movies").asString().body
        val json = JsonParser.parseString(moviesBody) as JsonArray
        val movies = json.map { gson.fromJson(it, Movie::class.java) }

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
    }

    private fun movieByPath(movies: List<Movie>, path: String) = movies.first { it.path == path }

    @Before
    fun startServer() {
        http = main(port, testDir, database)
    }

    @After
    fun stopServer() {
        http.stop()
    }

    @After
    fun dropDatabase() {
        database.drop()
    }
}