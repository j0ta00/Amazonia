package com.example.juanjomz.amazonia.ui.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.juanjomz.amazonia.R
import com.example.juanjomz.amazonia.databinding.ItemPlantBinding
import com.example.juanjomz.amazonia.domain.PlantBO

class PlantAdapter(val plantList: List<PlantBO>, val imageList: List<String>, private val onClickListener:(PlantBO)->Unit) : RecyclerView.Adapter<PlantAdapter.PlantViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlantViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return PlantViewHolder(layoutInflater.inflate(R.layout.item_plant,parent,false))
    }

    override fun onBindViewHolder(holder: PlantViewHolder, position: Int){
        holder.render(plantList[position],position,onClickListener)
    }

    override fun getItemCount(): Int = plantList.size
    inner class PlantViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val binding=ItemPlantBinding.bind(view)

        fun render(plant:PlantBO,position:Int,onClickListener: (PlantBO)->Unit){
            if(plant.commonName=="null") {
                binding.specie.text = plant.scientificName
            }else{
                binding.specie.text = plant.commonName
            }
            Glide.with(binding.root.context).load(imageList[position]).into(binding.plantImage)
            itemView.setOnClickListener{
                onClickListener(plant)
            }
        }
    }
}




