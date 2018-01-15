package kr.nomade.apivod

import android.app.Activity
import android.app.Application
import android.content.Context
import android.util.Log
import com.kakao.auth.*
import com.kakao.util.helper.Utility.getKeyHash


class GlobalApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        instance = this

        val keyHash = getKeyHash(this)
        Log.d(TAG, "keyHash : ${keyHash}")

        KakaoSDK.init(MyKakaoAdapter())
    }

    companion object {
        val TAG = GlobalApplication::class.java.name

        @Volatile var instance: GlobalApplication? = null

        fun getGlobalApplicationContext(): GlobalApplication {
            if ( instance == null )
                throw IllegalStateException("this applicaiton does not inherit com.kakao.GlobalApplication.")
            return instance!!
        }
    }

    inner class MyKakaoAdapter: KakaoAdapter() {
        override fun getSessionConfig(): ISessionConfig {
            return object: ISessionConfig {
                override fun isSaveFormData() = true
                override fun isSecureMode() = false
                override fun getApprovalType() = ApprovalType.INDIVIDUAL
                override fun isUsingWebviewTimer() = false
                override fun getAuthTypes(): Array<AuthType> {
                    return arrayOf(AuthType.KAKAO_LOGIN_ALL)
                }
            }
        }

        override fun getApplicationConfig(): IApplicationConfig {
            return object: IApplicationConfig {
                override fun getApplicationContext(): Context {
                    return GlobalApplication.getGlobalApplicationContext()
                }
            }
        }
    }
}