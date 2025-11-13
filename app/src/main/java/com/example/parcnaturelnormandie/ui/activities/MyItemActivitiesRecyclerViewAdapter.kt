package com.example.parcnaturelnormandie.ui.activities

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.parcnaturelnormandie.R
import com.example.parcnaturelnormandie.model.ActivityItem

class MyItemActivitiesRecyclerViewAdapter(
    private var items: List<ActivityItem>,
    private val onItemClick: (ActivityItem) -> Unit
) : RecyclerView.Adapter<MyItemActivitiesRecyclerViewAdapter.ActivityViewHolder>() {

    inner class ActivityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgActivity: ImageView = itemView.findViewById(R.id.imgActivity)
        val txtTitle: TextView = itemView.findViewById(R.id.txtTitle)
        val txtDuration: TextView = itemView.findViewById(R.id.txtDuration)
        val txtPrice: TextView = itemView.findViewById(R.id.txtPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_item, parent, false)
        return ActivityViewHolder(view)
    }

    override fun onBindViewHolder(holder: ActivityViewHolder, position: Int) {
        val item = items[position]

        // Titre = nom
        holder.txtTitle.text = item.nom

        // Durée
        holder.txtDuration.text = item.duree.ifBlank { "—" }

        // Prix : gratuit si 0, sinon "XX €"
        holder.txtPrice.text = when {
            item.tarif <= 0 -> "Gratuit"
            else -> "${item.tarif} €"
        }



        // Clic sur l’item
        holder.itemView.setOnClickListener {
            onItemClick(item)
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateItems(newItems: List<ActivityItem>) {
        items = newItems
        notifyDataSetChanged()
    }
}
