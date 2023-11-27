package com.evans.st10082027.acaciaschoolofmusic

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthResult
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

class MainActivity : AppCompatActivity(), OnCompleteListener<AuthResult> {

    private val login = LoginFragment()
    private val teacher = TeacherFragment()
    private val viewRequest = ViewRequestFragment()
    private val userFrag = UserFragment()
    private val myRequest = MyRequestsFragment()
    var username: String? = null

    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        FirebaseApp.initializeApp(this)

        bottomNavigationView = findViewById(R.id.bottomNavigationView)

        val currentUser = Firebase.auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            val userRef = Firebase.database.reference.child("teachers").child(userId)
            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        loadFragment(teacher)
                    } else {
                        loadFragment(userFrag)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(this@MainActivity, "Failed to retrieve user information", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            loadFragment(login)
        }

        supportFragmentManager.addFragmentOnAttachListener { fragmentManager, fragment ->
            if (fragment is LoginFragment) {
                fragment.loginListener = this
            }
        }

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.entries -> {
                    checkUserRole { isTeacher ->
                        if (isTeacher) {
                            loadFragment(teacher)
                        } else {
                            loadFragment(userFrag)
                        }
                    }
                }
                R.id.main -> {
                    checkUserRole { isTeacher ->
                        if (isTeacher) {
                            loadFragment(viewRequest)
                        } else {
                            loadFragment(myRequest)
                        }
                    }
                }
            }
            true
        }
    }

    override fun onComplete(task: Task<AuthResult>) {
        if (task.isSuccessful) {
            username = task.result?.user?.email
            Toast.makeText(this, "You have signed in $username", Toast.LENGTH_SHORT).show()

            val user = Firebase.auth.currentUser
            if (user != null) {
                val userId = user.uid

                val userRef = Firebase.database.reference.child("teachers").child(userId)
                userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            loadFragment(teacher)
                        } else {
                            loadFragment(userFrag)
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        // Handle the error
                        Toast.makeText(this@MainActivity, "Failed to retrieve user information", Toast.LENGTH_SHORT).show()
                    }
                })
            } else {
                // User not authenticated, handle accordingly
                Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Something happened when logging in", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkUserRole(callback: (Boolean) -> Unit) {
        val user = Firebase.auth.currentUser
        if (user != null) {
            val userId = user.uid
            val userRef = Firebase.database.reference.child("teachers").child(userId)
            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val isTeacher = dataSnapshot.exists()
                    callback(isTeacher)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle the error
                    callback(false)
                }
            })
        } else {
            callback(false)
        }
    }



    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    fun showBottomNavigation(show: Boolean) {
        if (show) {
            bottomNavigationView.visibility = View.VISIBLE
        } else {
            bottomNavigationView.visibility = View.GONE
        }
    }

}
