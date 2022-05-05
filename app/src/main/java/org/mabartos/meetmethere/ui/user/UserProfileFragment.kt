package org.mabartos.meetmethere.ui.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import org.mabartos.meetmethere.R
import org.mabartos.meetmethere.databinding.FragmentUserProfileBinding
import org.mabartos.meetmethere.service.user.UserService
import org.mabartos.meetmethere.service.user.UserServiceUtil
import org.mabartos.meetmethere.util.toast

class UserProfileFragment(
    private val userService: UserService = UserServiceUtil.createService()
) : Fragment() {

    private lateinit var binding: FragmentUserProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserProfileBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.userProfileToolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        binding.userProfileToolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        binding.userProfileToolbar.title = resources.getString(R.string.user_profile_title)

        val user = userService.getCurrentUser()

        if (user == null) {
            findNavController().navigateUp()
            context?.toast(resources.getString(R.string.no_signed_in_user))
        }

        binding.userProfileUsername.text = user!!.username
        val fullName = user.getFullName()
        if (fullName.isNotBlank()) {
            binding.userProfileName.text = fullName
        } else {
            binding.userProfileName.visibility = View.GONE
        }

        binding.userProfileEmail.text = user.email

        binding.userProfileOrganizedButton.text =
            resources.getString(R.string.show_organized_events)
        binding.userProfileAttributes.text = resources.getString(R.string.show_attributes)
        binding.userProfileLogout.text = resources.getString(R.string.logout)

        //TODO click handlers

        binding.userProfileOrganizedButton.setOnClickListener {
            findNavController().navigate(
                UserProfileFragmentDirections.actionProfileEditToOrganizedEvents(
                    user
                )
            )
        }

        binding.userProfileEditButton.setOnClickListener {
            findNavController().navigate(UserProfileFragmentDirections.actionUserProfileToProfileEdit())
        }

        binding.userProfileLogout.setOnClickListener {
            userService.logout()
            findNavController().navigate(UserProfileFragmentDirections.actionUserProfileToLogin())
            context?.toast(resources.getString(R.string.signed_off_note))
        }
    }

}