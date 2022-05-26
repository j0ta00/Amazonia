package com.example.juanjomz.amazonia

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
import com.google.android.material.dialog.MaterialAlertDialogBuilder


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
    private  var resultSelected = 0
    private lateinit var binding: FragmentPlantIdentificationBinding
    private val organs = listOf("flower","leaf","fruit")
    private val viewModel : PlantIdentificationVM by viewModels()
    private val responseLauncher=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ activityResult->
        if(activityResult.resultCode==Activity.RESULT_OK) {
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
        binding= FragmentPlantIdentificationBinding.inflate(inflater,container,false)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            checkExternalStoragePermission();
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
        binding.spnOrgan.adapter=
            context?.let { ArrayAdapter(it,R.layout.spinner_item,organs) }

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

    private fun setupVMObservers(){
        viewModel.plant.observe(viewLifecycleOwner){
            searchImage()
        }
        viewModel.image.observe(viewLifecycleOwner){
                showDialogPlantIdentificated()
        }

    }
        private fun makePhoto(){
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
                binding.btnDetect.id->identifyPlant()
                binding.btnRetry.id->makePhoto()
                R.id.btnNext->{if(resultSelected< viewModel.plant.value!!.size){
                    resultSelected++
                    searchImage()
                }
                }
                R.id.btnPrevious->{if(resultSelected>0){
                    resultSelected--
                    searchImage()
                }
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

        }
    }
    fun searchImage(){
        if(viewModel.plant.value?.get(resultSelected)?.commonName!="null"){
        viewModel.plant.value?.get(resultSelected)?.commonName?.let { viewModel.searchImage(it) }
        }else{
            viewModel.plant.value?.get(resultSelected)?.scientificName?.let { viewModel.searchImage(it) }
        }
    }
    fun showDialogPlantIdentificated(){
        val dialogLayout=layoutInflater.inflate(R.layout.plant_identificated_dialog,null)
        fillDialogView(dialogLayout)
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("This plant could be:").setView(dialogLayout)
            .setNeutralButton(resources.getString(R.string.cancel)) { dialog, which ->
                // Respond to neutral button press
            }
            .setPositiveButton(resources.getString(R.string.accept)) { dialog, which ->
                // Respond to positive button press
            }.show()
    }

    private fun fillDialogView(dialogLayout:View){
        if(viewModel.plant.value?.get(resultSelected)?.commonName=="null"){
            dialogLayout.findViewById<TextView>(R.id.txtPlantIdentificated).text=viewModel.plant.value?.get(resultSelected)?.scientificName
        }else{
            dialogLayout.findViewById<TextView>(R.id.txtPlantIdentificated).text=viewModel.plant.value?.get(resultSelected)?.commonName
        }
        Glide.with(requireContext())
            .load(viewModel.image.value?.get(1))
            .into(dialogLayout.findViewById(R.id.imagePlantIdentificated1))
        Glide.with(requireContext())
            .load(viewModel.image.value?.get(2))
            .into(dialogLayout.findViewById(R.id.imagePlantIdentificated2))
        Glide.with(requireContext())
            .load(viewModel.image.value?.get(3))
            .into(dialogLayout.findViewById(R.id.imagePlantIdentificated3))
        Glide.with(requireContext())
            .load(viewModel.image.value?.get(4))
            .into(dialogLayout.findViewById(R.id.imagePlantIdentificated4))
        dialogLayout.findViewById<ImageButton>(R.id.btnNext).setOnClickListener(this)
        dialogLayout.findViewById<ImageButton>(R.id.btnPrevious).setOnClickListener(this)
    }
}