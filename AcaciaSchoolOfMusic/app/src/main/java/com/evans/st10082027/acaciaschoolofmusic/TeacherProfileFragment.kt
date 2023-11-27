package com.evans.st10082027.acaciaschoolofmusic

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase

class TeacherProfileFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var databaseRef: DatabaseReference
    private lateinit var profileImageView: ImageView
    private lateinit var textViewFirstName: TextView
    private lateinit var buttonDeleteProfile: Button
    private lateinit var helpTextView: TextView
    private lateinit var buttonLogout: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_teacher_profile, container, false)

        profileImageView = view.findViewById(R.id.imageViewProfile)
        textViewFirstName = view.findViewById(R.id.textViewFirstName)
        buttonDeleteProfile = view.findViewById(R.id.buttonDeleteProfile)
        helpTextView = view.findViewById(R.id.helpTextView)
        buttonLogout = view.findViewById(R.id.buttonLogout)

        auth = Firebase.auth
        databaseRef = FirebaseDatabase.getInstance().reference.child("teachers")

        val currentUser = auth.currentUser
        currentUser?.let { user ->
            val userId = user.uid
            val userRef = databaseRef.child(userId)
            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val firstName = snapshot.child("firstName").value.toString()
                    val imageUrl = snapshot.child("profilePictureUrl").value.toString()
                    Glide.with(requireContext())
                        .load(imageUrl)
                        .placeholder(R.drawable.baseline_person_24)
                        .error(R.drawable.baseline_person_24)
                        .into(profileImageView)

                    textViewFirstName.text = firstName
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("TeacherProfileFragment", "Database error: ${error.message}")
                }
            })
        }

        buttonLogout.setOnClickListener {
            auth.signOut()
            loadFragment(LoginFragment())
        }

        buttonDeleteProfile.setOnClickListener {
            auth.currentUser?.delete()
            loadFragment(LoginFragment())
        }

        helpTextView.setOnClickListener {
            loadFragment(HelpFragment())
        }

        return view
    }
    private fun loadFragment(fragment: Fragment) {
        val transaction: FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}
