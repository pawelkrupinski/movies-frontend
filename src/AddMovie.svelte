<script>
    import Search from './Search.svelte';
    import { sanitizeYear } from './Data.svelte';
    import { MovieService } from './services.svelte';

    const service = new MovieService()

    function add(movie) {
      service.add(movie, json => {})
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
