<script>
    import Movies from './Movies.svelte';
    
    export let currentRoute
    
    function oneMovie(movie) {
      return movie.title == currentRoute.namedParams.title.replace("%20", " ")
    }
</script>


<Movies filterMovies={oneMovie} />
