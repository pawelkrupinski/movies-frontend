<script>
    import Movies from './Movies.svelte';
    
    const replaceTitlesWithPaths = movies => movies.map(replaceTitleWithPath)
    
    function replaceTitleWithPath(movie) {
      const lastIndexOfSlash = movie.path.lastIndexOf("/") + 1
      const fileName = movie.path.substring(lastIndexOfSlash)
      
      const lastIndexOfDot = fileName.lastIndexOf(".")
      
      movie.title = fileName.substring(0, lastIndexOfDot)  
      
      return movie
    }
</script>

<Movies fetchUrl="movies/deleted" decorate="{replaceTitlesWithPaths}"/>
