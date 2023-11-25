package com.evans.st10082027.acaciaschoolofmusic

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.evans.st10082027.acaciaschoolofmusic.databinding.FragmentUserRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask

class UserRegisterFragment : Fragment() {

    private lateinit var selectedImageUri: Uri
    private lateinit var profileImageView: ImageView
    private lateinit var uploadImageButton: Button
    private lateinit var registerButton: Button
    private lateinit var editTextUsername: EditText
    private lateinit var editTextFirstName: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var storageReference: StorageReference
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: FragmentUserRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        storageReference = FirebaseStorage.getInstance().reference
        auth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserRegisterBinding.inflate(inflater, container, false)
        (activity as? MainActivity)?.showBottomNavigation(false)

        profileImageView = binding.imageViewProfile
        uploadImageButton = binding.buttonUploadPicture
        registerButton = binding.buttonRegister
        editTextUsername = binding.editTextUsername
        editTextFirstName = binding.editTextFirstName
        editTextPassword = binding.editTextPassword

        uploadImageButton.setOnClickListener {
            selectImage()
        }

        registerButton.setOnClickListener {
            uploadImageAndRegister()
        }

        return binding.root
    }

    private fun selectImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }

    private fun uploadImageAndRegister() {
        if (::selectedImageUri.isInitialized) {
            val imageRef = storageReference.child("profile_pictures/${auth.currentUser?.uid}")
            imageRef.putFile(selectedImageUri)
                .addOnSuccessListener { taskSnapshot: UploadTask.TaskSnapshot ->
                    imageRef.downloadUrl.addOnSuccessListener { uri ->
                        val username = editTextUsername.text.toString()
                        val firstName = editTextFirstName.text.toString()
                        val password = editTextPassword.text.toString()
                        saveUserDetails(username, firstName, password, uri.toString())
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "There was an error creating your account", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun saveUserDetails(username: String, firstName: String, password: String, imageUrl: String) {
        val database = FirebaseDatabase.getInstance()
        auth.createUserWithEmailAndPassword(username, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    val currentUser = auth.currentUser
                    currentUser?.let { user ->
                        val userId = user.uid
                        val userRef = database.reference.child("users").child(userId)
                        userRef.child("username").setValue(username)
                        userRef.child("firstName").setValue(firstName)
                        userRef.child("imageUrl").setValue(imageUrl)

                        loadFragment(LoginFragment())
                    }
                } else {
                    Log.w("UserRegisterFragment", "createUserWithEmail:failure", task.exception)
                    Toast.makeText(requireContext(), "Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun loadFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            selectedImageUri = data.data!!
            profileImageView.setImageURI(selectedImageUri)
        }
    }

    companion object {
        private const val PICK_IMAGE_REQUEST = 101
    }
}
