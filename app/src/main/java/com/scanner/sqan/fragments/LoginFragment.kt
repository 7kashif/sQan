package com.scanner.sqan.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.scanner.sqan.databinding.LoginFragmentBinding
import com.scanner.sqan.models.Progress
import com.scanner.sqan.viewModels.LoginViewModel

class LoginFragment : Fragment() {
    private lateinit var binding: LoginFragmentBinding
    private val viewModel: LoginViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LoginFragmentBinding.inflate(inflater)

        binding.btnLogin.setOnClickListener {
            login()
        }

        Firebase.auth.currentUser?.let {
            this.findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToHomeFragment())
        }

        addObservers()

        return binding.root
    }

    private fun login() {
        binding.apply {
            viewModel.loginWithEmailAndPassword(
                etEmail.text.toString(),
                etPassword.text.toString()
            )
        }
    }

    private fun addObservers() {
        binding.apply {
            viewModel.progress.observe(viewLifecycleOwner, {
                when (it) {
                    is Progress.Loading -> {
                        btnLogin.isEnabled = false
                        progressBar.isVisible = true
                    }
                    is Progress.Error -> {
                        btnLogin.isEnabled = true
                        progressBar.isVisible = false
                        Toast.makeText(activity,it.error,Toast.LENGTH_LONG).show()
                    }
                    else -> {
                        btnLogin.isEnabled = true
                        progressBar.isVisible = false
                        this@LoginFragment.findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToHomeFragment())
                    }
                }
            })
        }
    }


}