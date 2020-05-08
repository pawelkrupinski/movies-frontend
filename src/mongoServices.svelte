<script context="module">
    import connect from 'mongodb';
    import assert from 'assert';
    
    const MongoClient = connect().MongoClient;
    
    // Connection URL
    const url = 'mongodb://localhost:27017';
    
    // Database Name
    const dbName = 'myproject';
    const client = new MongoClient(url, {useNewUrlParser: true});
    
    // Use connect method to connect to the server
    client.connect(function(err) {
      assert.equal(null, err);
      console.log("Connected successfully to server");
    })

  export class MovieService {

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

  export class TokenService {
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
