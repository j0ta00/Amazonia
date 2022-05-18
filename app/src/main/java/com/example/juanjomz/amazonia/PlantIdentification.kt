package com.example.juanjomz.amazonia

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.CancellationSignal
import android.os.Environment
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.contentValuesOf
import androidx.fragment.app.viewModels
import com.example.juanjomz.amazonia.databinding.FragmentPlantIdentificationBinding
import com.example.juanjomz.amazonia.ui.viewmodel.PlantIdentificationVM
import com.facebook.CallbackManager
import java.io.*
import java.util.*

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
            val imageBitmap=activityResult.data?.extras?.get("data") as Bitmap
            val outputStream : OutputStream
            val inputStream : InputStream
            val file: File
            try {
                val resolver = context?.getContentResolver()
                val values= contentValuesOf().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME,"Image_.jpg")
                    put(MediaStore.MediaColumns.MIME_TYPE,"image/jpeg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH,Environment.DIRECTORY_PICTURES+File.separator+getString(R.string.folderPath))
                }
                val imageUri=resolver?.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values)
                if (resolver != null) {
                    outputStream = Objects.requireNonNull(imageUri)?.let {
                        resolver.openOutputStream(it)}!!
                    imageBitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream)
                    Objects.requireNonNull(outputStream)
                    viewModel.loadPlant(File("/document/primary:Pictures/TestFolder/Image_.jpg"),"leaf")
                }
            }catch (e: IOException){
                e.printStackTrace()
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
        responseLauncher.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupVMObservers()

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