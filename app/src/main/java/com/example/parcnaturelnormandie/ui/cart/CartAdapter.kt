package com.example.parcnaturelnormandie.ui.cart

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.parcnaturelnormandie.R
import com.example.parcnaturelnormandie.databinding.ItemCartBinding
import java.time.format.DateTimeFormatter

class CartAdapter(
    private val items: List<CartItem>,
    private val onDeleteClick: (CartItem) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    inner class CartViewHolder(val binding: ItemCartBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = items[position]
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")

        val imageUrl = item.imgUrl;

        val imgActivity: ImageView = holder.binding.imgActivity
        // Logique identique à fragment_item.xml :
        if (imageUrl != null && imageUrl.isNotBlank()) {
            val url = if (imageUrl.startsWith("http")) imageUrl else "http://172.17.23.200:8002/${imageUrl}"
            Glide.with(holder.itemView)
                .load(url)
                .centerCrop()
                .placeholder(R.drawable.ic_dashboard_black_24dp)
                .into(imgActivity)
        } else {
            imgActivity.setImageResource(item.imageResId)
        }

        holder.binding.txtTitle.text = item.title
        holder.binding.txtDuration.text = item.duration
        holder.binding.txtPrice.text = item.price
        holder.binding.txtDate.text = item.date.format(formatter)

        holder.binding.buttonDelete.setOnClickListener {
            onDeleteClick(item)
        }
    }

    override fun getItemCount(): Int = items.size
}
