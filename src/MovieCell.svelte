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
    export let stateChanged = movie => {}

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
      stateChanged(movie)
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

    function markWatchNext() {
      movie.watchNext = true
      service.update(movie, () => {})
    }

    function unmarkWatchNext() {
      movie.watchNext = false
      service.update(movie, () => {})
    }

    function calculateSelectedClass(selecting, isSelected) {
      if (selecting && isSelected) {
        return "selected"
      } else {
        return ""
      }
    }

    function edit() {
      editing = true
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

    .movie-cell:hover {
      box-shadow: 
        0 1px 3px 0 rgba(60,64,67,.3), 
        0 4px 8px 3px rgba(60,64,67,.15);
      border-color: transparent;
      background-color: rgba(10,10,10,.03);
      transition: box-shadow .15s,background-color .15s,border-color .15s
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
          <div class="movie-cell" on:click={unflip}>
             <div>Title: {movie.title}</div>
             <div>Year: {movie.year}</div>
             <!-- <div>Plot:<br />{movie.plot}</div> -->
             {#if movie.watched}
                <button on:click={markUnwatched}>Mark unwatched</button>
             {:else}
                <button on:click={markWatched}>Mark watched</button>
             {/if}
             <button on:click={edit}>Edit</button> 
             {#if movie.watchNext}
                <button on:click={unmarkWatchNext}>Don't watch next</button>
             {:else}
                <button on:click={markWatchNext}>Watch next</button>
             {/if}
          </div>
        {:else}
          <div on:click="{clicked}" class="movie-cell {selectedClass}" style="display: grid;">
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
