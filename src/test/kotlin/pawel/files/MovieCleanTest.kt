package pawel.files

import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertThat
import org.junit.Test
import org.mockito.Mockito.mock
import pawel.movies.services.TokenService
import pawel.movies.model.Movie
import pawel.movies.services.MovieClean

class MovieCleanTest {

   
    @Test
    fun extractYear() {
        val nullInt: Int? = null

        val tokenService = mock(TokenService::class.java)

        assertThat(MovieClean(tokenService).extractYear("fssdf 1067 fdsf"), `is`(1067))
        assertThat(MovieClean(tokenService).extractYear("fssdf 1999 fdsf 1080p "), `is`(1999))
        assertThat(MovieClean(tokenService).extractYear("fssdf 1999 fdsf 1080 "), `is`(nullInt))
        assertThat(MovieClean(tokenService).extractYear("Lucy 2014 1080p BluRay x264 YIFY"), `is`(2014))
    }
    
    @Test
    fun cleansTitle() {
        val tokenService = mock(TokenService::class.java)
        
        val movie = Movie(title = "Lucy 2014  1080p BluRay x264  YIFY ", path = "")
        assertThat(MovieClean(tokenService).clean(movie).title, `is`("Lucy 1080p BluRay x264 YIFY"))   
    }
}