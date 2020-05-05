<script>
    import Movies from './Movies.svelte';
    
    export let limit = 20;
    
    const sortByAddedTime = (a, b) => b.added - a.added
    
    function determineLastAdded(movies) {
       movies.sort(sortByAddedTime)
       return movies.slice(0, limit)    
    }
</script>

<Movies decorate="{determineLastAdded}" sortFunction="{sortByAddedTime}"/>
