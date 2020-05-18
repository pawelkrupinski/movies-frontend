<script>
    import Search from './Search.svelte';
    import UploadPoster from './UploadPoster.svelte';
    import { movieService } from './services/Services.svelte';
    import EditCell from './EditCell.svelte';
    import { flip } from 'svelte/animate';
    import { quintOut } from 'svelte/easing';
    import { blur, crossfade, draw, fade, fly, scale, slide } from 'svelte/transition';

    const service = movieService()
    export let movie

    export let selecting = false
    export let selected = id => {}
    export let unselected = id => {}
    let isSelected = false

    $: if (!selecting) {
      isSelected = false
    }
    $: selectedClass = calculateSelectedClass(selecting, isSelected)
    let editing = false
    let posterUrl
    let flipped = false
    $: editValues = !selecting && (editing || !movie.edited)

    $: if (movie.customPoster) {
      posterUrl = 'http://localhost:8000/poster/' + movie.id
    } else {
      posterUrl = movie.poster
    }

    function clicked() {
        if (selecting) {
          if (isSelected) {
            isSelected = false
            unselected(movie.id)
          } else {
            isSelected = true
            selected(movie.id)
          }
        } else {
          flipped = true
        }
        
    }

    function unflip() {
      flipped = false
    }

    function finishedEditing() {
      editing = false
    }

    function markWatched() {
      movie.watched = true
      service.update(movie, () => {
        unflip()
      })
    }

    function markUnwatched() {
      movie.watched = false
      service.update(movie, () => {})
    }

    function calculateSelectedClass(selecting, isSelected) {
      if (selecting && isSelected) {
        return "selected"
      } else {
        return ""
      }
    }
</script>

<style>
    img {
       width: 300px;
       height: auto;
    }
    .movie-cell {
      width: 300px;
      height: 484px;
      padding: 10px;
    }

    .selected {
      background-color: green;
    }
</style>

<span style="display: grid;">
    {#if editValues}
      <EditCell finishEditing={finishedEditing} movie={movie} />
    {:else}
        {#if flipped}
          <div transition:fly class="movie-cell">
             <div>Title: {movie.title}</div>
             <div>Year: {movie.year}</div>
             <button on:click={unflip}>Unflip</button> 
             {#if movie.watched}
                <button on:click={markUnwatched}>Mark unwatched</button>
             {:else}
                <button on:click={markWatched}>Mark watched</button>
             {/if}
          </div>
        {:else}
          <div transition:fly on:click="{clicked}" class="movie-cell {selectedClass}" style="display: grid;">
            <div><img src="{posterUrl}" alt=""/></div>
            <div style="align-self: stretch" />
            <div style="align-self: end">
                <div>{movie.title}</div>
                <div>{movie.year}</div>
            </div>
          </div>
        {/if}
    {/if}

</span>
