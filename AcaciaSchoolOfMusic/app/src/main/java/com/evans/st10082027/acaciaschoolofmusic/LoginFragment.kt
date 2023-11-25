package com.evans.st10082027.acaciaschoolofmusic

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentTransaction
import com.evans.st10082027.acaciaschoolofmusic.databinding.FragmentLoginBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class LoginFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    lateinit var binding: FragmentLoginBinding
    lateinit var loginListener: OnCompleteListener<AuthResult>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater)
        (activity as? MainActivity)?.showBottomNavigation(false)

        binding.teacherButton.setOnClickListener {
            loadFragment(TeacherSignupFragment())
        }

        binding.registerButton.setOnClickListener {
            loadFragment(UserRegisterFragment())
        }

        binding.loginButton.setOnClickListener {
            val email = binding.userNameEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            if (email.isNotEmpty() && password.isNotEmpty()) {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(loginListener)
            } else {
                Toast.makeText(context, "Username and password cannot" +
                        "be blank",
                    Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }

    private fun loadFragment(fragment: Fragment) {
        val transaction: FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

}