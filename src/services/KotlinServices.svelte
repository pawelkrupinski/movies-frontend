<script context="module">
  const host = "http://localhost:8000"

  export class KotlinMovieService {

    add(movie, callback) {
      fetch(host + '/movie', {
            method: 'post',
            body: JSON.stringify(movie)
          }).then(response => response.json())
          .then(callback, onError)
    }

    update(movie, callback) {
      fetch(host + '/movie', {
            method: 'put',
            body: JSON.stringify(movie)
          }).then(response => response.json())
          .then(callback, onError)
    }

    delete(id , callback) {
      fetch(host + '/movie/' + id, {
           method: 'delete'
         }).then(response => response.json())
         .then(callback, onError)
    }

    findAll(callback) {
      fetch(host + '/movies')
        .then(response => response.json())
        .then(callback, onError)
    }

    deleteAll(ids, callback) {
      fetch(host + '/movies', {
        method: 'delete',
        body: JSON.stringify(ids)
      }).then(response => response.json())
      .then(callback, onError)
    }
  }

  export class KotlinTokenService {
    findAll(callback) {
        fetch(host + '/tokens')
          .then(response => response.json())
          .then(callback, onError)
    }

    delete(token, callback) {
      fetch(host + '/token/' + token, {
        method: 'delete'
      }).then(response => response.json())
      .then(callback, onError)
    }
  }

  function onError(message) {
    alert(message)
  }
</script>
