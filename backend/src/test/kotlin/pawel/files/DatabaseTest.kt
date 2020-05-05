package pawel.files

import org.junit.After
import pawel.movies.db.DB

open class DatabaseTest {
    val db = DB()
    val database = db.createTest()

    @After
    fun tearDown() {
        db.tearDownTest()
    }

}