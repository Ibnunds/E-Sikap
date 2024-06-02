package com.ardclient.esikap.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import coil.load
import coil.transform.CircleCropTransformation
import com.ardclient.esikap.LanguageActivity
import com.ardclient.esikap.LoginActivity
import com.ardclient.esikap.R
import com.ardclient.esikap.UpdateProfileActivity
import com.ardclient.esikap.databinding.FragmentProfileBinding
import com.ardclient.esikap.modal.ImageSelectorModal
import com.ardclient.esikap.modal.SpinnerModal
import com.ardclient.esikap.model.ApiResponse
import com.ardclient.esikap.model.UserSessionModel
import com.ardclient.esikap.model.api.UploadAvatarBody
import com.ardclient.esikap.service.ApiClient
import com.ardclient.esikap.utils.Base64Utils
import com.ardclient.esikap.utils.Constants
import com.ardclient.esikap.utils.SessionUtils
import com.github.dhaval2404.imagepicker.ImagePicker
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ProfileFragment : Fragment(R.layout.fragment_profile) {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var userSession: UserSessionModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentProfileBinding.bind(view)

        // get user session
        userSession = SessionUtils.getUserSession(requireActivity())

        // Set initial user details
        setUserDetails()

        binding.logoutButton.setOnClickListener {
            SessionUtils.clearUserSession(requireActivity(), object: SessionUtils.OnSessionClear{
                override fun onSessionCleared() {
                    val intent = Intent(requireContext(), LoginActivity::class.java)
                    startActivity(intent)

                    requireActivity().finish()
                }
            })
        }


        // Select Language
        binding.menuLanguage.setOnClickListener {
            val intent = Intent(requireContext(), LanguageActivity::class.java)
            startActivity(intent)
        }

        binding.avatarContainer.setOnClickListener {
            val intent = Intent(requireContext(), UpdateProfileActivity::class.java)
            intent.putExtra("AVATAR", userSession.userAvatar)
            intent.putExtra("USER_ID", userSession.userId)
            startActivity(intent)
        }
    }

    private fun setUserDetails() {
        val desc = "( ${userSession.userName} - ${userSession.userLevel} )"
        binding.tvName.text = userSession.name
        binding.tvUsername.text = desc
        binding.profilePic.load(userSession.userAvatar) {
            transformations(CircleCropTransformation())
        }
    }
}