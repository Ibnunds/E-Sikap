package com.ardclient.esikap

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.ardclient.esikap.fragment.input.phqc.PHQCFragment
import com.ardclient.esikap.model.KapalModel
import com.ardclient.esikap.model.PHQCModel
import com.google.android.material.appbar.MaterialToolbar


class PHQCInputActivity : AppCompatActivity() {
    private lateinit var kapal: KapalModel
    private lateinit var phqc: PHQCModel
    private var isUpdate: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phqc_input)

        // init
        val header = findViewById<MaterialToolbar>(R.id.topAppBar)

        // check is from update
        val updateData = intent.getParcelableExtra<PHQCModel>("PHQC")
        if (updateData!=null){
            phqc = updateData
            isUpdate = true
        }else{
            phqc = PHQCModel()
        }

        // existing kapal data
        val kapalData = intent.getParcelableExtra<KapalModel>("KAPAL")

        if (kapalData != null){
            kapal = kapalData
        }else{
            kapal = KapalModel()
        }

        // fragment
        val blue1Fragment = PHQCFragment.newInstance(isUpdate, kapal, phqc)

        // header
        header.setNavigationOnClickListener {
            finish()
        }

        // init fragment
        setCurrentFragment(blue1Fragment)
    }

    private fun setCurrentFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.inputFragment, fragment).commit()
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