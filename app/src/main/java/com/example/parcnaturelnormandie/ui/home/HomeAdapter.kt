// kotlin
package com.example.parcnaturelnormandie.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.parcnaturelnormandie.R

class HomeAdapter(
    private var items: List<ActivityType>,
    private val onClick: ((ActivityType) -> Unit)? = null
) : RecyclerView.Adapter<HomeAdapter.VH>() {

    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.textName)
        val image: ImageView = itemView.findViewById(R.id.imageThumb)
        init {
            itemView.setOnClickListener {
                val pos = bindingAdapterPosition
                if (pos != RecyclerView.NO_POSITION) onClick?.invoke(items[pos])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_activity_type, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.name.text = item.libelle
        val baseUrl = "http://172.17.23.200:8001/"
        Glide.with(holder.itemView)
            .load(baseUrl + item.imageUrl)
            .centerCrop()
            .placeholder(R.drawable.ic_placeholder)
            .into(holder.image)
    }

    override fun getItemCount(): Int = items.size
    fun updateData(newItems: List<ActivityType>) {
        items = newItems
        notifyDataSetChanged() // informe le RecyclerView que la liste a changé
    }
}
