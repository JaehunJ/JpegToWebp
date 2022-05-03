package com.example.jpegtowebp

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.jpegtowebp.databinding.LayoutImageItemBinding

class ImageRecyclerViewAdapter(val context: Context, val callback: (Int) -> Unit) :
    RecyclerView.Adapter<ImageRecyclerViewAdapter.ImageViewHolder>() {
    var list = mutableListOf<Uri>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ImageViewHolder.from(parent)

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val item = list[position]
        holder.bind(context, item) { callback(position) }
    }

    override fun getItemCount() = list.count()
    private fun getLastIndex() = itemCount - 1

    fun setData(l: MutableList<Uri>) {
        list = l
        notifyItemRangeChanged(0, itemCount)
    }

    fun addData(a: Uri) {
        val last = getLastIndex()
        list.add(a)

        notifyItemInserted(last + 1)
    }

    fun addData(l: MutableList<Uri>) {
        val last = getLastIndex()
        l.forEach {
            list.add(it)
        }

        notifyItemRangeInserted(last + 1, l.count())
    }

    fun removeData(pos: Int) {
        list.removeAt(pos)
        notifyItemRemoved(pos)
    }


    class ImageViewHolder(val b: LayoutImageItemBinding) : RecyclerView.ViewHolder(b.root) {
        companion object {
            fun from(parent: ViewGroup): ImageViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val b = LayoutImageItemBinding.inflate(layoutInflater, parent, false)

                return ImageViewHolder(b)
            }
        }

        fun bind(context: Context, uri: Uri, delete: () -> Unit) {
            Glide.with(context).load(uri).into(b.ivProduct)
            b.btnDelete.setOnClickListener {
                delete()
            }
        }
    }
}