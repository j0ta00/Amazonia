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
import androidx.core.view.drawToBitmap
import androidx.core.view.get
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
import androidx.fragment.app.FragmentManager
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
 * Use the [GalleryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class GalleryFragment : Fragment(), View.OnClickListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
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
            var newList= mutableListOf<Bitmap>()
            var newList2= mutableListOf<Bitmap>()
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

    private fun imagesDeleted(result: Boolean) {
        if (result) {
            Toast.makeText(requireContext(), "Images Deleted", Toast.LENGTH_SHORT)
        } else {
            Toast.makeText(requireContext(),
                "Something happens, deleted is wrong",
                Toast.LENGTH_SHORT)
        }
        activityViewModel.refreshImages(true)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment GalleryFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            GalleryFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    fun showBigImageDialog(p0: Bitmap) {
        val title = TextView(requireContext())
        title.text = "IMAGE"
        title.setBackgroundColor(Color.DKGRAY)
        title.setPadding(10, 10, 10, 10)
        title.gravity = Gravity.CENTER
        title.setTextColor(Color.WHITE)
        title.textSize = 20f
        bindingDialog = BigImagesLayoutBinding.inflate(LayoutInflater.from(requireContext()))
        bindingDialog!!.imgvimage.setImageBitmap(p0)
        MaterialAlertDialogBuilder(requireContext())
            .setCustomTitle(title).setView(bindingDialog!!.root).show()
    }

    fun onItemPressed(image: ImageView, index: Int) {
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
            super.getItemOffsets(outRect, view, parent, state);
            val itemWidth = parent.width / 3
            view.layoutParams.width = itemWidth
        }
    }

    override fun onClick(p0: View?) {
        binding.cdLoading.visibility = View.VISIBLE
        viewModel.deleteImages(getString(R.string.filePath)+auth.currentUser?.email, imageIndexToDelete)
    }
}