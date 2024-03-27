package com.ardclient.esikap

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.ardclient.esikap.databinding.ActivityLoginBinding
import com.ardclient.esikap.modal.SpinnerModal
import com.ardclient.esikap.model.ApiResponse
import com.ardclient.esikap.model.api.UserLoginRequest
import com.ardclient.esikap.model.api.UserLoginResponse
import com.ardclient.esikap.service.ApiClient
import com.ardclient.esikap.utils.Constants
import com.ardclient.esikap.utils.InputValidation
import com.ardclient.esikap.utils.SessionUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var spinner: SpinnerModal


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // splash
        val splash = installSplashScreen()
        splash.setKeepOnScreenCondition{false}


        // if user exist go to main
        val userSession = SessionUtils.getUserSession(this)
        if (userSession.userName!!.isNotEmpty()){
            Log.d("USER SESSION ID",userSession.userId.toString())
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        spinner = SpinnerModal()

        // session
        sharedPreferences = getSharedPreferences(Constants.USER_SESSION_PREFS_KEY, Context.MODE_PRIVATE)

        binding.loginButton.setOnClickListener {
            onLoginButton()
        }
    }

    private fun onLoginButton() {
        spinner.show(supportFragmentManager, "LOADING")
        with(binding) {
            val username = inUsername.editText?.text.toString()
            val password = inPassword.editText?.text.toString()

            val inputValidate = InputValidation.isAllFieldComplete(
                inUsername,
                inPassword
            )

            if (inputValidate){
                onLogin(username, password)
            }
        }
    }

    private fun onLogin(username: String, password: String) {
        val bodyRequest = UserLoginRequest(username, password)

        val call = ApiClient.apiService.userLogin(bodyRequest)

        call.enqueue(object : Callback<ApiResponse<UserLoginResponse>>{
            override fun onResponse(
                call: Call<ApiResponse<UserLoginResponse>>,
                response: Response<ApiResponse<UserLoginResponse>>
            ) {
                spinner.dismiss()
                if (response.isSuccessful){
                    saveUserSession(response.body())
                }else{
                    Toast.makeText(this@LoginActivity, response.message(), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse<UserLoginResponse>>, t: Throwable) {
                spinner.dismiss()
                Toast.makeText(this@LoginActivity, "Ada yang tidak beres, mohon coba lagi!", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun saveUserSession(body: ApiResponse<UserLoginResponse>?) {
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        val userData = body?.data

        val userId = userData?.id
        val name = userData?.nama
        val username = userData?.username
        val wilayah = userData?.wilayah
        val level = userData?.level

        // save session
        editor.putInt(Constants.USERID_KEY, userId!!)
        editor.putString(Constants.NAME_KEY, name)
        editor.putString(Constants.USERNAME_KEY, username)
        editor.putString(Constants.WILAYAH_KEY, wilayah)
        editor.putString(Constants.USER_LEVEL, level)

        editor.apply()

        // navigate to main
        // on complete
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        startActivity(intent)

        finish()
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }
}