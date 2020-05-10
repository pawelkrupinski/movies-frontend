<script context="module">
  const host = "http://localhost:8080"
  const pageSize = 100

  export function hasMongo(callback) {
    fetch(host)
      .then(response => response.json())
      .then(json => callback(json.includes("movies")), () => callback(false)
  }

  export class RestfulMongoMovieService {
    findAll(callback) {
      this.findAllWhere(`{"removed": { "$eq": false}}`, callback)
    }

    findAllWhere(filter, callback) {
      const size = 1000
      let page = 1
      let result = []

      function pageResults(movies) {
        result = result.concat(movies)
        if (movies.length < size) {
          callback(result)
        } else {
          page = page + 1
          findPaged(filter, page, size, pageResults)
        }
      }

      findPaged(filter, page, size, pageResults)
    }

    add(movie, callback) {
      fetch(host + '/movies', {
            method: 'post',
            body: JSON.stringify(movie)
          }).then(response => response.json())
          .then(callback, onError)
    }

    update(movie, callback) {
      this.deleteDuplicate(movie.imdbId, () => {
        movie.edited = true
        fetch(host + '/movies/' + movie.id , {
              method: 'PUT',
              body: JSON.stringify(movie),
              headers: {
                "Content-Type": "application/json"
              }
            }).then(callback(movie), onError)
      })
    }

    realDelete(id , callback) {
      fetch(host + '/movies/' + id, {
           method: 'DELETE'
         }).then(callback, onError)
    }

    delete(id , callback) {
      fetch(host + '/movies/' + id, {
           method: 'PATCH',
           body: JSON.stringify({ "$set": { "removed": true } }),
           headers: {
             "Content-Type": "application/json"
           }
         }).then(callback, onError)
    }

    deleteAll(ids, callback) {
      const idsFilter = deleteAllFilter(ids)
      fetch(host + '/movies*?filter=' + idsFilter, {
        method: 'PATCH',
        body: JSON.stringify({ "$set": { "removed": true } }),
        headers: {
          "Content-Type": "application/json"
        }
      }).then(response => response.json())
      .then(callback, onError)
    }
  }

  function deleteAllFilter(ids) {
    return JSON.stringify(
      { "_id": { "$in": ids } }
    ) .split("\n").join("")
  }


  function addId(movie) {
    movie.id = movie._id.$oid
    return movie
  }

  const addIds = json => json.map(addId)

  function findPaged(filter, page, size, callback) {
    fetch(host + `/movies?page=${page}&pagesize=${size}&filter=${filter}`)
      .then(response => response.json())
      .then(addIds)
      .then(callback, onError)
  }

  function findByImdbId(id, callback) {
    fetch(host + `/movies?filter={"imdbId":{"$eq": "${id}"} }`)
    .then(response => response.json())
    .then(addIds)
    .then(callback, onError)
  }

  function deleteDuplicate(imdbId, callback) {
    if (imdbId) {
      findByImdbId(imdbId, movies => {
        const otherMovie = movies[0]
        if (otherMovie != undefined) {
          realDelete(otherMovie.id, callback)
        }
      })
    }
  }

  export class RestfulMongoTokenService {
    findAll(callback) {
        fetch(host + '/tokens')
          .then(response => response.json())
          .then(callback, onError)
    }

    delete(token, callback) {
      fetch(host + `/tokens/*?filter={"token":{"$eq": "${token}"} }`, {
        method: 'delete'
      }).then(response => response.json())
      .then(callback, onError)
    }
  }

  function onError(message) {
    alert(message)
  }
</script>
