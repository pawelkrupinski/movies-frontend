package pawel.movies.services

import pawel.movies.model.Movie

class MovieClean(val tokenService: TokenService) {
    fun clean(movie: Movie) =
        if (movie.edited) movie
        else {
            val tokens = tokenService.tokens()
            val cleanedTitle = cleanTitle(movie.title)
            val year = extractYear(cleanedTitle)
            val furtherCleanedTitle = cleanedTitle
                .replace(year.toString(), "")
                .trim()
                .replace(Regex(" +"), " ")
            
            val evenFurtherRemovedTokens = removeTokens(furtherCleanedTitle, tokens)
            
            movie.copy(title = evenFurtherRemovedTokens, year = year)
        }

    fun extractYear(title: String): Int? {
        val matches: List<MatchResult> = Regex(" \\d{4} ").findAll(" $title ").toList()
        if (matches.size == 1) {
            return matches.first().value.trim().toInt()
        }
        return null
    }

    private fun cleanTitle(title: String) = title
        .replace(".", " ")
        .replace("-", " ")
        .replace("(", " ")
        .replace(")", " ")
        .replace("[", " ")
        .replace("]", " ")
        .replace("_", " ")
    
    private fun removeTokens(title: String, tokens: Set<String>): String = 
        title.split(" ").filterNot { tokens.contains(it) }.joinToString(" ")
}