<script>
  import Movie from './MovieCell.svelte';
  import { movieService } from './services/Services.svelte';

  const service = movieService()
  export const searchEnabled = true
  let selecting = false
  let selectedMovies=[]
  let search = ''
	let name = ''
	let searchResult = []
	let originalMovies = []
  let movies = []
  $: movies = updatedMovies(originalMovies, search)

  export let filterMovies = movie => true
  export let decorate = theMovies => theMovies
  const sortByTitle = (a, b) => a.title.localeCompare(b.title)
  export let sortFunction = sortByTitle

  if (movies.length == 0) {
      fetchMovies()
	}

	function sort(theMovies) {
    theMovies.sort(sortFunction)
    return theMovies
  }

	export function fetchMovies() {
    service.findAll(json => {
      originalMovies = json
    })
	}

	export function deleteAll() {
    const ids = movies.map(movie => movie.id)
    service.deleteAll(ids, () => {
      search = ''
      fetchMovies()
    })
	}

  function searchFilter(movie) {
    if (!search) {
      return true
    }
    return movie.title.includes(search)
  }

  function combinedFilter(movie) {
    return searchFilter(movie) && filterMovies(movie) && !(movie.removed)
  }

	function updatedMovies(originalMovies) {
    const filteredMovies = originalMovies.filter(combinedFilter)
    const decoratedMovies = decorate(filteredMovies)
    const sortedMovies = sort(decoratedMovies)
    return sortedMovies  
	}

	export function refresh() {
	   originalMovies = originalMovies
  }
  
  function startSelecting() {
    selecting = true
    selectedMovies = []
  }

  function cancelSelecting() {
    selecting = false
    selectedMovies = []
  }

  function selected(id) {
    selectedMovies.push(id)
  }

  function unselected(id) {
    selectedMovies = selectedMovies.filter(e => e != id)
  }

  async function markWatched() {
    await Promise.all(
      movies
        .filter(movie => selectedMovies.includes(movie.id))
        .map(movie => {
          movie.watched = true
          service.update(movie, () => {})
        })
    )
    cancelSelecting()
  }

  async function markWatchNext() {
    await Promise.all(
      movies
        .filter(movie => selectedMovies.includes(movie.id))
        .map(movie => {
          movie.watchNext = true
          service.update(movie, () => {})
        })
    )
    cancelSelecting()
  }
</script>

<style>
    body {
      background-color: #b2bfb5;
    }
</style>

<input bind:value={search} placeholder="Search" /> 
{#if !selecting}
   <button on:click={startSelecting}>Select many</button>
{:else}
   <button on:click={cancelSelecting}>Cancel selection</button>
   <button on:click={markWatched}>Mark watched</button>
   <button on:click={markWatchNext}>Mark watch next</button>
{/if}

<div style="display: flex; flex-wrap: wrap;">
    {#each movies as movie}
    <Movie movie={movie} 
        bind:selecting={selecting} 
        selected={selected} 
        unselected={unselected} 
        stateChanged={fetchMovies} />
    {/each}
</div>
