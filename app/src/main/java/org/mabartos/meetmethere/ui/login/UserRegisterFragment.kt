package org.mabartos.meetmethere.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import org.mabartos.meetmethere.R
import org.mabartos.meetmethere.databinding.FragmentUserRegisterBinding

class UserRegisterFragment : Fragment() {
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
            findNavController().navigate(UserRegisterFragmentDirections.actionRegisterToHome())
        }
    }
}