package com.ardclient.esikap

import android.app.LocaleManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.LocaleList
import android.view.View
import android.widget.Toast
import com.ardclient.esikap.databinding.ActivityLanguageBinding
import com.ardclient.esikap.utils.Constants
import com.ardclient.esikap.utils.LocaleHelper
import com.ardclient.esikap.utils.StorageUtils
import java.util.Locale

class LanguageActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var binding: ActivityLanguageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLanguageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // session
        sharedPreferences = getSharedPreferences(Constants.USER_LANGUAGE_PREFS_KEY, Context.MODE_PRIVATE)

        val currentLang = StorageUtils.getLang(this)

        with(binding){
            topAppBar.setNavigationOnClickListener {
                finish()
            }

            if (currentLang == "en"){
                checkEn.visibility = View.VISIBLE
                checkId.visibility = View.GONE
            }

            if (currentLang == "in"){
                checkEn.visibility = View.GONE
                checkId.visibility = View.VISIBLE
            }

            // set lang to english
            langEn.setOnClickListener {
                onChangeLang("en")
            }


            // set lang to indonesia
            langId.setOnClickListener {
                onChangeLang("in")
            }
        }
    }

    private fun onChangeLang(lang: String) {
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString("LANG", lang)
        editor.apply()

        if (lang == "en"){
            binding.checkEn.visibility = View.VISIBLE
            binding.checkId.visibility = View.GONE
        }

        if (lang == "in"){
            binding.checkEn.visibility = View.GONE
            binding.checkId.visibility = View.VISIBLE
        }

        setAppLocale(Locale.forLanguageTag(lang), lang)

        Toast.makeText(this, getString(R.string.language_changed), Toast.LENGTH_SHORT).show()
    }

    private fun setAppLocale(lang: Locale, code: String) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            val localeManager = getSystemService(LocaleManager::class.java)
//            localeManager.applicationLocales = LocaleList(lang)
//        } else {
//            LocaleHelper().setLocale(applicationContext, code)
//            recreate()
//            restartApp()
//            //Toast.makeText(this, "Versi android anda tidak mensupport untuk mengganti bahasa secara langsung!", Toast.LENGTH_SHORT).show()
//        }
        LocaleHelper().setLocale(applicationContext, code)
        recreate()
        restartApp()
    }

    override fun attachBaseContext(base: Context?) {
        LocaleHelper().setLocale(base!!, LocaleHelper().getLanguage(base))
        super.attachBaseContext(LocaleHelper().onAttach(base))
    }

    private fun restartApp() {
        val intent = Intent(this, LoginActivity::class.java) // Atau aktivitas awal aplikasi
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish() // Menutup aktivitas saat ini untuk mencegah tumpang tindih
    }
}