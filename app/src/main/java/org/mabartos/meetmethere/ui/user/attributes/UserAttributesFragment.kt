package org.mabartos.meetmethere.ui.user.attributes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.mabartos.meetmethere.R
import org.mabartos.meetmethere.databinding.FragmentUserAttributesBinding
import org.mabartos.meetmethere.service.user.UserService
import org.mabartos.meetmethere.service.user.UserServiceUtil
import org.mabartos.meetmethere.util.toast
import java.util.*
import java.util.stream.Collectors

class UserAttributesFragment(
    private val userService: UserService = UserServiceUtil.createService()
) : Fragment() {
    private lateinit var binding: FragmentUserAttributesBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserAttributesBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.userAttributesToolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        binding.userAttributesToolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        binding.userAttributesToolbar.title = resources.getString(R.string.user_attributes_title)

        binding.createAttributeFloatingButton.setOnClickListener { }

        val user = userService.getCurrentUser() ?: return

        if (user.attributes.isEmpty()) {
            binding.userAttributesList.visibility = View.GONE
            binding.userAttributesEmpty.visibility = View.VISIBLE
            binding.userAttributesEmpty.text = resources.getText(R.string.user_attributes_empty)
        } else {
            binding.userAttributesList.visibility = View.VISIBLE
            binding.userAttributesEmpty.visibility = View.GONE

            val adapter = UserAttributeListAdapter(
                onItemClick = {},
                onDeleteClick = { item ->
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle(R.string.deleteThisAttributeQuestion)
                        .setMessage(R.string.deleteAttributeConfirmationMsg)
                        .setNeutralButton(resources.getString(R.string.cancel)) { _, _ ->
                        }
                        .setPositiveButton(
                            resources.getString(R.string.confirm).uppercase()
                        ) { _, _ ->
                            userService.removeAttribute(user.id, item.key)
                            context?.toast(resources.getString(R.string.user_attribute_deleted))
                            onViewCreated(view, savedInstanceState)
                        }
                        .show()
                }
            )

            binding.userAttributesList.layoutManager = LinearLayoutManager(requireContext())
            binding.userAttributesList.adapter = adapter

            val attributes: List<UserAttributeItem> =
                user.attributes
                    .entries
                    .stream()
                    .filter { f -> Objects.nonNull(f) }
                    .map { f -> UserAttributeItem(f.key, f.value) }
                    .collect(Collectors.toList())

            adapter.submitList(attributes)
        }
    }
}