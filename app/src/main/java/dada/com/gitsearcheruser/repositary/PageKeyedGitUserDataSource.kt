package dada.com.gitsearcheruser.repositary


import android.content.Context
import android.net.ConnectivityManager
import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dada.com.gitsearcheruser.App
import dada.com.gitsearcheruser.R
import dada.com.gitsearcheruser.api.GitApi
import dada.com.gitsearcheruser.const.Const.Companion.INITIAL_PAGE
import dada.com.gitsearcheruser.data.GitApiErrorResponse
import dada.com.gitsearcheruser.data.GitSearchResponse
import dada.com.gitsearcheruser.data.GitUser
import dada.com.gitsearcheruser.util.ErrorUtil
import retrofit2.Call
import retrofit2.Response
import java.io.IOException
import java.util.concurrent.Executor


class PageKeyedGitUserDataSource(
    private val gitApi: GitApi,
    private val gitUserQuery: String,
    private val retryExecutor: Executor
) : PageKeyedDataSource<Int, GitUser>() {
    private var retry: (() -> Any)? = null

    val networkState = MutableLiveData<NetworkState>()


    fun retryAllFailed() {
        val prevRetry = retry
        retry = null
        prevRetry?.let {
            retryExecutor.execute {
                it.invoke()
            }
        }
    }



    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, GitUser>) {
        // Do nothing
    }

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, GitUser>
    ) {
        if (!isInternetOn()) {
            retry = {
                loadInitial(params, callback)
            }
            networkState.postValue(
                NetworkState.error(
                    App.getResourses()?.getString(R.string.network_error)
                )
            )
            return
        }

        val request = gitApi.getGitSearch(
            query = gitUserQuery,
            perPage = params.requestedLoadSize,
            page = INITIAL_PAGE
        )
        networkState.postValue(NetworkState.LOADING)

        try {
            val response = request.execute()
            if (response.isSuccessful) {
                val body = response.body()
                val items = body?.items ?: emptyList()
                retry = null
                networkState.postValue(
                    if (items.isNotEmpty()) NetworkState.LOADED_SUCCESS
                    else NetworkState.LOADED_EMPTY
                )
                callback.onResult(
                    items, null,
                    if (items.isNotEmpty()) INITIAL_PAGE + 1
                    else null
                )
            } else {
                retry = {
                    loadInitial(params, callback)
                }
                apiErrorHandle(response)

            }

        } catch (ioException: IOException) {
            retry = {
                loadInitial(params, callback)
            }
            val error = NetworkState.error(
                ioException.message ?: App.getResourses()?.getString(R.string.unknown_error)
            )
            networkState.postValue(error)
        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, GitUser>) {
        if (!isInternetOn()) {
            retry = {
                loadAfter(params, callback)
            }
            networkState.postValue(
                NetworkState.error(
                    App.getResourses()?.getString(R.string.network_error)
                )
            )
            return
        }

        networkState.postValue(NetworkState.LOADING)
        gitApi.getGitSearch(
            query = gitUserQuery,
            page = params.key,
            perPage = params.requestedLoadSize
        ).enqueue(
            object : retrofit2.Callback<GitSearchResponse> {
                override fun onFailure(call: Call<GitSearchResponse>, t: Throwable) {
                    retry = {
                        loadAfter(params, callback)
                    }
                    networkState.postValue(
                        NetworkState.error(
                            t.message ?: App.getResourses()?.getString(R.string.unknown_error)
                        )
                    )
                }

                override fun onResponse(
                    call: Call<GitSearchResponse>,
                    response: Response<GitSearchResponse>
                ) {
                    if (response.isSuccessful) {
                        val body = response.body()
                        val items = body?.items ?: emptyList()
                        retry = null
                        callback.onResult(
                            items,
                            if (isNotResultEnd(items, params.requestedLoadSize)) params.key + 1
                            else null
                        )
                        networkState.postValue(
                            if (isNotResultEnd(items, params.requestedLoadSize)) NetworkState.LOADED_SUCCESS
                            else NetworkState.LOADED_END
                        )
                    } else {
                        retry = {
                            loadAfter(params, callback)
                        }
                        apiErrorHandle(response)
                    }

                }
            }
        )
    }

    fun isNotResultEnd(gitUsers: List<GitUser>, pageSize: Int) =
        (!gitUsers.isNullOrEmpty() && gitUsers.size == pageSize)

    fun apiErrorHandle(response: Response<GitSearchResponse>) {
        val headerMap = response.headers().toMultimap()
        val errorBody = response.errorBody()?.string()

        var gitApiErrorResponse: GitApiErrorResponse? = null
        errorBody?.let {
            val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()
            val adapter = moshi.adapter<GitApiErrorResponse>(GitApiErrorResponse::class.java)
            gitApiErrorResponse = adapter.fromJson(errorBody)
        }
        var errorBodyMsg = gitApiErrorResponse?.message ?: errorBody ?: App.getResourses()
            ?.getString(R.string.unknown_error)
        val errorMsg =
            if (errorBodyMsg != null) ErrorUtil.getApiErrorResponseMsg(headerMap, errorBodyMsg)
            else App.getResourses()?.getString(R.string.unknown_error)
        networkState.postValue(
            NetworkState.error(errorMsg)
        )
    }

    private fun isInternetOn(): Boolean {
        val cm =
            App.getInstance()?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnected
    }


}