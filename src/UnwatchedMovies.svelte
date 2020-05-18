<script>
    import Movies from './Movies.svelte';
</script>

<Movies filterMovies="{movie => !movie.watched && movie.edited}"/>
