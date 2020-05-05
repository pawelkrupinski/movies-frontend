package pawel.files

import org.hamcrest.CoreMatchers.`is`
import org.junit.After
import org.junit.Assert.assertThat
import org.junit.Test
import org.litote.kmongo.getCollection
import org.litote.kmongo.toId
import pawel.movies.db.DB
import pawel.movies.files.ScanForMovies
import pawel.movies.files.UpdateMovies
import pawel.movies.model.Movie
import java.io.File
import java.nio.file.Paths

class ScanForMoviesTest : FileTest() {
    val db = DB()
    val database = db.createTest()
    val scanForMovies = ScanForMovies(testDir)
    val updateMovies = UpdateMovies(scanForMovies, database)
    val collection = database.getCollection<Movie>("movies")

    @After
    override fun tearDown() {
        super.tearDown()
        db.tearDownTest()
    }

    @Test
    fun noFilesCreateNoRecords() {
        assertThat(scanForMovies.scan(), `is`(emptyList()))
        updateMovies.update()

        assertThat(collection.find().toList(), `is`(emptyList()))
    }

    @Test
    fun newFileGetsAddedToTheDatabase() {
        val path = createMovieFile()
        val file = File(path)

        val expectedMovie = Movie(id = "".toId(),
            path = path,
            title = file.nameWithoutExtension,
            added = file.lastModified()
        )
        
        assertThat(scanForMovies.scan(), `is`(listOf(Paths.get(path))))

        updateMovies.update()
        
        assertThat(collection.find().toSet().map(::clearId), `is`(listOf(expectedMovie)))
    }

    private fun clearId(movie: Movie) = movie.copy(id = "".toId())

    @Test
    fun doesNotAddTheSameFileTwice() {
        val path = createMovieFile()
        val file = File(path)
        
        val expectedMovie = Movie(id = "".toId(), 
            path = path, 
            title = file.nameWithoutExtension,
            added = file.lastModified()
        )

        updateMovies.update()
        updateMovies.update()

        assertThat(collection.find().toSet().map(::clearId), `is`(listOf(expectedMovie)))
    }

    @Test
    fun marksFileAsMissing() {
        val path = createMovieFile()
        val file = File(path)

        val expectedMovie = Movie(id = "".toId(),
            path = path,
            title = file.nameWithoutExtension,
            added = file.lastModified(),
            missing = true
        )

        updateMovies.update()
        
        delete(path)
        
        updateMovies.update()

        assertThat(collection.find().toSet().map(::clearId), `is`(listOf(expectedMovie)))
    }
}
