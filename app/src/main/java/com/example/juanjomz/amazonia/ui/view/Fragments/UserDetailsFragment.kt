package com.example.juanjomz.amazonia.ui.view.Fragments

import android.os.Bundle
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
import androidx.activity.viewModels
import androidx.fragment.app.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.example.juanjomz.amazonia.databinding.FragmentGalleryBinding
import com.example.juanjomz.amazonia.databinding.FragmentPlantListBinding
import com.example.juanjomz.amazonia.ui.viewmodel.ActivityVM
import com.example.juanjomz.amazonia.ui.viewmodel.GalleryVM
import com.example.juanjomz.amazonia.ui.viewmodel.PlantListVM

/**
 * Fragmento detalles del usuario, se centra en las funcionalidades relacionadas con la sesión del usuario, las funciones override o de listener no tendrán documentación ya que vienen comentadas en el padre
 */

class UserDetailsFragment : Fragment(), View.OnClickListener {
    private lateinit var binding: FragmentUserDetailsBinding
    private lateinit var auth: FirebaseAuth
    private val activityViewModel : ActivityVM by activityViewModels()


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
        activityViewModel.refreshImages(true)
        binding.instaUrlButton.setOnClickListener(this)
        binding.twitterUrlButton.setOnClickListener(this)
        binding.btnSave.setOnClickListener(this)
        binding.imgbEdit.setOnClickListener(this)
    }
    /**
     * Propósito: Permite editar la contraseña del usuario
     * */
    private fun editPassword() {
        if (binding.txtUserPassword.visibility == View.VISIBLE) {
            editMode()
        } else {
            returnToNormalMode()
        }
    }
    /**
     * Propósito: Vuelve la vista al modo normal
     * */
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
    /**
     * Propósito: Convierte la vista al modo de edición, permitiendo cambiar la contraseña
     * */
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

    /**
     * Propósito: Guarda la nueva contraseña introducida
     * */
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
            binding.logOutButton.id->{
                activityViewModel.changeShowDialog(false)
                auth.signOut()
                activity?.viewModelStore?.clear()
                findNavController().navigate(R.id.login)}
            else -> startActivity(Intent(Intent.ACTION_VIEW,Uri.parse("https://www.youtube.com/watch?v=B4CcX720DW4")))
        }
    }

}
