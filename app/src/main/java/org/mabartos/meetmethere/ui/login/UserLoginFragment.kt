package org.mabartos.meetmethere.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import org.mabartos.meetmethere.R
import org.mabartos.meetmethere.databinding.FragmentUserLoginBinding
import org.mabartos.meetmethere.service.user.UserService
import org.mabartos.meetmethere.service.user.UserServiceUtil
import org.mabartos.meetmethere.util.InputUtils.Companion.errorOnBlankField
import org.mabartos.meetmethere.util.toast

class UserLoginFragment(
    private val userService: UserService = UserServiceUtil.createService()
) : Fragment() {

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
            val username = binding.userLoginUsernameInput.text
            val password = binding.userLoginPasswordInput.text

            val errorUsername = errorOnBlankField(
                username,
                binding.userLoginUsername,
                resources.getString(R.string.missing_username)
            )
            if (errorUsername) return@setOnClickListener

            val errorPassword = errorOnBlankField(
                password,
                binding.userLoginPassword,
                resources.getString(R.string.missing_password)
            )
            if (errorPassword) return@setOnClickListener

            if (userService.login(username = username.toString(), password = password.toString())) {
                findNavController().navigate(UserLoginFragmentDirections.actionLoginToEventList())
            } else {
                context?.toast("Invalid password or user doesn't exist")
            }
        }

        binding.userLoginRegisterButton.setOnClickListener {
            findNavController().navigate(UserLoginFragmentDirections.actionLoginToRegister())
        }

    }
}