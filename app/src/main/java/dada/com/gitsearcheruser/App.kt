package dada.com.gitsearcheruser

import android.app.Application
import android.content.res.Resources

class App : Application() {

    companion object {

        @JvmField
        var mInstance: App? = null

        @JvmField
        var res: Resources? = null

        fun getInstance(): App? {
            return mInstance
        }

        fun getResourses(): Resources? {
            return res
        }
    }

    override fun onCreate() {
        super.onCreate()
        mInstance = this
        res = resources
    }


}