package dada.com.gitsearcheruser.const

class Const {
    companion object {
        const val BASE_URL = "https://api.github.com/"
        const val USER_PER_PAGE = 10
        const val GIT_USER_QUERY = "git_user_query"
        const val INITIAL_PAGE = 1

        //Git api header key
        const val GIT_USAGE_REMAINING = "x-ratelimit-remaining"
        const val GIT_USAGE_RESET = "x-ratelimit-reset"
    }
}