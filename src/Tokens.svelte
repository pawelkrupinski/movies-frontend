<script>
	import { tokenService } from './services/Services.svelte';

	const service = new tokenService()
	let tokens = [];

	if (tokens.length == 0) {
      fetchTokens()
	}

	function fetchTokens() {
    service.findAll(json => tokens = json)
	}

	function deleteToken(token) {
    service.delete(token, json => tokens = json)
	}
</script>

<table>
    {#each tokens as token}
    <tr>
        <td>{token}</td>
        <td><button on:click="{deleteToken(token)}">Delete</button></td>
    </tr>
    {/each}
</table>
