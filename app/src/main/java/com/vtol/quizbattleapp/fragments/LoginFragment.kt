package com.vtol.quizbattleapp.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.vtol.quizbattleapp.LoginViewModel
import com.vtol.quizbattleapp.R
import com.vtol.quizbattleapp.databinding.FragmentLoginBinding
import com.vtol.quizbattleapp.model.Player

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
                    findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                } else {

                }
            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()

    }

    private fun showToastMessage(){
        Toast.makeText(requireContext(),"This feature is not available", Toast.LENGTH_SHORT).show()
    }

}