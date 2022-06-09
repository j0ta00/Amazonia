package com.example.juanjomz.amazonia.ui.view.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.juanjomz.amazonia.R
import com.example.juanjomz.amazonia.databinding.FragmentUserDetailsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.annotation.NonNull
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import android.content.Intent.getIntent
import android.system.Os.remove
import android.util.Log
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.findFragment
import com.example.juanjomz.amazonia.databinding.FragmentGalleryBinding
import com.example.juanjomz.amazonia.databinding.FragmentPlantListBinding


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [UserDetailsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UserDetailsFragment : Fragment(), View.OnClickListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentUserDetailsBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentUserDetailsBinding.inflate(inflater, container, false)
        Firebase.initialize(context!!)
        auth = Firebase.auth
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.txtUserEmail.text = auth.currentUser?.email
        binding.txtUserPassword.text = getString(R.string.passwordAsteriks)
        binding.facebookUrlButton.setOnClickListener(this)
        binding.logOutButton.setOnClickListener(this)
        binding.instaUrlButton.setOnClickListener(this)
        binding.twitterUrlButton.setOnClickListener(this)
        binding.btnSave.setOnClickListener(this)
        binding.imgbEdit.setOnClickListener(this)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment UserDetailsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            UserDetailsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun editPassword() {
        if (binding.txtUserPassword.visibility == View.VISIBLE) {
            editMode()
        } else {
            returnToNormalMode()
        }
    }
    private fun returnToNormalMode(){
        binding.txtUserPassword.visibility = View.VISIBLE
        binding.edUserPassword.visibility = View.GONE
        binding.btnSave.visibility = View.GONE
        binding.logOutButton.visibility = View.VISIBLE
        binding.imgbEdit.setImageDrawable(
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.ic_baseline_edit,
                null
            )
        )
    }
    private fun editMode(){
        binding.txtUserPassword.visibility = View.GONE
        binding.edUserPassword.visibility = View.VISIBLE
        binding.logOutButton.visibility = View.GONE
        binding.btnSave.visibility = View.VISIBLE
        binding.imgbEdit.setImageDrawable(
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.ic_baseline_cancel_24, null
            )
        )
    }


    private fun saveNewPassword() {
        if (binding.edUserPassword.text.isNotEmpty() && binding.edUserPassword.text.trim()
                .matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+\$).{8,}".toRegex())//debido a problemas con las cadenas no he podido hacer la cadena regex un recurso ya que si no, no funcionaba ya que convertía varios caracteres en caracteres especiales
        ) {
            auth.currentUser?.updatePassword(binding.edUserPassword.text.toString().trim())
                ?.addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.passwordModifySuccess),
                            Toast.LENGTH_SHORT
                        ).show()
                        returnToNormalMode()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.passwordModifyError),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        } else {
            binding.edUserPassword.error = getString(R.string.passwordFormatError)
        }
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            binding.imgbEdit.id -> editPassword()
            binding.btnSave.id -> saveNewPassword()
            binding.logOutButton.id->{auth.signOut()
                activity?.viewModelStore?.clear()
                findNavController().navigate(R.id.login)}
            else -> startActivity(Intent(Intent.ACTION_VIEW,Uri.parse("https://www.youtube.com/watch?v=B4CcX720DW4")))
        }
    }

}
