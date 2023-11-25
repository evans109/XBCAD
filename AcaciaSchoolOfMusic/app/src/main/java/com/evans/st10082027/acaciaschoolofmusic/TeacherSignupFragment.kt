package com.evans.st10082027.acaciaschoolofmusic

// TeacherSignupFragment.kt

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.evans.st10082027.acaciaschoolofmusic.databinding.FragmentTeacherSignupBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask

class TeacherSignupFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private lateinit var storageReference: StorageReference

    lateinit var binding: FragmentTeacherSignupBinding
    lateinit var signupListener: OnCompleteListener<AuthResult>

    private val PICK_IMAGE_REQUEST = 71
    private var filePath: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTeacherSignupBinding.inflate(inflater)
        (activity as? MainActivity)?.showBottomNavigation(false)

        binding.teacherProfilePictureButton.setOnClickListener {
            chooseImage()
        }

        binding.teacherSignupButton.setOnClickListener {
            val email = binding.teacherEmailEditText.text.toString()
            val password = binding.teacherPasswordEditText.text.toString()
            val firstName = binding.teacherFirstNameEditText.text.toString()
            val key = binding.teacherKeyEditText.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty() && firstName.isNotEmpty() && key.isNotEmpty() && filePath != null) {
                if (key == "admin") {
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val userId = auth.currentUser?.uid
                                if (userId != null) {
                                    uploadImage(userId)
                                }
                            } else {
                                Log.w("TeacherSignup", "createUserWithEmail:failure", task.exception)
                                Toast.makeText(context, "Unable to register", Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    Toast.makeText(context, "Invalid teacher key", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(
                    context,
                    "Email, password, first name, key, and profile picture cannot be blank",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        return binding.root
    }

    private fun chooseImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            filePath = data.data
            binding.teacherProfilePicture.setImageURI(filePath)
        }
    }

    private fun uploadImage(userId: String) {
        if (filePath != null) {
            val ref = storageReference.child("teachers/$userId/profilePicture")
            ref.putFile(filePath!!)
                .addOnCompleteListener { task: Task<UploadTask.TaskSnapshot> ->
                    if (task.isSuccessful) {
                        ref.downloadUrl.addOnSuccessListener { uri ->
                            saveTeacherDetails(userId, binding.teacherKeyEditText.text.toString(), binding.teacherFirstNameEditText.text.toString(), uri.toString())
                        }
                    } else {
                        Toast.makeText(context, "Failed to upload image", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun saveTeacherDetails(userId: String, key: String, firstName: String, profilePictureUrl: String) {
        val userRef = Firebase.database.reference.child("teachers").child(userId)
        userRef.apply {
            child("teacherKey").setValue(key)
            child("firstName").setValue(firstName)
            child("profilePictureUrl").setValue(profilePictureUrl)
        }
    }
}

