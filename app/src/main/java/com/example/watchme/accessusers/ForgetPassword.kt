package com.example.watchme.accessusers

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.watchme.R
import com.example.watchme.model.AuthResult
import com.example.watchme.viewmodel.AuthViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ForgetPassword : Fragment() {
    
    private val viewModel: AuthViewModel by viewModels()
    private lateinit var emailEdt: EditText
    private lateinit var resetBtn: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_forget_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        emailEdt = view.findViewById(R.id.email_edt)
        resetBtn = view.findViewById(R.id.reset_btn)
        
        resetBtn.setOnClickListener {
            resetPasswordClicked()
        }
    }

    private fun resetPasswordClicked() {
        val email = emailEdt.text.toString().trim()

        if (email.isEmpty()) {
            emailEdt.error = "Enter email"
            return
        }
        
        viewModel.resetPassword(email)
        //observeAuthState()
    }

//    private fun observeAuthState() {
//        lifecycleScope.launch(Dispatchers.Main) {
//            viewModel.authState.collect { result ->
//                when (result) {
//                    is AuthResult.Loading -> {
//                        resetBtn.isEnabled = false
//                        resetBtn.text = "Sending..."
//                    }
//                    is AuthResult.Success -> {
//                        resetBtn.isEnabled = true
//                        resetBtn.text = "Send Email"
//                        Toast.makeText(requireContext(), "Password reset email sent", Toast.LENGTH_SHORT).show()
//                        findNavController().navigateUp()
//                    }
//                    is AuthResult.Error -> {
//                        resetBtn.isEnabled = true
//                        resetBtn.text = "Send Email"
//                        Snackbar.make(resetBtn, "Failed to send reset email", Snackbar.LENGTH_SHORT).show()
//                    }
//                    null -> Unit
//                }
//            }
//        }
//    }
}
