package pawel.files

import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertThat
import org.junit.Test
import pawel.movies.model.Movie
import pawel.movies.services.MovieClean

class MovieCleanTest {
   
    @Test
    fun extractYear() {
        val nullInt: Int? = null

        assertThat(MovieClean().extractYear("fssdf 1067 fdsf"), `is`(1067))
        assertThat(MovieClean().extractYear("fssdf 1999 fdsf 1080p "), `is`(1999))
        assertThat(MovieClean().extractYear("fssdf 1999 fdsf 1080 "), `is`(nullInt))
        assertThat(MovieClean().extractYear("Lucy 2014 1080p BluRay x264 YIFY"), `is`(2014))
    }
    
    @Test
    fun cleansTitle() {
        val movie = Movie(title = "Lucy 2014  1080p BluRay x264  YIFY ", path = "", added = 0)
        assertThat(MovieClean().clean(movie, emptySet()).title, `is`("Lucy 1080p BluRay x264 YIFY"))   
    }
}