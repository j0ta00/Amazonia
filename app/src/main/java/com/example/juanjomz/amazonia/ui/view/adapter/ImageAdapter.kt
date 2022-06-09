package com.example.juanjomz.amazonia.ui.view.adapter

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.juanjomz.amazonia.R
import com.example.juanjomz.amazonia.databinding.GalleryBinding

class ImageAdapter(private val itemsSelected:List<Bitmap>,private val onClickListener:(Bitmap)->Unit, private val onLongClickListener: (ImageView,Int) -> Unit) : ListAdapter<Bitmap,ImageAdapter.ImageViewHolder>(DiffUtilCallback) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            return ImageViewHolder(layoutInflater.inflate(R.layout.gallery,parent,false))
        }

        override fun onBindViewHolder(holder: ImageViewHolder, position: Int){
            holder.binding.bind (getItem(position),itemsSelected,position,onClickListener,onLongClickListener)
        }

        inner class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view){

            val binding = GalleryBinding.bind(view)
        }
}
private object DiffUtilCallback: DiffUtil.ItemCallback<Bitmap>() {
    override fun areItemsTheSame(oldItem: Bitmap, newItem: Bitmap): Boolean =
        oldItem.equals(newItem)

    override fun areContentsTheSame(oldItem: Bitmap, newItem: Bitmap): Boolean =
        oldItem.sameAs(newItem)
}

private fun GalleryBinding.bind(
    image:Bitmap,itemsSelected: List<Bitmap>,position: Int,
    onClickListener: (Bitmap) -> Unit, onLongClickListener: (ImageView,Int) -> Unit
) {
   root.setOnLongClickListener{onLongClickListener(galleryImage,position)
       true}
    root.setOnClickListener{onClickListener(image)}
    galleryImage.setImageBitmap(image)
    var alreadyPainted=false
    itemsSelected.forEach {
        if ((galleryImage.drawable as BitmapDrawable).bitmap.sameAs(it) && !alreadyPainted){
            alreadyPainted=true
            galleryImage.setBackgroundColor(Color.HSVToColor(floatArrayOf(112f, 172f, 224f)))
        }else{
            galleryImage.setBackgroundColor(Color.HSVToColor(floatArrayOf(136f, 0f, 100f)))
        }
    }
}