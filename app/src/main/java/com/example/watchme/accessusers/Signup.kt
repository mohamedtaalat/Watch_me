package com.example.watchme.accessusers

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.watchme.R
import com.example.watchme.databinding.FragmentSignupBinding
import com.example.watchme.model.AuthResult
import com.example.watchme.viewmodel.AuthViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch


class Signup : Fragment() {

    lateinit var binding: FragmentSignupBinding
    val viewModel: AuthViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignupBinding.inflate(layoutInflater)
        binding.sloginBtn.setOnClickListener {
            lifecycleScope.launch {
                loginButtonClicked()
            }
        }
        binding.ssignupBtn.setOnClickListener {
            lifecycleScope.launch {
                signupButtonClicked()
                observeAuthState()
            }
        }
        return binding.root
    }
    private fun loginButtonClicked(){
        findNavController().navigate(R.id.action_signup_to_login)
    }
    private fun signupButtonClicked(){
        val email = binding.emailEdt.text.toString().trim()
        val password = binding.passwordEdt.text.toString()

        if (email.isEmpty()) {
            binding.emailEdt.error = "Enter email"
        }
        if (password.isEmpty()) {
            binding.passwordEdt.error = "Enter password"
        }
        if (!password.isEmpty()&&!email.isEmpty()){
            viewModel.signup(
                email,
                password
            )
        }
    }
    private fun observeAuthState() {

        lifecycleScope.launch {

            viewModel.authState.collect { result ->

                when (result) {

                    is AuthResult.Loading -> {
                        binding.ssignupBtn.isEnabled = false
                        binding.ssignupBtn.text = "Signing up..."
                        findNavController().navigate(R.id.action_login_to_homePage)
                    }

                    is AuthResult.Success -> {
                        binding.ssignupBtn.isEnabled = true
                        binding.ssignupBtn.text = "Sign up"
                        Toast.makeText(requireContext(), "Sign up Successful", Toast.LENGTH_SHORT).show()
                    }

                    is AuthResult.Error -> {
                        binding.ssignupBtn.isEnabled = true
                        binding.ssignupBtn.text = "Log in"
                        Snackbar.make(binding.ssignupBtn,"Sign up failed",Snackbar.LENGTH_SHORT).show()
                    }
                    null -> Unit
                }
            }
        }
    }

}