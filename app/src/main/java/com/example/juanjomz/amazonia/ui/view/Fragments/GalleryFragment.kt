package com.example.juanjomz.amazonia.ui.view.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.google.android.material.bottomnavigation.BottomNavigationView

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Rect
import android.os.Build
import android.view.Gravity
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.juanjomz.amazonia.R
import com.example.juanjomz.amazonia.databinding.FragmentGalleryBinding
import com.example.juanjomz.amazonia.ui.view.adapter.ImageAdapter
import com.example.juanjomz.amazonia.ui.viewmodel.GalleryVM
import java.util.*
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.example.juanjomz.amazonia.databinding.BigImagesLayoutBinding
import com.example.juanjomz.amazonia.ui.viewmodel.ActivityVM
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlin.collections.ArrayList
import android.graphics.drawable.BitmapDrawable
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
/**
 * Fragmento galería el cual mostrará las imagenes realizadas con la app, las funciones override o de listener no tendrán documentación ya que vienen comentadas en el padre
 */
class GalleryFragment : Fragment(), View.OnClickListener {
    private lateinit var auth: FirebaseAuth
    private val itemsSelecteds=mutableListOf<Bitmap>()
    private val adapter by lazy{ImageAdapter(itemsSelecteds,{showBigImageDialog(it)},
        {view:ImageView,it:Int->onItemPressed(view,it)})}
    private val imageIndexToDelete = LinkedList<Int>()
    private var refresh: Boolean = true
    private var bindingDialog: BigImagesLayoutBinding? = null
    private val viewModel: GalleryVM by viewModels()
    private lateinit var binding: FragmentGalleryBinding
    private val activityViewModel: ActivityVM by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentGalleryBinding.inflate(inflater, container, false)
        binding.rcvGallery.adapter=adapter
        return binding.root
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.findViewById<BottomNavigationView>(R.id.navigationMenu)?.visibility = View.VISIBLE
        getString(R.string.emptyField)
        setupVMObservers()
        Firebase.initialize(context!!)
        auth = Firebase.auth
        binding.imgbDeleteImages.setOnClickListener(this)
        binding.rcvGallery.addItemDecoration(GridSpacingItemDecoration(3, 0, false))
        viewModel.loadLocalStorageImages(getString(R.string.filePath)+auth.currentUser?.email)
    }

    override fun onResume() {
        super.onResume()
        if(refresh){
            binding.cdLoading.visibility = View.VISIBLE
        }
    }
    private fun setupVMObservers() {
        viewModel.imageList.observe(viewLifecycleOwner) { image ->
            if(refresh) {
                binding.cdLoading.visibility = View.VISIBLE
                adapter.submitList(image)
            }
            binding.cdLoading.visibility = View.GONE
        }
        viewModel.imagesDeleted.observe(viewLifecycleOwner) {
            imagesDeleted(it)
            val newList= mutableListOf<Bitmap>()
            val newList2= mutableListOf<Bitmap>()
            newList.addAll(adapter.currentList)
            imageIndexToDelete.forEach { imagesIndex ->
                newList2.add(newList[imagesIndex])
            }
            newList2.forEach{
                newList.remove(it)
            }
            adapter.submitList(null)
            adapter.submitList(ArrayList(newList))
            binding.cdLoading.visibility = View.GONE
            binding.imgbDeleteImages.visibility = View.GONE
            binding.txtImagesToDeleteCount.visibility = View.GONE
            imageIndexToDelete.clear()
        }
        activityViewModel.refreshImages.observe(viewLifecycleOwner) {
            refresh = it
        }

    }
    /**
     * Propósito: muestra el resultado de las imagenes borradas
     * @param result: Boolean
     * */
    private fun imagesDeleted(result: Boolean) {
        if (result) {
            Toast.makeText(requireContext(), getString(R.string.imageDeleteCorrectly), Toast.LENGTH_SHORT)
        } else {
            Toast.makeText(requireContext(),
                getString(R.string.imageDeleteError),
                Toast.LENGTH_SHORT)
        }
        activityViewModel.refreshImages(true)
    }
    /**
     * Propósito: muestra el dialog de la imagen ampliada
     * @param image: Bitmap
     * */
    private fun showBigImageDialog(image: Bitmap) {
        val title = TextView(requireContext())
        title.text = getString(R.string.image)
        title.setBackgroundColor(Color.DKGRAY)
        title.setPadding(10, 10, 10, 10)
        title.gravity = Gravity.CENTER
        title.setTextColor(Color.WHITE)
        title.textSize = 20f
        bindingDialog = BigImagesLayoutBinding.inflate(LayoutInflater.from(requireContext()))
        bindingDialog!!.imgvimage.setImageBitmap(image)
        MaterialAlertDialogBuilder(requireContext())
            .setCustomTitle(title).setView(bindingDialog!!.root).show()
    }
    /**
     * Propósito: marca las imagenes y llama a las funciones que hacen la lógica del borrado
     * @param image: Bitmap
     * */
    private fun onItemPressed(image: ImageView, index: Int) {
        if (!imageIndexToDelete.contains(index)) {
            binding.imgbDeleteImages.visibility = View.VISIBLE
            binding.txtImagesToDeleteCount.visibility = View.VISIBLE
             image.setBackgroundColor(Color.HSVToColor(floatArrayOf(112f, 172f, 224f)))
            imageIndexToDelete.add(index)
            itemsSelecteds.add((image.drawable as BitmapDrawable).bitmap)
            binding.txtImagesToDeleteCount.text = imageIndexToDelete.size.toString()
        } else {
            image.setBackgroundColor(Color.WHITE)
            imageIndexToDelete.remove(index)
            itemsSelecteds.remove((image.drawable as BitmapDrawable).bitmap)
            if (imageIndexToDelete.size == 0) {
                binding.imgbDeleteImages.visibility = View.GONE
                binding.txtImagesToDeleteCount.visibility = View.GONE
            } else {
                binding.txtImagesToDeleteCount.text = imageIndexToDelete.size.toString()
            }
        }
    }
    /**
     * Clase para que el recyclerview se adecue a cada pantalla de forma programática
     *
     * */
    inner class GridSpacingItemDecoration(
        private val spanCount: Int,
        private val spacing: Int,
        private val includeEdge: Boolean,
    ) : ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State,
        ) {
            super.getItemOffsets(outRect, view, parent, state)
            val itemWidth = parent.width / 3
            view.layoutParams.width = itemWidth
        }
    }

    override fun onClick(p0: View?) {
        binding.cdLoading.visibility = View.VISIBLE
        viewModel.deleteImages(getString(R.string.filePath)+auth.currentUser?.email, imageIndexToDelete)
    }
}