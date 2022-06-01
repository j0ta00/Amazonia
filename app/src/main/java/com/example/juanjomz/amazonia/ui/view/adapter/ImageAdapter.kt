package com.example.juanjomz.amazonia.ui.view.adapter

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.juanjomz.amazonia.R
import com.example.juanjomz.amazonia.databinding.GalleryBinding
import com.example.juanjomz.amazonia.databinding.ItemPlantBinding
import com.example.juanjomz.amazonia.domain.PlantBO

class ImageAdapter(val imageList: List<Bitmap>, private val onClickListener:(Bitmap)->Unit) : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            return ImageViewHolder(layoutInflater.inflate(R.layout.gallery,parent,false))
        }

        override fun onBindViewHolder(holder: ImageViewHolder, position: Int){
            holder.render(imageList[position],onClickListener)
        }

        override fun getItemCount(): Int = imageList.size
        inner class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view){
            val binding= GalleryBinding.bind(view)

            fun render(image:Bitmap, onClickListener: (Bitmap)->Unit){
                binding.galleryImage.setImageBitmap(image)
                itemView.setOnClickListener{
                    onClickListener(image)
                }
            }
        }
}