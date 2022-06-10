package com.example.juanjomz.amazonia.ui.view.Fragments

import android.app.Activity.RESULT_OK
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.juanjomz.amazonia.R
import com.example.juanjomz.amazonia.databinding.FragmentLoginBinding
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import kotlinx.coroutines.*

/**
 * Fragmento de login el cual el cual realiza las funcionalidades de login y registro, las funciones override o de listener no tendrán documentación ya que vienen comentadas en el padre
 */
class Login : Fragment(), View.OnClickListener {
    private lateinit var binding: FragmentLoginBinding
    private lateinit var auth: FirebaseAuth
    var callbackManager = CallbackManager.Factory.create()
    private val responseLauncher=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ activityResult->
        callbackManager.onActivityResult(activityResult.describeContents(), activityResult.resultCode, activityResult.data)
        if(activityResult.resultCode==RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(activityResult.data)
            try{
            val account = task.getResult(ApiException::class.java)
            if (account != null) {
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                Firebase.auth.signInWithCredential(credential)
                    .addOnCompleteListener(requireActivity(), OnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(requireContext(), getString(R.string.logged_in), Toast.LENGTH_SHORT).show()
                            Navigation.findNavController(binding.root).navigate(R.id.galleryFragment)
                        } else {
                            Toast.makeText(requireContext(), getString(R.string.wrong_credentials), Toast.LENGTH_SHORT).show()
                        }

                    })
            }
        }catch(e: ApiException){
                Toast.makeText(requireContext(), getString(R.string.showConnectionError), Toast.LENGTH_SHORT).show()
        }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        Firebase.initialize(context!!)
        activity?.actionBar?.hide()
        auth = Firebase.auth
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        parentFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        activity?.findViewById<BottomNavigationView>(R.id.navigationMenu)?.visibility=View.INVISIBLE
        if(!auth.currentUser?.email.isNullOrEmpty()){
            Navigation.findNavController(binding.root).navigate(R.id.galleryFragment)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.logBtn.setOnClickListener(this)
        binding.regBtn.setOnClickListener(this)
        binding.googleButton.setOnClickListener(this)
        binding.facebookButton.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        if (p0 != null) {
            when (p0.id) {
                binding.logBtn.id -> signIn()
                binding.regBtn.id -> register()
                binding.googleButton.id->sigInWithGoogle()
                binding.facebookButton.id->sigInWithFacebook()
            }
        }
    }


    /**
     * Propósito: permita loguearse con facebook
     * */
    private fun sigInWithFacebook() {
        LoginManager.getInstance().logInWithReadPermissions(this, listOf("email"))
        LoginManager.getInstance().registerCallback(callbackManager,
        object:FacebookCallback<LoginResult>{
            override fun onSuccess(result: LoginResult?) {
                result?.let{
                    val credential = FacebookAuthProvider.getCredential(it.accessToken.toString())
                    auth.signInWithCredential(credential).addOnCompleteListener {
                        if (it.isSuccessful) {
                            Toast.makeText(requireContext(),getString(R.string.logged_in),Toast.LENGTH_SHORT).show()
                            Navigation.findNavController(binding.root).navigate(R.id.galleryFragment)
                        } else {
                            Toast.makeText(requireContext(),getString(R.string.wrong_credentials),Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            override fun onCancel() {}
            override fun onError(error: FacebookException?) {}
        })
    }
    /**
     * Propósito: permita loguearse con google
     * */
    private fun sigInWithGoogle() {
        val googleConf=GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()
        val googleClient= GoogleSignIn.getClient(requireActivity(),googleConf)
        responseLauncher.launch(googleClient.signInIntent)
        googleClient.signOut()
    }
    /**
     * Propósito: permita loguearse en firebase
     * */
    private fun signIn() {
        lifecycleScope.launch {
            if (checkFields()) {
                auth.signInWithEmailAndPassword(
                    binding.edEmail.text.toString().trim(),
                    binding.edPassword.text.toString().trim()
                ).addOnCompleteListener(requireActivity(), OnCompleteListener { task->
                    if(task.isSuccessful){
                        Toast.makeText(requireContext(),getString(R.string.logged_in),Toast.LENGTH_SHORT).show()
                        Navigation.findNavController(binding.root).navigate(R.id.galleryFragment)
                    }else{
                        Toast.makeText(requireContext(),getString(R.string.wrong_credentials),Toast.LENGTH_SHORT).show()
                    }

                })
            }
        }
    }
    /**
     * Propósito: permita loguearse con facebook
     * @return arevalids:Boolean if fields are valids
     * */
    private fun checkFields(): Boolean {
        var areValids = true
        if (binding.edEmail.text.toString().isNullOrEmpty()) {
            binding.edEmail.error = getString(R.string.emptyField)
            areValids = false
        }
        if (binding.edPassword.text.toString().isNullOrEmpty()) {
            binding.edPassword.error = getString(R.string.emptyField)
            areValids = false
        } else if (!binding.edPassword.text.toString().trim()
                .matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+\$).{8,}".toRegex())//debido a problemas con las cadenas no he podido hacer la cadena regex un recurso ya que si no, no funcionaba ya que convertía varios caracteres en caracteres especiales
        ) {
            binding.edPassword.error = getString(R.string.passwordFormatError)
            areValids = false
        }
        return areValids
    }
    /**
     * Propósito: permite regidtrarse en firebase
     * */
    private fun register() {
        if (checkFields()) {
            auth.createUserWithEmailAndPassword(
                binding.edEmail.text.toString().trim(),
                binding.edPassword.text.toString().trim()
            ).addOnCompleteListener(requireActivity(), OnCompleteListener { task->
                if(task.isSuccessful){
                    Toast.makeText(requireContext(),getString(R.string.logged_in),Toast.LENGTH_SHORT).show()
                    Navigation.findNavController(binding.root).navigate(R.id.galleryFragment)
                }else{
                    Toast.makeText(requireContext(),getString(R.string.wrong_credentials),Toast.LENGTH_SHORT).show()
                }

            })
        }

    }


}












