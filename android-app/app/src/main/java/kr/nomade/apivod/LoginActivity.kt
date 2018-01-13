package kr.nomade.apivod

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch


class LoginActivity : AppCompatActivity(), View.OnClickListener {
    private val blogManager by androidLazy { BlogManager() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setSupportActionBar(toolbar)
        setTitle("Login")

        submit_button.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        if ( v.id == submit_button.id ) {
            submit_button.isEnabled = false

            val username = username_textview.text.toString()
            val password = password_textview.text.toString()

            launch(UI) {
                val userToken = blogManager.getUserToken(username, password)
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

                submit_button.isEnabled = true
            }
        }
    }
}
