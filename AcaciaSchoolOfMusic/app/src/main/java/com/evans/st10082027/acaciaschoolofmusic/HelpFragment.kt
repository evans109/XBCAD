package com.evans.st10082027.acaciaschoolofmusic

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.evans.st10082027.acaciaschoolofmusic.databinding.FragmentHelpBinding

class HelpFragment : Fragment() {

    private lateinit var binding: FragmentHelpBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHelpBinding.inflate(inflater)

        binding.textViewContactOwner.setOnClickListener {
            val email = "Cheslinfortes9@gmail.com"
            composeEmail(email)
        }

        binding.textViewContactDeveloper.setOnClickListener {
            val email = "Pillayevan27@gmail.com"
            composeEmail(email)
        }

        return binding.root
    }

    private fun composeEmail(email: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            putExtra(Intent.EXTRA_SUBJECT, "")
            putExtra(Intent.EXTRA_TEXT, "")
        }
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(Intent.createChooser(intent, "Send Email"))
        }
    }
}
