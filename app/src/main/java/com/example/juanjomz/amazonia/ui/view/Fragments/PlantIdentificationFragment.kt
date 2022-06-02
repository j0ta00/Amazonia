package com.example.juanjomz.amazonia.ui.view.Fragments

import android.Manifest

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import com.example.juanjomz.amazonia.databinding.FragmentPlantIdentificationBinding
import com.example.juanjomz.amazonia.ui.viewmodel.PlantIdentificationVM
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.*

import android.os.Environment
import androidx.core.content.contentValuesOf
import java.util.*
import androidx.core.app.ActivityCompat

import android.content.pm.PackageManager
import android.os.Build
import android.provider.DocumentsContract
import android.util.Log

import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import android.graphics.drawable.BitmapDrawable
import android.widget.*
import androidx.fragment.app.activityViewModels
import androidx.navigation.*
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.juanjomz.amazonia.R
import com.example.juanjomz.amazonia.databinding.PlantIdentificatedDialogBinding
import com.example.juanjomz.amazonia.domain.PlantBO
import com.example.juanjomz.amazonia.ui.viewmodel.ActivityVM
import com.example.juanjomz.amazonia.ui.viewmodel.GalleryVM
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PlantIdentification.newInstance] factory method to
 * create an instance of this fragment.
 */
class PlantIdentification : Fragment(), View.OnClickListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var showdialog=true
    private var specieList: List<PlantBO>? = null
    private var imagesList: List<String>? = null
    private val activityViewModel : ActivityVM by activityViewModels()
    private var bindingDialog: PlantIdentificatedDialogBinding? = null
    private lateinit var auth: FirebaseAuth
    private var dialog: androidx.appcompat.app.AlertDialog? = null
    private var resultSelected = 0
    private lateinit var binding: FragmentPlantIdentificationBinding
    private val organs = listOf("flower", "leaf", "fruit")
    private val viewModel: PlantIdentificationVM by viewModels()
    private val responseLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
            if (activityResult.resultCode == Activity.RESULT_OK) {
                binding.image.tag = "new_image"
                val imageBitmap = activityResult.data?.extras?.get("data") as Bitmap
                binding.image.setImageBitmap(imageBitmap)

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
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentPlantIdentificationBinding.inflate(inflater, container, false)
        Firebase.initialize(context!!)
        activity?.actionBar?.hide()
        auth = Firebase.auth
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkExternalStoragePermission()
        }
        makePhoto()
        return binding.root
    }

    private fun checkExternalStoragePermission() {
        val permissionCheck = ContextCompat.checkSelfPermission(
            requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            Log.i("Mensaje", "No se tiene permiso para leer.")
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                225
            )
        } else {
            Log.i("Mensaje", "Se tiene permiso para leer!")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupVMObservers()
        binding.btnDetect.setOnClickListener(this)
        binding.btnRetry.setOnClickListener(this)
        binding.image.setImageResource(R.drawable.takeaphoto)
        binding.image.tag = "default_image"
        binding.spnOrgan.adapter =context?.let { ArrayAdapter(it, R.layout.spinner_item, organs) }

    }


    private fun getRealPathFromUri(context: Context, uri: Uri): String {
        var realPath = String()
        uri.path?.let { path ->
            val databaseUri: Uri
            val selection: String?
            val selectionArgs: Array<String>?
            if (path.contains("/document/image:")) {
                databaseUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                selection = "_id=?"
                selectionArgs = arrayOf(DocumentsContract.getDocumentId(uri).split(":")[1])
            } else {
                databaseUri = uri
                selection = null
                selectionArgs = null
            }
            try {
                val column = "_data"
                val projection = arrayOf(column)
                val cursor = context.contentResolver.query(
                    databaseUri,
                    projection,
                    selection,
                    selectionArgs,
                    null
                )
                cursor?.let {
                    if (it.moveToFirst()) {
                        val columnIndex = cursor.getColumnIndexOrThrow(column)
                        realPath = cursor.getString(columnIndex)
                    }
                    cursor.close()
                }
            } catch (e: Exception) {
                println(e)
            }
        }
        return realPath
    }

    private fun setupVMObservers() {
        viewModel.plant.observe(viewLifecycleOwner) {
            specieList = it
            searchImage()

        }
        activityViewModel.showDialog.observe(viewLifecycleOwner){
            showdialog=it
        }
        viewModel.image.observe(viewLifecycleOwner) {
            imagesList = it
            binding.progressBar.visibility=View.GONE
            if(showdialog) {
                showDialogPlantIdentificated()
            }
        }
        viewModel.specieAdded.observe(viewLifecycleOwner) {
            activityViewModel.refreshSpecies(true)
            showResultSpecieAdded(it)
        }

    }

    private fun showResultSpecieAdded(result: Boolean) {
        if (result) {
            Toast.makeText(requireContext(), "Specie added correctly!!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(),
                "Something went wrong, check your internet connection",
                Toast.LENGTH_SHORT).show()
        }

    }

    private fun makePhoto() {
        responseLauncher.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE))
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PlantIdentification.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PlantIdentification().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onClick(p0: View?) {
        if (p0 != null) {
            when (p0.id) {
                binding.btnDetect.id ->{ identifyPlant()
                    activityViewModel.changeShowDialog(true)
                    binding.progressBar.visibility=View.VISIBLE}
                binding.btnRetry.id -> makePhoto()
                bindingDialog?.btnNext?.id -> {
                    if (resultSelected < specieList?.size!!) {
                        resultSelected++
                        searchImage()
                    }
                }
                bindingDialog?.btnPrevious?.id -> {
                    if (resultSelected > 0) {
                        resultSelected--
                        searchImage()
                    }
                }
                bindingDialog?.imagePlantIdentificated1?.id->{

                }
                bindingDialog?.imagePlantIdentificated2?.id->{

                }
                bindingDialog?.imagePlantIdentificated3?.id->{

                }
                bindingDialog?.imagePlantIdentificated4?.id->{

                }
            }
        }
    }

    fun identifyPlant() {
        if (binding.image.tag != "default_image") {
            val outputStream: OutputStream
            var imageUri: Uri? = null
            try {
                val resolver = context?.getContentResolver()
                val values = contentValuesOf().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME,
                        "Image_" + System.currentTimeMillis() + ".jpeg")
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH,
                        Environment.DIRECTORY_PICTURES + File.separator + getString(R.string.folderPath))
                }
                imageUri = resolver?.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                if (resolver != null) {
                    outputStream = Objects.requireNonNull(imageUri)?.let {
                        resolver.openOutputStream(it)
                    }!!
                    (binding.image.drawable as BitmapDrawable).bitmap.compress(Bitmap.CompressFormat.JPEG,
                        100,
                        outputStream)
                    Objects.requireNonNull(outputStream)

                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
            if (imageUri != null) {
                val file = File(getRealPathFromUri(requireContext(), imageUri))
                file.mkdir()
                val builder = MultipartBody.Builder()
                builder.addFormDataPart("images",
                    file.name,
                    RequestBody.create(MediaType.parse(getRealPathFromUri(requireContext(),
                        imageUri)), file))
                builder.addFormDataPart("organs", "leaf")
                builder.setType(MultipartBody.FORM)
                var requestBody = builder.build()
                viewModel.loadPlant(requestBody)
            }
            activityViewModel.refreshImages(true)
        }
    }

    fun searchImage() {
        if (specieList?.get(resultSelected)?.commonName != "null") {
            specieList?.get(resultSelected)?.commonName?.let { viewModel.searchImage(it) }
        } else {
            specieList!![resultSelected].scientificName.let { viewModel.searchImage(it) }
        }
    }

    fun showDialogPlantIdentificated() {
        bindingDialog=PlantIdentificatedDialogBinding.inflate(LayoutInflater.from(requireContext()))
        fillDialogView()
        activityViewModel.changeShowDialog(false)
         if (dialog != null) {
            dialog!!.dismiss()
        }
        dialog = createDialog()
    }

    private fun createDialog(): androidx.appcompat.app.AlertDialog {
        return MaterialAlertDialogBuilder(requireContext())
            .setTitle("This plant could be:").setView(bindingDialog?.root)
            .setNeutralButton(resources.getString(R.string.cancel)) { dialog, which ->
                dialog.dismiss()
            }
            .setPositiveButton(resources.getString(R.string.accept)) { dialog, which ->
                specieList?.get(resultSelected)?.let {
                    auth.currentUser?.email?.let { it2 ->
                        viewModel.addPlant(it2,
                            it)
                    }
                }
                dialog.dismiss()
            }.show()
    }

    private fun fillDialogView() {
        if (specieList?.get(resultSelected)?.commonName == "null") {
            bindingDialog?.txtPlantIdentificated?.text =
                specieList?.get(resultSelected)?.scientificName
        } else {
            bindingDialog?.txtPlantIdentificated?.text = specieList?.get(resultSelected)?.commonName
        }
        bindingDialog?.imagePlantIdentificated1?.setOnClickListener(this)
        bindingDialog?.imagePlantIdentificated2?.setOnClickListener(this)
        bindingDialog?.imagePlantIdentificated3?.setOnClickListener(this)
        bindingDialog?.imagePlantIdentificated4?.setOnClickListener(this)
        bindingDialog?.let {
            Glide.with(requireContext())
                .load(imagesList?.get(1))
                .into(it.imagePlantIdentificated1)
        }
        bindingDialog?.imagePlantIdentificated2?.let {
            Glide.with(requireContext())
                .load(imagesList?.get(2))
                .into(it)
        }
        bindingDialog?.imagePlantIdentificated3?.let {
            Glide.with(requireContext())
                .load(imagesList?.get(3))
                .into(it)
        }
        bindingDialog?.imagePlantIdentificated4?.let {
            Glide.with(requireContext())
                .load(imagesList?.get(4))
                .into(it)
        }
        bindingDialog?.btnNext?.setOnClickListener(this)
        bindingDialog?.btnPrevious?.setOnClickListener(this)
    }
}