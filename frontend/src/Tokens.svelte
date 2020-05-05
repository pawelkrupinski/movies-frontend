<script>
	let tokens = [];
	
	if (tokens.length == 0) {
      fetchTokens()
	}
	
	function fetchTokens() {
      fetch('http://localhost:8000/tokens')
	    .then(response => response.json())
        .then(json => tokens = json, alert)
	}
	
	function deleteToken(token) {
	  fetch('http://localhost:8000/token/' + token, {       
	        method: 'delete'
	      }) 
	    .then(response => response.json())
        .then(json => tokens = json, alert)
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
