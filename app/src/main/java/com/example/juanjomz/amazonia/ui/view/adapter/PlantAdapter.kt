package com.example.juanjomz.amazonia.ui.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.juanjomz.amazonia.R
import com.example.juanjomz.amazonia.databinding.ItemPlantBinding
import com.example.juanjomz.amazonia.domain.PlantBO
import com.example.juanjomz.amazonia.domain.PlantWithImageBO
/**
 * Adaptador para el recyclerview de las especies
 * la mayoría de métodos son override por lo que no tiene mucho sentido documentarlos ya que vienen documentados
 * en la clase padre.
 * */
class PlantAdapter(private val plantList: MutableList<PlantWithImageBO>, private val onClickListener:(PlantWithImageBO)->Unit) : ListAdapter<PlantWithImageBO,PlantAdapter.PlantViewHolder>(DiffUtilCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlantViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return PlantViewHolder(layoutInflater.inflate(R.layout.item_plant, parent, false))
    }

    override fun onBindViewHolder(holder: PlantViewHolder, position: Int) {
        holder.binding.bind(plantList[position], onClickListener)
    }

    private object DiffUtilCallback : DiffUtil.ItemCallback< PlantWithImageBO>() {
        override fun areItemsTheSame(oldItem:  PlantWithImageBO, newItem:  PlantWithImageBO): Boolean =
            oldItem.equals(newItem)

        override fun areContentsTheSame(oldItem: PlantWithImageBO, newItem: PlantWithImageBO): Boolean {
            return oldItem.commonName == newItem.commonName &&
                    oldItem.scientificName == newItem.scientificName &&
                    oldItem.family == newItem.family &&
                    oldItem.image==newItem.image
        }

    }

    inner class PlantViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemPlantBinding.bind(view)
    }
}
private fun ItemPlantBinding.bind(
    plant:PlantWithImageBO,
    onClickListener: (PlantWithImageBO) -> Unit
){
    if(plant.commonName=="null") {
        specie.text = plant.scientificName
    }else{
        specie.text = plant.commonName
    }
    Glide.with(root.context).load(plant.image).into(plantImage)
        root.setOnClickListener{
        onClickListener(plant)
    }
}








