package org.mabartos.meetmethere.ui.user.attributes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import org.mabartos.meetmethere.R
import org.mabartos.meetmethere.databinding.FragmentUserAttributeCreateBinding
import org.mabartos.meetmethere.service.user.UserService
import org.mabartos.meetmethere.service.user.UserServiceUtil
import org.mabartos.meetmethere.util.InputUtils
import org.mabartos.meetmethere.util.toast

class UserAttributeCreateFragment(
    private val userService: UserService = UserServiceUtil.createService()
) : Fragment() {

    private lateinit var binding: FragmentUserAttributeCreateBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserAttributeCreateBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.userAttributeAddToolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        binding.userAttributeAddToolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        binding.userAttributeAddToolbar.title = resources.getString(R.string.user_attribute_create)

        binding.userAttributeAddCreateButton.text = resources.getString(R.string.create)

        userService.getCurrentUser(
            onSuccess = { user ->
                binding.userAttributeAddCreateButton.setOnClickListener {
                    val key = binding.userAttributeAddKeyInput.text
                    val keyError = InputUtils.errorOnBlankField(
                        key,
                        binding.userAttributeAddKey,
                        resources.getString(R.string.missing_key)
                    )
                    if (keyError) return@setOnClickListener

                    val value = binding.userAttributeAddValueInput.text
                    val valueError = InputUtils.errorOnBlankField(
                        value,
                        binding.userAttributeAddValue,
                        resources.getString(R.string.missing_value)
                    )
                    if (valueError) return@setOnClickListener

                    if (user.attributes.containsKey(key.toString())) {
                        binding.userAttributeAddKey.error =
                            resources.getString(R.string.user_attribute_duplicate)
                    } else {
                        binding.userAttributeAddKey.error = ""
                        userService.addAttribute(
                            user.id,
                            key.toString(),
                            value.toString(),
                            onSuccess = {
                                context?.toast(resources.getString(R.string.user_attribute_created))
                                findNavController().navigateUp()
                            },
                            onFailure = {
                                context?.toast(resources.getString(R.string.user_attribute_cannot_create))
                            }
                        )
                    }
                }
            }, onFailure = {
                context?.toast("Cannot find current user")
            })
    }

}