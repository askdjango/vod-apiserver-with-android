package kr.nomade.apivod

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.kakao.auth.ISessionCallback
import com.kakao.auth.Session
import com.kakao.util.exception.KakaoException
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch


class LoginActivity : AppCompatActivity(), View.OnClickListener {
    private val blogManager by androidLazy { BlogManager() }
    private val sessionCallback by androidLazy { SessionCallback() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setSupportActionBar(toolbar)
        setTitle("Login")

        submit_button.setOnClickListener(this)

        // 카카오 로그인 버튼을 클릭했을 때 Access Token을 요청토록 설정합니다.
        Session.getCurrentSession().addCallback(sessionCallback)
        Session.getCurrentSession().checkAndImplicitOpen()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if ( Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data) )
            return
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onDestroy() {
        super.onDestroy()
        Session.getCurrentSession().removeCallback(sessionCallback)
    }

    override fun onClick(v: View) {
        if ( v.id == submit_button.id ) {
            submit_button.isEnabled = false

            val username = username_textview.text.toString()
            val password = password_textview.text.toString()

            launch(UI) {
                val userToken = blogManager.getUserToken(username, password)
                redirectMainActivity(userToken)
                submit_button.isEnabled = true
            }
        }
    }

    inner class SessionCallback: ISessionCallback {
        // access token을 성공적으로 발급받아 valid access token을 가지고 있는 상태
        override fun onSessionOpened() {
            val accessToken = Session.getCurrentSession().tokenInfo.accessToken
            Log.d(TAG, "accessToken : ${accessToken}")
            launch(UI) {
                val userToken = blogManager.getUserTokenWithProvider("kakao", accessToken)
                redirectMainActivity(userToken)
            }
        }

        // memory와 cache에 session정보가 전혀없는 상태.
        // 일반적으로 로그인 버튼이 보이고, 사용자가 클릭시 동의를 받아 access token 요청을
        // 시도한다.
        override fun onSessionOpenFailed(exception: KakaoException?) {
            if ( exception != null )
                Log.e(TAG, "")
        }
    }

    protected fun redirectMainActivity(userToken: String) {
        if ( userToken.isEmpty() ) {
            toast("Login Failed.")
        }
        else {
            val returnIntent = Intent()
            returnIntent.putExtra("userToken", userToken)
            setResult(RESULT_OK, returnIntent)
            finish()
            overridePendingTransition(R.anim.slide_up, R.anim.slide_down)
        }
    }

    companion object {
        val TAG = LoginActivity::class.java.name
    }
}
