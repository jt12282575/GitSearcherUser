package dada.com.gitsearcheruser.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import dada.com.gitsearcheruser.const.Const.Companion.GIT_USER_QUERY
import dada.com.gitsearcheruser.const.Const.Companion.USER_PER_PAGE
import dada.com.gitsearcheruser.repositary.GitUserRepositary


class GitUserViewModel(
    private val repository: GitUserRepositary,
    private val savedStateHandle: SavedStateHandle
): ViewModel() {
    private val repoResult = savedStateHandle.getLiveData<String>(GIT_USER_QUERY).map {query->
        repository.gitUsersOfSearch(query, USER_PER_PAGE)
    }
    val gitUsers = repoResult.switchMap { it.pagedList }
    val networkState = repoResult.switchMap { it.networkState }
    fun retry(){
        val listing = repoResult.value
        listing?.retry?.invoke()
    }

    fun showGitUserQueryResult(gitUserQuery: String): Boolean {
        if (savedStateHandle.get<String>(GIT_USER_QUERY) == gitUserQuery) {
            return false
        }
        savedStateHandle.set(GIT_USER_QUERY, gitUserQuery)
        return true
    }
}