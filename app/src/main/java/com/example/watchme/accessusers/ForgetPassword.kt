package com.example.watchme.accessusers

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.watchme.R
import com.example.watchme.databinding.FragmentForgetPasswordBinding
import com.example.watchme.model.AuthResult
import com.example.watchme.viewmodel.AuthViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class ForgetPassword : Fragment() {

    private var _binding: FragmentForgetPasswordBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentForgetPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.resetBtn.setOnClickListener {
            resetPasswordClicked()
        }

        binding.backToLoginTv.setOnClickListener {
            navigateBackToLogin()
        }

        observeAuthState()
    }

    private fun resetPasswordClicked() {
        val email = binding.emailEdt.text.toString().trim()

        if (email.isEmpty()) {
            binding.emailEdt.error = "Enter email"
            binding.emailEdt.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailEdt.error = "Enter a valid email address"
            binding.emailEdt.requestFocus()
            return
        }

        binding.emailEdt.error = null
        viewModel.resetPassword(email)
    }

    private fun observeAuthState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.authState.collect { result ->
                    when (result) {
                        is AuthResult.Loading -> {
                            binding.resetBtn.isEnabled = false
                            binding.resetProgress.isVisible = true
                            binding.resetBtn.text = "Sending..."
                        }

                        is AuthResult.Success -> {
                            binding.resetBtn.isEnabled = true
                            binding.resetProgress.isVisible = false
                            binding.resetBtn.text = "Send Reset Email"
                            Toast.makeText(
                                requireContext(),
                                "Password reset email sent! Check your inbox.",
                                Toast.LENGTH_LONG
                            ).show()
                            viewModel.resetAuthState()
                            navigateBackToLogin()
                        }

                        is AuthResult.Error -> {
                            binding.resetBtn.isEnabled = true
                            binding.resetProgress.isVisible = false
                            binding.resetBtn.text = "Send Reset Email"
                            Snackbar.make(
                                binding.root,
                                result.message,
                                Snackbar.LENGTH_LONG
                            ).show()
                            viewModel.resetAuthState()
                        }

                        null -> {
                            binding.resetBtn.isEnabled = true
                            binding.resetProgress.isVisible = false
                            binding.resetBtn.text = "Send Reset Email"
                        }
                    }
                }
            }
        }
    }

    private fun navigateBackToLogin() {
        if (!findNavController().popBackStack()) {
            findNavController().navigate(R.id.action_forgetPassword_to_login)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
