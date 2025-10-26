package com.vtol.quizbattleapp.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.vtol.quizbattleapp.viewmodel.LoginViewModel
import com.vtol.quizbattleapp.R
import com.vtol.quizbattleapp.util.Resource
import com.vtol.quizbattleapp.databinding.FragmentLoginBinding
import com.vtol.quizbattleapp.model.Player
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment: Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private val loginViewModel by viewModels<LoginViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.apply {
            signUpBtn.setOnClickListener { showToastMessage() }
            signInBtn.setOnClickListener { showToastMessage()}
            guestBtn.setOnClickListener {
                setUpDialog(requireContext())
            }
        }

        val loadingDialog = showLoadingDialog()

        lifecycleScope.launch {
            loginViewModel.questLogin.collect {
                when(it) {
                    is Resource.Loading -> loadingDialog.show()

                    is Resource.Success -> {
                        loadingDialog.dismiss()
                        findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                    }

                    is Resource.Error -> {
                        loadingDialog.dismiss()
                        Toast.makeText(requireContext(),"Failed to sign in as a quest",Toast.LENGTH_SHORT).show()

                    }
                    else -> Unit
                }
            }
        }
    }

    private fun setUpDialog(context: Context) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_enter_name, null)
        val nameEditText = dialogView.findViewById<EditText>(R.id.etName)

        val dialog = AlertDialog.Builder(context)
            .setTitle("Continue as guest")
            .setView(dialogView)
            .setPositiveButton("Continue") { _, _ ->
                val name = nameEditText.text.toString().trim()
                if (name.isNotEmpty()) {
                    loginViewModel.continueAsQuest(player = Player(playerName = name))
                } else {
                    Toast.makeText(requireContext(),"Please Enter your name",Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()

    }

    private fun showToastMessage(){
        Toast.makeText(requireContext(),"This feature is not available", Toast.LENGTH_SHORT).show()
    }

    private fun showLoadingDialog(): AlertDialog {
        val progress = ProgressBar(requireContext())
        return AlertDialog.Builder(requireContext())
            .setView(progress)
            .setCancelable(false)
            .create()
    }

}