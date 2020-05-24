package dada.com.gitsearcheruser.api


import dada.com.gitsearcheruser.BuildConfig
import dada.com.gitsearcheruser.const.Const.Companion.BASE_URL
import dada.com.gitsearcheruser.data.GitSearchResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface GitApi {

    companion object {
        fun get(): GitApi {
            val clientBuilder = OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .retryOnConnectionFailure(false)
            if (BuildConfig.DEBUG) {
                val loggingInterceptor = HttpLoggingInterceptor()
                loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
                clientBuilder.addInterceptor(loggingInterceptor)
            }

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(clientBuilder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(GitApi::class.java)
        }


    }

    @GET("search/users")
    fun getGitSearch(
        @Query("q") query:String,
        @Query("page") page:Int,
        @Query("per_page") perPage:Int
    ): Call<GitSearchResponse>




}