<script>
    import Search from './Search.svelte';
    
    export let movie;
    export let refreshAll;
    let editing = false
    $: editValues = editing || !movie.edited
    
    function keyPress(e) {
      if (e.keyCode === 13) {
        editing = false
        update()
      }
    }
    
    function update() {
      movie.removed = false
      justUpdate()
    }
    
    function justUpdate() {
      fetch('http://localhost:8000/movie', {
            method: 'put',
            body: JSON.stringify(movie)
          }).then(response => response.json())
          .then(json => {
            movie = json
            refreshAll()
          }, alert)
    }
    
    function startEditing() {
        editing = true
    }
    
    function stopEditing() {
        editing = false
    }
    
    function deleteMovie() {
       fetch('http://localhost:8000/movie/' + movie.id, {
            method: 'delete'
          }).then(response => response.json())
          .then(json => refreshAll(), alert)
    }
    
    function selectedMovie(selected) {
      editing = false
      movie.title = selected.Title
      movie.year = year(selected)
      movie.imdbId = selected.imdbID
      movie.poster = selected.Poster
      update()
    }
    
    function year(selected) {
      if (movie.year) {
        return movie.year
      }
      return selected.Year 
    }
    
    function clear() {
      movie.edited = false
      movie.imdbId = null
      movie.poster = null
      justUpdate()
    }
</script>

<span style="display: grid; padding: 10px;">
    {#if editValues}
      <span>
        <span>
            <input size="35" bind:value="{movie.title}" on:keypress="{keyPress}"/>
        </span>
        <span>
            <div>{movie.title}</div>
            <div><input size="10" bind:value="{movie.year}" on:keypress="{keyPress}"/></div>
            <div>{movie.path}</div>
            <button on:click="{stopEditing}">Cancel</button>
            <button on:click="{clear}">Clear</button>
            <div><button on:click="{deleteMovie}">Delete</button></div>
        </span>
    </span>
    <div>
        <Search bind:name="{movie.title}"
                bind:year="{movie.year}"
                selectedMovie={selectedMovie} />    
    </div>
    {:else}
      <span on:click="{startEditing}">
        <span><div><img src="{movie.poster}" alt=""/></div></span>
        <span>
            <div>{movie.title}</div>
            <div>{movie.year}</div>
<!--            <div>{movie.path}</div>-->
        </span>
    </span>
    {/if}
    
</span>