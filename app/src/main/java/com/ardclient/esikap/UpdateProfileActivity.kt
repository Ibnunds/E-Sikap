package com.ardclient.esikap

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import coil.load
import coil.transform.CircleCropTransformation
import com.ardclient.esikap.databinding.ActivityUpdateProfileBinding
import com.ardclient.esikap.modal.ImageSelectorModal
import com.ardclient.esikap.modal.SpinnerModal
import com.ardclient.esikap.model.ApiResponse
import com.ardclient.esikap.model.api.UploadAvatarBody
import com.ardclient.esikap.service.ApiClient
import com.ardclient.esikap.utils.Base64Utils
import com.ardclient.esikap.utils.Constants
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UpdateProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUpdateProfileBinding
    private var selectedImageUri:Uri ?= null
    private lateinit var spinner: SpinnerModal
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val defaultAvatar = intent.getStringExtra("AVATAR")
        val userId = intent.getIntExtra("USER_ID", 0)

        spinner = SpinnerModal()

        // session
        sharedPreferences = getSharedPreferences(Constants.USER_SESSION_PREFS_KEY, Context.MODE_PRIVATE)

        with(binding){
            topAppBar.setNavigationOnClickListener {
                finish()
            }

            profilePic.load(defaultAvatar){
                transformations(CircleCropTransformation())
            }

            avatarContainer.setOnClickListener {
                onSelectImage()
            }

            uploadButton.setOnClickListener {
                if (selectedImageUri != null){
                    onImageUpload(userId)
                }
            }
        }
    }

    private fun onImageUpload(userId: Int) {
        spinner.show(supportFragmentManager, "LOADING")
        val imageEncode = Base64Utils.uriToBase64(applicationContext, selectedImageUri!!)
        val body = UploadAvatarBody(imageEncode!!)

        val call = ApiClient.apiService.uploadAvatar(userId, body)

        call.enqueue(object : Callback<ApiResponse<String>> {
            override fun onResponse(
                call: Call<ApiResponse<String>>,
                response: Response<ApiResponse<String>>
            ) {
                spinner.dismiss()
                if (response.isSuccessful){
                    val data = response.body()?.data
                    val editor: SharedPreferences.Editor = sharedPreferences.edit()
                    editor.putString(Constants.AVATAR_KEY, data)
                    editor.apply()

                    Toast.makeText(applicationContext, "Berhasil mengupload avatar.", Toast.LENGTH_SHORT).show()
                    finish()
                }else{
                    Toast.makeText(applicationContext, "Gagal mengupload avatar.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse<String>>, t: Throwable) {
                spinner.dismiss()
                Toast.makeText(applicationContext, "Gagal mengupload avatar.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun onSelectImage() {
        val imageSelectorDialog = ImageSelectorModal(object : ImageSelectorModal.OnImageSelectedListener{
            override fun onImageSelected(imageUri: Uri) {
                selectedImageUri = imageUri
                binding.profilePic.load(imageUri){
                    transformations(CircleCropTransformation())
                }
            }
        })
        imageSelectorDialog.show(supportFragmentManager, "IMAGE_PICKER")
    }
}