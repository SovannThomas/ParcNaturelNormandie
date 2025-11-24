package com.example.parcnaturelnormandie.ui.activities

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.parcnaturelnormandie.R
import com.example.parcnaturelnormandie.data.AppDatabase
import com.example.parcnaturelnormandie.data.CartItemEntity
import com.example.parcnaturelnormandie.model.ActivityItem
import java.time.LocalDateTime
import java.time.ZoneId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

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

        holder.txtTitle.text = item.nom

        holder.txtDuration.text = if (item.duree.isNotBlank()) item.duree else "—"

        holder.txtPrice.text = when {
            item.tarif <= 0 -> "Gratuit"
            else -> "${item.tarif} €"
        }

        val baseUrl = "http://172.17.23.200:8002/"
        Glide.with(holder.itemView)
            .load(baseUrl + item.image_url)
            .centerCrop()
            .placeholder(R.drawable.ic_dashboard_black_24dp)
            .into(holder.imgActivity)

        // Clic item
        holder.itemView.setOnClickListener { onItemClick(item) }

        // Ajout bouton addToCartButton
        val addToCartButton = holder.itemView.findViewById<View>(R.id.addToCartButton)
        addToCartButton.setOnClickListener {
            val context = holder.itemView.context
            val now = LocalDateTime.now()
            DatePickerDialog(context, { _, year, month, dayOfMonth ->
                TimePickerDialog(context, { _, hourOfDay, minute ->
                    val selectedDateTime = LocalDateTime.of(year, month + 1, dayOfMonth, hourOfDay, minute)
                    // Ajout à la BDD Room
                    // On doit trouver un moyen d'accéder à un scope de Fragment ou Activity pour launch
                    // On tente avec (context as? FragmentActivity)?.lifecycleScope
                    val activity = context as? androidx.fragment.app.FragmentActivity
                    activity?.lifecycleScope?.launch {
                        val db = AppDatabase.getInstance(context)
                        db.cartDao().insert(
                            CartItemEntity(
                                title = item.nom,
                                duration = item.duree,
                                price = if (item.tarif <= 0) "Gratuit" else "${item.tarif} €",
                                dateTimeMillis = selectedDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
                                imgUrl = item.image_url
                            )
                        )
                        Toast.makeText(context, "Ajouté au panier !", Toast.LENGTH_SHORT).show()
                    }
                }, now.hour, now.minute, true).show()
            }, now.year, now.monthValue - 1, now.dayOfMonth).show()
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateItems(newItems: List<ActivityItem>) {
        items = newItems
        notifyDataSetChanged()
    }


}
