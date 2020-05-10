<script>
    import Movies from './Movies.svelte'
    import { navigateTo } from 'svelte-router-spa'
    let movies;

    function filter(movie) {
      return !movie.edited
    }

    function deleteAll() {
      movies.deleteAll()
      search = ''
    }

    function scan() {
      fetch('http://localhost:8000/scan', {
        method: 'post'
      }).then(response => response.json())
        .then(json => movies.fetchMovies(), alert)
    }

    function goToLastAdded() {
      navigateTo('lastAdded')
    }

</script>

<button on:click="{deleteAll}">Delete all</button>

<button on:click="{scan}">Scan</button>

<button on:click="{goToLastAdded}">Go to Last added</button>

<Movies filterMovies="{filter}" bind:this={movies} />
