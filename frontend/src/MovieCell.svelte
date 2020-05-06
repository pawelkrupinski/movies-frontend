<script>
    import Search from './Search.svelte';
    import UploadPoster from './UploadPoster.svelte';

    export let movie;
    export let refreshAll;
    let editing = false
    let posterUrl;
    $: editValues = editing || !movie.edited
    
    $: if (movie.customPoster) {
      posterUrl = 'http://localhost:8000/poster/' + movie.id
    } else {
      posterUrl = movie.poster
    }
    
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
    
    function posterUpdated() {
        movie.customPoster = true
        stopEditing()
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
      movie.customPoster = false
      movie.imdbId = null
      movie.poster = null
      justUpdate()
    }
</script>

<style>
    img {
       width: 300px;
       height: auto;
    }
</style>

<span style="display: grid;">
    {#if editValues}
      <span>
        <span>
            <input size="35" bind:value="{movie.title}" on:keypress="{keyPress}"/>
        </span>
        <span>
            <div>{movie.title}</div>
            <div><input size="10" bind:value="{movie.year}" on:keypress="{keyPress}"/></div>
            {#if movie.path}
                <div>{movie.path}</div>
            {/if}   
            
            <UploadPoster movieId={movie.id} posterUpdated="{posterUpdated}" />
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
    <span on:click="{startEditing}" class="movie-cell" style="display: grid; padding: 10px;">
        <div><img src="{posterUrl}" alt=""/></div>
        <div style="align-self: stretch" />
        <div style="align-self: end">
            <div>{movie.title}</div>
            <div>{movie.year}</div>
        </div>
    </span>
    {/if}
    
</span>