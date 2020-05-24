

package dada.com.gitsearcheruser.repositary

enum class Status {
    RUNNING,
    SUCCESS,
    FAILED,
    EMPTY,
    NO_MORE_DATA
}

@Suppress("DataClassPrivateConstructor")
data class NetworkState private constructor(
        val status: Status,
        val msg: String? = null) {
    companion object {
        val LOADED_SUCCESS = NetworkState(Status.SUCCESS)
        val LOADED_EMPTY = NetworkState(Status.EMPTY)
        val LOADED_END = NetworkState(Status.NO_MORE_DATA)
        val LOADING = NetworkState(Status.RUNNING)
        fun error(msg: String?) = NetworkState(Status.FAILED, msg)
    }
}