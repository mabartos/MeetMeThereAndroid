package org.mabartos.meetmethere.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import org.mabartos.meetmethere.databinding.FragmentUserLoginBinding

class UserLoginFragment : Fragment() {

    private lateinit var binding: FragmentUserLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserLoginBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.userLoginButton.setOnClickListener {
            findNavController().navigate(UserLoginFragmentDirections.actionLoginToEventList())
        }

        binding.userLoginRegisterButton.setOnClickListener {
            findNavController().navigate(UserLoginFragmentDirections.actionLoginToRegister())
        }

    }
}