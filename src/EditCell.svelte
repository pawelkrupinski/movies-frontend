<script>
    import Search from './Search.svelte';
    import UploadPoster from './UploadPoster.svelte';
    import { movieService } from './services/Services.svelte';

    const service = movieService()
    export let movie;
    export let finishEditing;
    let posterUrl;

    $: if (movie.customPoster) {
      posterUrl = 'http://localhost:8000/poster/' + movie.id
    } else {
      posterUrl = movie.poster
    }

    function keyPress(e) {
      if (e.keyCode === 13) {
        update()
      }
    }

    function update() {
      movie.removed = false
      justUpdate()
    }

    function justUpdate() {
      service.update(movie, json => {
        movie = json
        finishEditing()
      })
    }

    function posterUpdated() {
        movie.customPoster = true
        finishEditing()
    }

    function deleteMovie() {
      service.delete(movie.id, json => {})
    }

    function selectedMovie(selected) {
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

<span style="display: grid;">
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
            <button on:click="{finishEditing}">Cancel</button>
            <button on:click="{clear}">Clear</button>
            <div><button on:click="{deleteMovie}">Delete</button></div>
        </span>
    </span>
    <div>
        <Search bind:name="{movie.title}"
                bind:year="{movie.year}"
                selectedMovie={selectedMovie} />
    </div>
</span>
