package com.ardclient.esikap

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.ardclient.esikap.databinding.ActivityLoginBinding
import com.ardclient.esikap.utils.Constants
import com.ardclient.esikap.utils.InputValidation

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    private lateinit var sharedPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // session
        sharedPreferences = getSharedPreferences(Constants.USER_SESSION_PREFS_KEY, Context.MODE_PRIVATE)

        binding.loginButton.setOnClickListener {
            onLoginButton()
        }
    }

    private fun onLoginButton() {
        with(binding) {
            val username = inUsername.editText?.text.toString()
            val password = inPassword.editText?.text.toString()

            val inputValidate = InputValidation.isAllFieldComplete(
                inUsername,
                inPassword
            )

            if (inputValidate){
                val editor: SharedPreferences.Editor = sharedPreferences.edit()


                // save session
                editor.putInt(Constants.USERID_KEY, 1)
                editor.putString(Constants.NAME_KEY, "DEV")
                editor.putString(Constants.USERNAME_KEY, username)
                editor.putString(Constants.WILAYAH_KEY, "JAKARTA")

                editor.apply()

                // on complete
                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                startActivity(intent)

                finish()
            }
        }
    }
}