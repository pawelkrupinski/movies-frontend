<script context="module">
  import Taffy from 'taffydb-reboot';
  const movies = Taffy()
  movies.store('movies')

  export class LocalStorageMovieService {

    add(movie, callback) {
      movie.id = movie.imdbId
      movies.insert(movie)
      callback(movie)
    }

    update(movie, callback) {
      movies({id: movie.id}).update({
        title: movie.title,
        year: movie.year,
        imdbId: movie.imdbId,
        poster: movie.poster
      })
    }

    delete(id , callback) {
      movies({id: id}).remove()
      callback()
    }

    findAll(callback) {
      callback(movies().get())
    }

    deleteAll(ids, callback) {
      movies({id: ids}).delete()
      callback()
    }
  }

  function onError(message) {
    alert(message)
  }
</script>
