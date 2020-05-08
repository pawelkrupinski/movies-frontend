<script>
  import Movie from './MovieCell.svelte';
  import { MovieService } from './services.svelte';

  const service = new MovieService()
  export const searchEnabled = true
  let search = ''
	let name = ''
	let searchResult = []
	let originalMovies = []
	let movies = []

  export let filterMovies = movie => true
  export let decorate = theMovies => theMovies
  const sortByTitle = (a, b) => a.title.localeCompare(b.title)
  export let sortFunction = sortByTitle
  export let moviesUpdated = movies => {}

	if (movies.length == 0) {
      fetchMovies()
	}

    $: {
      search
      refresh()
    }

	function sort(theMovies) {
      theMovies.sort(sortFunction)
      return theMovies
    }

	export function fetchMovies() {
      service.findAll(json => {
        updateMovies(json)
        moviesUpdated(movies)
      })
	}

	export function deleteAll() {
    const ids = movies.map(movie => movie.id)
    service.deleteAll(ids, json => fetchMovies())
	}

    function searchFilter(movie) {
      if (!search) {
        return true
      }
      return movie.title.includes(search)
    }

    function combinedFilter(movie) {
      return searchFilter(movie) && filterMovies(movie)
    }

	function updateMovies(json) {
	   originalMovies = json
       let filteredMovies = originalMovies.filter(combinedFilter)
       let decoratedMovies = sort(decorate(filteredMovies))
       movies = decoratedMovies
	}

	export function refresh() {
	   updateMovies(originalMovies)
	}
</script>

<style>
    body {
      background-color: #b2bfb5;
    }

</style>

<input bind:value={search}  placeholder="Search"/>

<div style="display: flex; flex-wrap: wrap;">
    {#each movies as movie}
    <Movie movie={movie} refreshAll={fetchMovies} />
    {/each}
</div>
