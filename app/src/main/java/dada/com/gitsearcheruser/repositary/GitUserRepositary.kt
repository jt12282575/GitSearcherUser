package dada.com.gitsearcheruser.repositary

import androidx.annotation.MainThread
import androidx.lifecycle.switchMap
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import dada.com.gitsearcheruser.api.GitApi
import dada.com.gitsearcheruser.data.GitUser
import java.util.concurrent.Executor

class GitUserRepositary(private val gitApi: GitApi,
                        private val networkExecutor: Executor
)  {
    @MainThread
    fun gitUsersOfSearch(query: String, pageSize: Int): Listing<GitUser>{
        val sourceFactory = GitUserDataSourceFactory(gitApi, query, networkExecutor)
        val config = PagedList.Config.Builder()
            .setInitialLoadSizeHint(pageSize)
            .setPageSize(pageSize).build()
        val livePagedList = LivePagedListBuilder(sourceFactory,config).build()
        return Listing(
            pagedList = livePagedList,
            networkState = sourceFactory.sourceLiveData.switchMap {
                it.networkState
            },
            retry = {
                sourceFactory.sourceLiveData.value?.retryAllFailed()
            }
        )
    }
}