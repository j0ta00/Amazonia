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

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Login.newInstance] factory method to
 * create an instance of this fragment.
 */
class Login : Fragment(), View.OnClickListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
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
                            Toast.makeText(requireContext(), "Successfully logged in", Toast.LENGTH_SHORT).show()
                            Navigation.findNavController(binding.root).navigate(R.id.galleryFragment)
                        } else {
                            Toast.makeText(requireContext(), "wrong credentials invalid username or password", Toast.LENGTH_SHORT).show()
                        }

                    })
            }
        }catch(e: ApiException){
                Toast.makeText(requireContext(), "Something went wrong during the authentication, please check your internet connection", Toast.LENGTH_SHORT).show()
        }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
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

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Login.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Login().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
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



    private fun sigInWithFacebook() {
        LoginManager.getInstance().logInWithReadPermissions(this, listOf("email"))
        LoginManager.getInstance().registerCallback(callbackManager,
        object:FacebookCallback<LoginResult>{
            override fun onSuccess(result: LoginResult?) {
                result?.let{
                    val credential = FacebookAuthProvider.getCredential(it.accessToken.toString())
                    auth.signInWithCredential(credential).addOnCompleteListener {
                        if (it.isSuccessful) {
                            Toast.makeText(requireContext(),"Successfully logged in",Toast.LENGTH_SHORT).show()
                            Navigation.findNavController(binding.root).navigate(R.id.galleryFragment)
                        } else {
                            Toast.makeText(requireContext(),"wrong credentials invalid username or password",Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            override fun onCancel() {}
            override fun onError(error: FacebookException?) {}
        })
    }

    private fun sigInWithGoogle() {
        val googleConf=GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()
        val googleClient= GoogleSignIn.getClient(requireActivity(),googleConf)
        responseLauncher.launch(googleClient.signInIntent)
        googleClient.signOut()
    }

    private fun signIn() {
        lifecycleScope.launch {
            if (checkFields()) {
                auth.signInWithEmailAndPassword(
                    binding.edEmail.text.toString().trim(),
                    binding.edPassword.text.toString().trim()
                ).addOnCompleteListener(requireActivity(), OnCompleteListener { task->
                    if(task.isSuccessful){
                        Toast.makeText(requireContext(),"Successfully logged in",Toast.LENGTH_SHORT).show()
                        Navigation.findNavController(binding.root).navigate(R.id.galleryFragment)
                    }else{
                        Toast.makeText(requireContext(),"wrong credentials invalid username or password",Toast.LENGTH_SHORT).show()
                    }

                })
            }
        }
    }

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

    private fun register() {
        if (checkFields()) {
            auth.createUserWithEmailAndPassword(
                binding.edEmail.text.toString().trim(),
                binding.edPassword.text.toString().trim()
            ).addOnCompleteListener(requireActivity(), OnCompleteListener { task->
                if(task.isSuccessful){
                    Toast.makeText(requireContext(),"Successfully logged in",Toast.LENGTH_SHORT).show()
                    Navigation.findNavController(binding.root).navigate(R.id.galleryFragment)
                }else{
                    Toast.makeText(requireContext(),"wrong credentials invalid username or password",Toast.LENGTH_SHORT).show()
                }

            })
        }

    }


}












