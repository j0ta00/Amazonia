package com.example.juanjomz.amazonia.ui.view.Fragments

import android.os.Build
import androidx.annotation.RequiresApi
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.View.inflate
import androidx.fragment.app.Fragment
import android.widget.AdapterView
import android.widget.Button
import android.widget.ImageButton
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.juanjomz.amazonia.R
import com.example.juanjomz.amazonia.databinding.ActivityMainBinding.inflate
import com.example.juanjomz.amazonia.databinding.FragmentPlantListBinding
import com.example.juanjomz.amazonia.domain.PlantBO
import com.example.juanjomz.amazonia.ui.view.adapter.PlantAdapter
import com.example.juanjomz.amazonia.ui.viewmodel.PlantListVM
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import androidx.appcompat.widget.SearchView
import com.example.juanjomz.amazonia.databinding.FilterLayoutBinding
import com.example.juanjomz.amazonia.databinding.PlantIdentificatedDialogBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList
import android.view.Gravity
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavArgument
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.findNavController
import com.example.juanjomz.amazonia.ui.viewmodel.ActivityVM
import com.example.juanjomz.amazonia.ui.viewmodel.GalleryVM
import com.google.rpc.Help


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PlantListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PlantListFragment : Fragment(), View.OnClickListener, SearchView.OnQueryTextListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var speciesList: List<PlantBO>? = null
    private var refresh = true
    private var imageList: LinkedList<String>? = LinkedList()
    private var bindingDialog: FilterLayoutBinding? = null
    private val activityViewModel : ActivityVM by activityViewModels()
    private var filtereSpeciesList: LinkedList<PlantBO> = LinkedList()
    private val viewModel: PlantListVM by viewModels()
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: FragmentPlantListBinding
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
        binding = FragmentPlantListBinding.inflate(inflater, container, false)
        Firebase.initialize(context!!)
        auth = Firebase.auth
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupVMObservers()
        setHasOptionsMenu(true)
        layoutInflater.inflate(R.layout.plant_identificated_dialog, null)
        binding.filters.setOnClickListener(this)
        binding.searchview.setOnQueryTextListener(this)
        auth.currentUser?.email?.let { viewModel.loadSpeciesList(it) }

    }

    override fun onResume() {
        super.onResume()
        if(refresh){
            binding.progressBar.visibility = View.VISIBLE
        }
    }

    private fun setupVMObservers() {
        viewModel.speciesList.observe(viewLifecycleOwner) {
            speciesList = it
            viewModel.loadListOfImages(it)
            if (filtereSpeciesList.isNullOrEmpty()) {
                filtereSpeciesList.addAll(it)
            }
        }
        viewModel.imageList.observe(viewLifecycleOwner) {
            if (imageList.isNullOrEmpty()) {
                imageList?.addAll(it)
            }
            buildList()
        }
        activityViewModel.refreshSpecies.observe(viewLifecycleOwner){
                refresh=it
        }

    }


    private fun buildList() {
        if(refresh) {
            binding.plantList.adapter =
                filtereSpeciesList?.let {
                    imageList?.let { it1 ->
                        PlantAdapter(it, it1) { itemSelected ->
                            onItemSelected(itemSelected)
                        }
                    }
                }
        }
        binding.progressBar.visibility=View.GONE
        refresh=false
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PlantListFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PlantListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    fun onItemSelected(plant: PlantBO) {

    }


    override fun onClick(p0: View?) {
        bindingDialog = FilterLayoutBinding.inflate(LayoutInflater.from(requireContext()))
        val title = TextView(requireContext())
        title.text = "FILTERS"
        title.setBackgroundColor(Color.DKGRAY)
        title.setPadding(10, 10, 10, 10)
        title.gravity = Gravity.CENTER
        title.setTextColor(Color.WHITE)
        title.textSize = 20f
        MaterialAlertDialogBuilder(requireContext())
            .setCustomTitle(title).setView(bindingDialog!!.root)
            .setNeutralButton(resources.getString(R.string.cancel)) { dialog, which ->
                bindingDialog!!.chbSName.isActivated = true
                bindingDialog!!.chbCName.isActivated = true
                bindingDialog!!.chbFamily.isActivated = false
            }
            .setPositiveButton(resources.getString(R.string.acceptFilters)) { dialog, which ->
                if (bindingDialog!!.rdbAToZ.isChecked) {
                    filtereSpeciesList = LinkedList(filtereSpeciesList.sortedBy { it.commonName }
                        .sortedBy { it.scientificName })
                } else if (bindingDialog!!.rdbZToA.isChecked) {
                    filtereSpeciesList =
                        LinkedList(filtereSpeciesList.sortedByDescending { it.commonName }
                            .sortedByDescending { it.scientificName })
                }
                binding.plantList.adapter =
                    imageList?.let {
                        PlantAdapter(filtereSpeciesList, it) { itemSelected ->
                            onItemSelected(itemSelected)
                        }
                    }
            }.show()
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        TODO("Not yet implemented")
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (!speciesList.isNullOrEmpty()) {
            filtereSpeciesList.clear()
            val searchText = newText!!.lowercase()
            if (searchText.isNotEmpty()) {
                speciesList?.forEach {
                    var alreadyAdded = false
                    if (bindingDialog == null) {
                        if (it.scientificName.lowercase()
                                .contains(searchText) || it.commonName.lowercase()
                                .contains(searchText)
                        ) {
                            filtereSpeciesList.add(it)
                        }
                    } else {
                        if (bindingDialog!!.chbCName.isChecked && !alreadyAdded) {
                            if (it.commonName.lowercase().contains(searchText)) {
                                filtereSpeciesList.add(it)
                                alreadyAdded = true
                            }
                        }
                        if (bindingDialog!!.chbSName.isChecked && !alreadyAdded) {
                            if (it.scientificName.lowercase().contains(searchText)) {
                                filtereSpeciesList.add(it)
                                alreadyAdded = true
                            }
                        }
                        if (bindingDialog!!.chbFamily.isChecked && !alreadyAdded) {
                            if (it.family.lowercase().contains(searchText)) {
                                filtereSpeciesList.add(it)
                                alreadyAdded = true
                            }
                        }
                    }
                }
            } else {
                speciesList?.let { filtereSpeciesList?.addAll(it) }
            }
            binding.plantList.adapter?.notifyDataSetChanged()
        }
        return false
    }


}