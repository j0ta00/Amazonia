package com.example.juanjomz.amazonia.ui.view.Fragments

import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.juanjomz.amazonia.R
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.*
import android.view.Gravity
import android.widget.*
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.example.juanjomz.amazonia.databinding.SpecieDetailsLayoutBinding
import com.example.juanjomz.amazonia.domain.PlantWithImageBO
import com.example.juanjomz.amazonia.ui.viewmodel.ActivityVM


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
    private var refresh: Boolean = true
    private var param2: String? = null
    private var speciesListWithImages: MutableList<PlantWithImageBO> = mutableListOf()
    private var speciesList: List<PlantBO>? = null
    private val adapter by lazy {
        filtereSpeciesList?.let { list -> PlantAdapter(list) { onItemSelected(it) } }
    }
    private var imageList: List<String>? = LinkedList()
    private var bindingDialog: FilterLayoutBinding? = null
    private var bindingDetailsDialog: SpecieDetailsLayoutBinding? = null
    private val activityViewModel: ActivityVM by activityViewModels()
    private var filtereSpeciesList: LinkedList<PlantWithImageBO> = LinkedList()
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
        binding.plantList.adapter = adapter
        auth.currentUser?.email?.let { viewModel.loadSpeciesList(it) }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupVMObservers()
        setHasOptionsMenu(true)
        layoutInflater.inflate(R.layout.plant_identificated_dialog, null)
        binding.filters.setOnClickListener(this)
        binding.searchview.setOnQueryTextListener(this)
    }


    private fun showFirestoreError() {
        Toast.makeText(requireContext(), getString(R.string.showConnectionError), Toast.LENGTH_LONG)
            .show()
        binding.cdLoading.visibility = View.GONE
    }

    private fun setupVMObservers() {
        activityViewModel.refreshSpecies.observe(viewLifecycleOwner){
            refresh=it
        }

        viewModel.speciesList.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                speciesList = it
                viewModel.loadListOfImages(it)
                activityViewModel.refreshSpecies(false)
            }else{
                showFirestoreError()
            }
        }
        viewModel.imageList.observe(viewLifecycleOwner) {
            if(it.size==speciesList?.size && !refresh) {
                imageList = it
                (0 until speciesList?.size!!).forEach { i ->
                    if(!speciesListWithImages.contains(PlantWithImageBO(
                            speciesList!![i].commonName,
                            speciesList!![i].scientificName,
                            speciesList!![i].family,
                            imageList!![i]
                        ))) {
                        speciesListWithImages?.add(
                            PlantWithImageBO(
                                speciesList!![i].commonName,
                                speciesList!![i].scientificName,
                                speciesList!![i].family,
                                imageList!![i]
                            )
                        )
                    }
                }
                if (filtereSpeciesList.isNullOrEmpty()) {
                    speciesListWithImages?.let { it1 -> filtereSpeciesList.addAll(it1) }
                }
                buildList()
            }else if(it.size==speciesList?.size){
                binding.cdLoading.visibility=View.VISIBLE
            }
        }
    }


    private fun buildList() {
        filtereSpeciesList
        adapter?.submitList(filtereSpeciesList)
        binding.cdLoading.visibility = View.GONE
        activityViewModel.refreshSpecies(false)
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

    fun onItemSelected(plant: PlantWithImageBO) {
        bindingDetailsDialog =
            SpecieDetailsLayoutBinding.inflate(LayoutInflater.from(requireContext()))
        addDetailsToBinding(plant)
        val title = TextView(requireContext())
        title.text = "PLANT DETAILS"
        title.setBackgroundColor(Color.DKGRAY)
        title.setPadding(10, 10, 10, 10)
        title.gravity = Gravity.CENTER
        title.setTextColor(Color.WHITE)
        title.textSize = 20f
        MaterialAlertDialogBuilder(requireContext())
            .setCustomTitle(title).setView(bindingDetailsDialog!!.root)
            .setNeutralButton(resources.getString(R.string.cancel)) { dialog, which ->

            }.show()
    }


    private fun addDetailsToBinding(plant: PlantWithImageBO) {
        bindingDetailsDialog?.let {
            Glide.with(requireContext())
                .load(plant.image)
                .into(it.imgSpecieDetail)
        }
        var commonName: String = plant.commonName
        var family: String = plant.family
        if (plant.commonName == "null") {
            commonName = "Unknow"
        }
        if (plant.family == "null") {
            family = "Unknow"
        }
        bindingDetailsDialog?.specieCommonName?.text = commonName
        bindingDetailsDialog?.specieScientificName?.text = plant.scientificName
        bindingDetailsDialog?.Family?.text = family
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
                bindingDialog!!.chbSName.isChecked=true
                bindingDialog!!.chbCName.isChecked=true
                bindingDialog!!.chbFamily.isChecked=false
            }
            .setPositiveButton(resources.getString(R.string.acceptFilters)) { dialog, which ->
                if (bindingDialog!!.rdbAToZ.isChecked) {
                    filtereSpeciesList.clear()
                    filtereSpeciesList.addAll(speciesListWithImages.sortedBy { it.scientificName }
                        .sortedBy { it.commonName })
                    binding.plantList.adapter?.notifyDataSetChanged()
                } else if (bindingDialog!!.rdbZToA.isChecked) {
                    filtereSpeciesList.clear()
                    filtereSpeciesList.addAll(speciesListWithImages.sortedByDescending { it.scientificName }
                        .sortedByDescending { it.commonName })
                    binding.plantList.adapter?.notifyDataSetChanged()
                }

            }.show()

    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (!speciesList.isNullOrEmpty()) {
            filtereSpeciesList.clear()
            val searchText = newText!!.lowercase()
            if (searchText.isNotEmpty()) {
                speciesListWithImages?.forEach {
                    var alreadyAdded = false
                    if (bindingDialog == null) {
                        if (it.scientificName.lowercase()
                                .contains(searchText) || it.commonName.lowercase()
                                .contains(searchText)
                        ) {
                            filtereSpeciesList.add(it)
                        }
                    } else {
                        if(!bindingDialog!!.chbCName.isChecked && !bindingDialog!!.chbSName.isChecked && !bindingDialog!!.chbFamily.isChecked){
                            bindingDialog!!.chbSName.isChecked=true
                            bindingDialog!!.chbCName.isChecked=true
                        }
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
                speciesListWithImages?.let { filtereSpeciesList?.addAll(it) }
            }
            adapter.notifyDataSetChanged()
        }
        return false
    }


}