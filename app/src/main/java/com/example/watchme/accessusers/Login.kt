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
import com.example.watchme.databinding.FragmentLoginBinding
import com.example.watchme.model.AuthResult
import com.example.watchme.viewmodel.AuthViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class Login : Fragment() {
    private val viewModel: AuthViewModel by viewModels()
    lateinit var binding: FragmentLoginBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(layoutInflater)
        binding.loginBtn.setOnClickListener {
            lifecycleScope.launch(Dispatchers.Main) {
                loginButtonClicked()
            }
        }
        binding.signupBtn.setOnClickListener {
            lifecycleScope.launch (Dispatchers.Main){
                signupButtonClicked()
            }
        }
        binding.forgetPasswordTv.setOnClickListener {
            lifecycleScope.launch(Dispatchers.Main) {
                forgetPasswordClicked()
            }
        }
        return binding.root
    }
    private fun loginButtonClicked(){
        val email = binding.emailEdt.text.toString().trim()
        val password = binding.passwordEdt.text.toString()

        if (email.isEmpty()) {
            binding.emailEdt.error = "Enter email"
        }
        if (password.isEmpty()) {
            binding.passwordEdt.error = "Enter password"
        }
        if (!password.isEmpty()&&!email.isEmpty()){
            viewModel.login(
                email,
                password
            )
            observeAuthState()
        }
    }
    private fun observeAuthState() {

        lifecycleScope.launch(Dispatchers.Main) {

            viewModel.authState.collect { result ->

                when (result) {

                    is AuthResult.Loading -> {
                        binding.loginBtn.isEnabled = false
                        binding.loginBtn.text = "Logging in..."

                    }

                    is AuthResult.Success -> {
                        binding.loginBtn.isEnabled = true
                        binding.loginBtn.text = "Login"
                        Toast.makeText(requireContext(), "Log in Successful", Toast.LENGTH_SHORT).show()
                        findNavController().navigate(R.id.action_login_to_homePage)
                    }

                    is AuthResult.Error -> {
                        binding.loginBtn.isEnabled = true
                        binding.loginBtn.text = "Log in"
                        Snackbar.make(binding.loginBtn,"Log in failed",Snackbar.LENGTH_SHORT).show()
                    }
                    null -> Unit
                }
            }
        }
    }

    private fun signupButtonClicked(){
        findNavController().navigate(R.id.action_login_to_signup)
    }
    private fun forgetPasswordClicked(){
        findNavController().navigate(R.id.action_login_to_forgetPassword)
    }

}