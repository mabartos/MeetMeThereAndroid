package org.mabartos.meetmethere.ui.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import org.mabartos.meetmethere.R
import org.mabartos.meetmethere.data.user.User
import org.mabartos.meetmethere.databinding.FragmentUserEditBinding
import org.mabartos.meetmethere.service.user.ModelDuplicateException
import org.mabartos.meetmethere.service.user.UserService
import org.mabartos.meetmethere.service.user.UserServiceUtil
import org.mabartos.meetmethere.util.InputUtils
import org.mabartos.meetmethere.util.toast

class UserEditFragment(
    private val userService: UserService = UserServiceUtil.createService()
) : Fragment() {

    private lateinit var binding: FragmentUserEditBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserEditBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.userUpdateToolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        binding.userUpdateToolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        binding.userUpdateToolbar.title = resources.getString(R.string.edit_user_profile_title)

        userService.getCurrentUser(
            onSuccess = { user ->
                binding.userUpdateUsernameInput.setText(user.username)
                binding.userUpdateEmailInput.setText(user.email)
                binding.userUpdateFirstnameInput.setText(user.firstName)
                binding.userUpdateLastnameInput.setText(user.lastName)

                binding.userUpdateAttrButton.text =
                    resources.getString(R.string.edit_user_attributes)
                binding.userUpdatePasswordButton.text =
                    resources.getString(R.string.change_password)

                binding.userUpdateAttrButton.setOnClickListener {
                    findNavController().navigate(
                        UserEditFragmentDirections.actionUserProfileEditToUserAttributes()
                    )
                }

                binding.userUpdatePasswordButton.setOnClickListener {
                    findNavController().navigate(
                        UserEditFragmentDirections.actionUserProfileEditFragmentToUserChangePasswordFragment(
                            user
                        )
                    )
                }

                binding.userUpdateSaveButton.setOnClickListener {
                    var isUpdated = false
                    val builder = User.Builder(user)

                    val username = binding.userUpdateUsernameInput.text
                    val usernameError = InputUtils.errorOnBlankField(
                        username,
                        binding.userUpdateUsername,
                        resources.getString(R.string.missing_username)
                    )
                    if (usernameError) return@setOnClickListener

                    val email = binding.userUpdateEmailInput.text
                    val emailError = InputUtils.errorOnBlankField(
                        email,
                        binding.userUpdateEmail,
                        resources.getString(R.string.missing_email)
                    )
                    if (emailError) return@setOnClickListener

                    val firstName = binding.userUpdateFirstnameInput.text
                    val lastName = binding.userUpdateLastnameInput.text

                    //TODO
                    if (username.toString() != user.username) {
                        userService.findByUsername(
                            username = username.toString(),
                            onSuccess = {
                                binding.userUpdateUsername.error =
                                    resources.getString(R.string.duplicate_username)
                            },
                            onFailure = {
                                if (it is ModelDuplicateException) {
                                    isUpdated = true
                                    builder.username(username.toString())
                                }
                            })
                    }

                    if (email.toString() != user.email) {
                        userService.findByEmail(
                            email = email.toString(),
                            onSuccess = {
                                binding.userUpdateEmail.error =
                                    resources.getString(R.string.duplicate_email)
                            },
                            onFailure = {
                                if (it is ModelDuplicateException) {
                                    isUpdated = true
                                    builder.email(email.toString())
                                }
                            })
                    }

                    // -----------------------------------------------------------------

                    if (firstName.toString() != user.firstName) {
                        isUpdated = true
                        builder.firstName(firstName.toString())
                    }

                    if (lastName.toString() != user.lastName) {
                        isUpdated = true
                        builder.lastName(lastName.toString())
                    }

                    if (isUpdated) {
                        userService.updateUser(
                            user = builder.build(),
                            onSuccess = {
                                context?.toast("User updated")
                                findNavController().navigateUp()
                            },
                            onFailure = { e ->
                                context?.toast("Cannot update user. ${e.message}")
                            })
                    }
                }
            }, onFailure = {
                findNavController().navigateUp()
                context?.toast(resources.getString(R.string.no_signed_in_user))
            })
    }
}