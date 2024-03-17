package com.ardclient.esikap.cop

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.ardclient.esikap.R
import com.ardclient.esikap.databinding.ActivityCopInputBinding
import com.ardclient.esikap.model.COPModel

class CopInputActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCopInputBinding
    private var launcher: ActivityResultLauncher<Intent>? = null
    private lateinit var copBasicData: COPModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCopInputBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // header
        binding.topAppBar.setNavigationOnClickListener {
            finish()
        }

        // init value
        copBasicData = COPModel()

        // handle input result
        launcher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data

                // Receive Data
                val basicCOPData = data?.getParcelableExtra<COPModel>("COP_BASIC")
                if (basicCOPData !=null){
                    copBasicData = basicCOPData
                    binding.chipCOPDataUmum.isChecked = true
                    binding.chipCOPDataUmum.text = "Lengkap"
                }
            }
        }

        // card button
        binding.cardCOPDataUmum.setOnClickListener {
            val intent = Intent(this, CopInputDataUmumActivity::class.java)
            intent.putExtra("EXISTING_DATA", copBasicData)
            launcher?.launch(intent)
        }

        binding.cardCOPDokumenKapal.setOnClickListener {
            val intent = Intent(this, CopInputDokumenActivity::class.java)
            launcher?.launch(intent)
        }
    }

    private fun setCurrentFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.inputFragment, fragment).commit()
    }


}