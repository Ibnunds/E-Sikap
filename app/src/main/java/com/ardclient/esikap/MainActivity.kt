package com.ardclient.esikap

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.ardclient.esikap.fragment.DashboardFragment
import com.ardclient.esikap.fragment.ProfileFragment
import com.ardclient.esikap.model.ApiResponse
import com.ardclient.esikap.model.UserSessionModel
import com.ardclient.esikap.model.api.UserLoginResponse
import com.ardclient.esikap.service.ApiClient
import com.ardclient.esikap.utils.LocaleHelper
import com.ardclient.esikap.utils.SessionUtils
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private lateinit var userSession: UserSessionModel
    private lateinit var bottomNav: BottomNavigationView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNav = findViewById(R.id.bottom_navigation)

        // -- Handle bottom navigation
        userSession = SessionUtils.getUserSession(this)

        onCekStatus()

        // define fragment
        val dashboardFragment = DashboardFragment()
        val profileFragment = ProfileFragment()

        setCurrentFragment(dashboardFragment)

        bottomNav.selectedItemId = R.id.imHome

        // handle bottom nav
        bottomNav.setOnItemSelectedListener{
            onCekStatus()
            when(it.itemId){
                R.id.imHome -> {
                    setCurrentFragment(dashboardFragment)
                    true
                }

                R.id.imProfile -> {
                    setCurrentFragment(profileFragment)
                    true
                }
                else -> false
            }
        }
    }

    private fun onCekStatus() {
        val call = ApiClient.apiService.userStatus(userSession.userName!!)

        call.enqueue(object: Callback<ApiResponse<UserLoginResponse>>{
            override fun onResponse(
                call: Call<ApiResponse<UserLoginResponse>>,
                response: Response<ApiResponse<UserLoginResponse>>
            ) {
                if (response.isSuccessful){
                    handleStatus(response.body(), userSession)
                }else{
                    Log.d("CEK STATUS USER", response.toString())
                }
            }

            override fun onFailure(call: Call<ApiResponse<UserLoginResponse>>, t: Throwable) {
                Log.d("CEK STATUS USER", "FAILED")
            }
        })
    }

    private fun handleStatus(data: ApiResponse<UserLoginResponse>?, session: UserSessionModel) {
        val userData = data?.data

        if (userData?.aktif == 0){
            onLogout()
        }

        userData?.password?.let {
            if (it != session.userPassword){
                onLogout()
            }
        }

//        if (userData?.password !== session.userPassword){
//            onLogout()
//        }
    }

    private fun onLogout(){
        SessionUtils.clearUserSession(this, object: SessionUtils.OnSessionClear{
            override fun onSessionCleared() {
                val intent = Intent(applicationContext, LoginActivity::class.java)
                startActivity(intent)

                finish()
            }
        })
    }

    private fun setCurrentFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.flFragment, fragment).commit()
    }

    override fun onResume() {
        super.onResume()
        // define fragment
        val dashboardFragment = DashboardFragment()
        val profileFragment = ProfileFragment()

        when(bottomNav.selectedItemId){
            R.id.imHome -> {
                setCurrentFragment(dashboardFragment)
                true
            }
            R.id.imProfile -> {
                setCurrentFragment(profileFragment)
                true
            }
        }

        onCekStatus()
    }

    override fun attachBaseContext(base: Context?) {
        LocaleHelper().setLocale(base!!, LocaleHelper().getLanguage(base))
        super.attachBaseContext(LocaleHelper().onAttach(base))
    }
}