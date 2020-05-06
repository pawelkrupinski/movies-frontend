import AddMovie from './AddMovie.svelte'
import LastAddedMovies from './LastAddedMovies.svelte'
import DeletedMovies from './DeletedMovies.svelte'
import EditedMovies from './EditedMovies.svelte'
import UneditedMovies from './UneditedMovies.svelte'
import SingleMovie from './SingleMovie.svelte'
import Tokens from './Tokens.svelte'
import Search from './Search.svelte'

function userIsAdmin() {
  //check if user is admin and returns true or false
}

const routes = [
  {
    name: '/',
    component: EditedMovies
  },
  {
    name: 'movie/:title',
    component: SingleMovie
  },
  {
    name: 'unedited',
    component: UneditedMovies
  },
  {
    name: 'add',
    component: AddMovie
  },
  {
    name: 'lastAdded',
    component: LastAddedMovies
  },
  {
    name: 'deleted',
    component: DeletedMovies
  },
  { 
    name: 'tokens', 
    component: Tokens 
  },
  {
    name: 'search', 
    component: Search 
  }
]

export { routes }