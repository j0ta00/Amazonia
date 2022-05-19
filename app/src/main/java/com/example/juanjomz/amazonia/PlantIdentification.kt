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

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PlantIdentification.newInstance] factory method to
 * create an instance of this fragment.
 */
class PlantIdentification : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentPlantIdentificationBinding
    private val viewModel : PlantIdentificationVM by viewModels()
    private val responseLauncher=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ activityResult->
        if(activityResult.resultCode==Activity.RESULT_OK) {
            val imageBitmap = activityResult.data?.extras?.get("data") as Bitmap
            val outputStream : OutputStream
            var imageUri: Uri?=null
            try {
                val resolver = context?.getContentResolver()
                val values= contentValuesOf().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME,"Image_"+System.currentTimeMillis()+".jpeg")
                    put(MediaStore.MediaColumns.MIME_TYPE,"image/jpeg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH,Environment.DIRECTORY_PICTURES+File.separator+getString(R.string.folderPath))
                }
                 imageUri=resolver?.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values)
                if (resolver != null) {
                    outputStream = Objects.requireNonNull(imageUri)?.let {
                        resolver.openOutputStream(it)}!!
                    imageBitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream)
                    Objects.requireNonNull(outputStream)

                }
            }catch (e: IOException){
                e.printStackTrace()
            }
            if(imageUri != null){
                val file=File(getRealPathFromUri(requireContext(),imageUri))
                file.mkdir()
                val builder=MultipartBody.Builder()
                builder.addFormDataPart("images", file.name, RequestBody.create(MediaType.parse(getRealPathFromUri(requireContext(),imageUri)), file))
                builder.addFormDataPart("organs","leaf")
                builder.setType(MultipartBody.FORM)
                var requestBody=builder.build()
                viewModel.loadPlant(requestBody)
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
        binding= FragmentPlantIdentificationBinding.inflate(inflater,container,false)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            checkExternalStoragePermission();
        }
        responseLauncher.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE))
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
            binding.prueba.text=it.commonName
        }

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
}