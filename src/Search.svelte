<script>
  export let selectedMovie = (movie) => {}
	export let name = '';
	export let year = undefined;
	export let searchBox = false;
	export let returnIfOne = false;
	let searchResults = [];

    $: if (name != '') {
      let yearQuery;
        if (year === undefined) {
          yearQuery = ''
        } else {
          yearQuery = '&y=' + year
        }
      fetch('http://www.omdbapi.com/?apikey=6a88d99a&s=' + name + yearQuery)
        .then(response => response.json())
        .then(renderResults, alert)
    }

    function renderResults(json) {
      if (json.Search !== undefined) {
        searchResults = json.Search
        checkReturnOne()
      } else {
        searchResults = []
      }
    }

    function checkReturnOne() {
      if (returnIfOne && searchResults.length > 0) {
        const result = searchResults.filter(result => result.Title === name)
        if (result.length == 1) {
          selectedMovie(result[0])
        }
      }
    }

    function onMovieSelected(result) {
      name = ''
      searchResults = []
      selectedMovie(result)
    }
</script>

<style>
    img {
      width: 100px;
      height: auto;
    }

    .resultRow {
      padding: 0.5vw;
    }
</style>

{#if searchBox}
<input bind:value={name} placeholder="Search" />
{/if}

<table>
    {#each searchResults as result}
    <tr on:click="{onMovieSelected(result)}">
        <td class="resultRow"><img src="{result.Poster}" alt="" /></td>
        <td class="resultRow">{result.Title}</td>
        <td class="resultRow">{result.Year}</td>
    </tr>
    {/each}
</table>