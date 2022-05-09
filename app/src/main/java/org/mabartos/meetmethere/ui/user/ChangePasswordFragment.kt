package org.mabartos.meetmethere.ui.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import org.mabartos.meetmethere.R
import org.mabartos.meetmethere.data.user.User
import org.mabartos.meetmethere.databinding.FragmentUserChangePasswordBinding
import org.mabartos.meetmethere.service.user.UserService
import org.mabartos.meetmethere.service.user.UserServiceUtil
import org.mabartos.meetmethere.util.InputUtils
import org.mabartos.meetmethere.util.toast

class ChangePasswordFragment(
    private val userService: UserService = UserServiceUtil.createService()
) : Fragment() {

    private lateinit var binding: FragmentUserChangePasswordBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserChangePasswordBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.userChangePwdToolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        binding.userChangePwdToolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        binding.userChangePwdToolbar.title = resources.getString(R.string.change_password)

        val user: User = ChangePasswordFragmentArgs.fromBundle(requireArguments()).user

        binding.userChangePwdSaveButton.setOnClickListener {
            val oldPwd = binding.userChangePwdOldInput.text
            val oldPwdError = InputUtils.errorOnBlankField(
                oldPwd,
                binding.userChangePwdOld,
                resources.getString(R.string.missing_password)
            )
            if (oldPwdError) return@setOnClickListener

            val newPwd = binding.userChangePwdNewInput.text
            val newPwdError = InputUtils.errorOnBlankField(
                newPwd,
                binding.userChangePwdNew,
                resources.getString(R.string.missing_password)
            )
            if (newPwdError) return@setOnClickListener

            val newPwdConfirm = binding.userChangePwdNewConfirmInput.text
            val newPwdConfirmError = InputUtils.errorOnBlankField(
                newPwdConfirm,
                binding.userChangePwdNewConfirm,
                resources.getString(R.string.missing_password_confirmation)
            )
            if (newPwdConfirmError) return@setOnClickListener

            if (oldPwd.toString() != user.password) {
                binding.userChangePwdOld.error = resources.getString(R.string.wrong_password)
                return@setOnClickListener
            } else {
                binding.userChangePwdOld.error = ""
            }

            if (newPwd.toString() != newPwdConfirm.toString()) {
                binding.userChangePwdNewConfirm.error =
                    resources.getString(R.string.passwords_not_match)
                return@setOnClickListener
            } else {
                binding.userChangePwdNewConfirm.error = ""
            }

            userService.updateUser(
                user = User.Builder(user).password(newPwd.toString()).build(),
                onSuccess = {
                    context?.toast(resources.getString(R.string.password_was_changed))
                    findNavController().navigateUp()
                },
                onFailure = {
                    context?.toast(resources.getString(R.string.password) + ". ${it.message}")
                })
        }
    }
}