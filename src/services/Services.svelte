<script context="module">
import { RestfulMongoMovieService } from './RestfulMongoServices.svelte'
import { LocalStorageMovieService } from './LocalStorageServices.svelte'
import { KotlinMovieService } from './KotlinServices.svelte'
import { KotlinTokenService } from './KotlinServices.svelte'

export function movieService() {
  return new RestfulMongoMovieService()
}

export function tokenService() {
  return new KotlinTokenService()
}
</script>
