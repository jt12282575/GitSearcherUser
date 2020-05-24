package dada.com.gitsearcheruser.repositary

import androidx.lifecycle.MutableLiveData
import dada.com.gitsearcheruser.api.GitApi
import dada.com.gitsearcheruser.data.GitUser
import java.util.concurrent.Executor


class GitUserDataSourceFactory(
    private val gitApi: GitApi,
    private val gitUserQuery: String,
    private val retryExecutor: Executor
) : androidx.paging.DataSource.Factory<Int, GitUser>() {
    val sourceLiveData = MutableLiveData<PageKeyedGitUserDataSource>()
    override fun create(): androidx.paging.DataSource<Int, GitUser> {
        val source = PageKeyedGitUserDataSource(gitApi, gitUserQuery, retryExecutor)
        sourceLiveData.postValue(source)
        return source
    }

}