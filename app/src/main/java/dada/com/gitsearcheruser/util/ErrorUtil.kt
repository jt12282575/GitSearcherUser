package dada.com.gitsearcheruser.util

import android.text.TextUtils
import dada.com.gitsearcheruser.App
import dada.com.gitsearcheruser.R
import dada.com.gitsearcheruser.const.Const.Companion.GIT_USAGE_REMAINING
import dada.com.gitsearcheruser.const.Const.Companion.GIT_USAGE_RESET

class ErrorUtil {

    companion object {
        fun getApiErrorResponseMsg(
            header: MutableMap<String, List<String>>?,
            errorMsg: String
        ): String? {

            header?.let {
                val gitApiUsageRemain = it[GIT_USAGE_REMAINING]?.getOrNull(0)
                val gitApiUsageReset = it[GIT_USAGE_RESET]?.getOrNull(0)
                if (gitApiUsageRemain != null &&
                    gitApiUsageReset != null &&
                    TextUtils.equals(gitApiUsageRemain, "0")
                ) {
                    return App.getResourses()?.getString(
                        R.string.git_api_limit,
                        errorMsg,
                        DateUtil.getDate(gitApiUsageReset)
                    )
                }

            }

            return errorMsg
        }


    }
}