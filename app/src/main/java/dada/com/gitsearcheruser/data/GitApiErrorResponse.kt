package dada.com.gitsearcheruser.data


import com.squareup.moshi.Json

data class GitApiErrorResponse(
    @field:Json(name = "message")
    var message: String? = null
    ,
    @field:Json(name = "documentation_url")
    var documentation_url: String? = null
)
