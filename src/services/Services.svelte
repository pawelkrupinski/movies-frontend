<script context="module">
import { RestfulMongoMovieService, hasMongo } from './RestfulMongoServices.svelte'
import { LocalStorageMovieService } from './LocalStorageServices.svelte'
import { KotlinMovieService, KotlinTokenService } from './KotlinServices.svelte'

import { writable } from 'svelte/store';

const mongo = localStorage.getItem("hasMongo") == 'true';

if (localStorage.getItem("hasMongo") == undefined) {
    hasMongo(mongoPresent => {
      localStorage.setItem("hasMongo", mongoPresent)
    })
}

export function movieService() {
  if (mongo) {
    return new RestfulMongoMovieService()
  } else {
    return new LocalStorageMovieService()
  }
}

export function tokenService() {
  return new KotlinTokenService()
}
</script>
