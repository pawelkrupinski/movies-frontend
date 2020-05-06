<script>
    import Search from './Search.svelte';
    import { sanitizeYear } from './Data.svelte';
    
    function add(movie) {
      fetch('http://localhost:8000/movie', {
            method: 'post',
            body: JSON.stringify(movie)
          }).then(response => response.json())
          .then(json => {}, alert)
    }
    
    function selectedMovie(selected) {
      const movie = {
        "title": selected.Title,
        "year": sanitizeYear(selected.Year),
        "imdbId": selected.imdbID,
        "poster": selected.Poster,
        "edited": true
      }
      add(movie)
    }
</script>

<Search searchBox="true" selectedMovie="{selectedMovie}"/>