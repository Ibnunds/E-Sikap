package com.ardclient.esikap.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.ardclient.esikap.LoginActivity
import com.ardclient.esikap.R
import com.ardclient.esikap.databinding.FragmentProfileBinding
import com.ardclient.esikap.model.UserSessionModel
import com.ardclient.esikap.utils.SessionUtils


class ProfileFragment : Fragment(R.layout.fragment_profile) {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var userSession: UserSessionModel
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentProfileBinding.bind(view)


        // get user session
        userSession = SessionUtils.getUserSession(requireActivity())

        binding.tvName.text = userSession.name
        binding.tvLevel.text = userSession.userLevel

        binding.logoutButton.setOnClickListener {
            SessionUtils.clearUserSession(requireActivity(), object: SessionUtils.OnSessionClear{
                override fun onSessionCleared() {
                    val intent = Intent(requireContext(), LoginActivity::class.java)
                    startActivity(intent)

                    requireActivity().finish()
                }
            })
        }
    }
}