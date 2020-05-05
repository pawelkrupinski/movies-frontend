<script>
    import Movie from './MovieCell.svelte';

    const host = "http://localhost:8000/"
    export const searchEnabled = true 
    let search = ''
	let name = ''
	export let fetchUrl = 'movies'
	let fullFetchUrl = host + fetchUrl
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
      fetch(fullFetchUrl)
	    .then(response => response.json())
        .then(json => {
          updateMovies(json)
          moviesUpdated(movies) 
        }, alert)
	}
	
	export function deleteAll() {
      fetch(host + 'movies', {
            method: 'delete',
            body: JSON.stringify(movies.map(movie => movie.id))
          }).then(response => response.json())
          .then(json => fetchMovies(), alert)
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
