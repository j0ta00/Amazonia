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
/**
 * Fragmento listado de plantas, se centra en mostrar el listado de plantas y poder buscar y filtrar sobre este, las funciones override o de listener no tendrán documentación ya que vienen comentadas en el padre
 */
class PlantListFragment : Fragment(), View.OnClickListener, SearchView.OnQueryTextListener {
    private var refresh: Boolean = true
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
        activityViewModel.refreshImages(true)
        layoutInflater.inflate(R.layout.plant_identificated_dialog, null)
        binding.filters.setOnClickListener(this)
        binding.searchview.setOnQueryTextListener(this)
    }

    /**
     * Propósito: Muestra un error cuando el firestore no está disponible
     * */
    private fun showFirestoreError() {
        Toast.makeText(requireContext(), getString(R.string.showConnectionError), Toast.LENGTH_LONG)
            .show()
        binding.cdLoading.visibility = View.GONE
    }
    /**
     * Propósito: Muestra un mensaje pidiendo que añadas alguna especie
     * */
    private fun showAddSomeSpecies() {
        Toast.makeText(requireContext(), getString(R.string.addSomeSpecies), Toast.LENGTH_LONG)
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
            }else if(it.isEmpty()){
                showAddSomeSpecies()
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

    /**
     * Propósito: Construye la lista y la sube al adaptador
     * */
    private fun buildList() {
        filtereSpeciesList
        adapter?.submitList(filtereSpeciesList)
        binding.cdLoading.visibility = View.GONE
        activityViewModel.refreshSpecies(false)
    }

    private fun onItemSelected(plant: PlantWithImageBO) {
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

    /**
     * Propósito: Añade detalles al binding del dialog
     * @param plant: PlantWithImageBO
     * */
    private fun addDetailsToBinding(plant: PlantWithImageBO) {
        bindingDetailsDialog?.let {
            Glide.with(requireContext())
                .load(plant.image)
                .into(it.imgSpecieDetail)
        }
        var commonName: String = plant.commonName
        var family: String = plant.family
        if (plant.commonName == getString(R.string.null_value)) {
            commonName = getString(R.string.unknow_value)
        }
        if (plant.family == getString(R.string.null_value)) {
            family = getString(R.string.unknow_value)
        }
        bindingDetailsDialog?.specieCommonName?.text = commonName
        bindingDetailsDialog?.specieScientificName?.text = plant.scientificName
        bindingDetailsDialog?.Family?.text = family
    }

    override fun onClick(view: View?) {
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