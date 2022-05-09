package org.mabartos.meetmethere.ui.login

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import org.mabartos.meetmethere.R
import org.mabartos.meetmethere.data.user.CreateUser
import org.mabartos.meetmethere.databinding.FragmentUserRegisterBinding
import org.mabartos.meetmethere.service.user.ModelDuplicateException
import org.mabartos.meetmethere.service.user.UserService
import org.mabartos.meetmethere.service.user.UserServiceUtil
import org.mabartos.meetmethere.util.InputUtils

class UserRegisterFragment(
    private val userService: UserService = UserServiceUtil.createService()
) : Fragment() {
    private lateinit var binding: FragmentUserRegisterBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserRegisterBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.userRegisterToolbar.setNavigationIcon(R.drawable.ic_close)
        binding.userRegisterToolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        binding.userRegisterToolbar.title = "Register"

        binding.userRegisterButton.setOnClickListener {
            val username = binding.userRegisterUsernameInput.text
            val usernameError = InputUtils.errorOnBlankField(
                username,
                binding.userRegisterUsername,
                resources.getString(R.string.missing_username)
            )
            if (usernameError) return@setOnClickListener

            val email = binding.userRegisterEmailInput.text
            val emailError = InputUtils.errorOnBlankField(
                email,
                binding.userRegisterEmail,
                resources.getString(R.string.missing_email)
            )
            if (emailError) return@setOnClickListener

            val firstName = binding.userRegisterFirstnameInput.text
            val lastName = binding.userRegisterLastnameInput.text

            val password = binding.userRegisterPasswordInput.text
            val passwordError = InputUtils.errorOnBlankField(
                password,
                binding.userRegisterPassword,
                resources.getString(R.string.missing_password)
            )
            if (passwordError) return@setOnClickListener

            val passwordConfirm = binding.userRegisterPasswordConfirmInput.text
            val passwordConfirmError = InputUtils.errorOnBlankField(
                passwordConfirm,
                binding.userRegisterPasswordConfirm,
                resources.getString(R.string.missing_password_confirmation)
            )
            if (passwordConfirmError) return@setOnClickListener

            if (password.toString() != passwordConfirm.toString()) {
                binding.userRegisterPasswordConfirm.error =
                    resources.getString(R.string.passwords_not_match)
                return@setOnClickListener
            } else {
                binding.userRegisterPasswordConfirm.error = ""
            }

            userService.register(
                CreateUser(
                    username = username.toString(),
                    email = email.toString(),
                    firstName = firstName.toString(),
                    lastName = lastName.toString(),
                    password = password.toString(),
                    attributes = HashMap()
                ),
                onSuccess = { findNavController().navigate(UserRegisterFragmentDirections.actionRegisterToHome()) },
                onFailure = { e ->
                    if (e is ModelDuplicateException) {
                        if (e.getField() == UserService.USERNAME_FIELD) {
                            binding.userRegisterUsername.error =
                                resources.getString(R.string.duplicate_username)
                        } else if (e.getField() == UserService.EMAIL_FIELD) {
                            binding.userRegisterEmail.error =
                                resources.getString(R.string.duplicate_email)
                        }
                    } else {
                        Log.e(tag.toString(), "Cannot register", e)
                    }
                }
            )
        }
    }
}