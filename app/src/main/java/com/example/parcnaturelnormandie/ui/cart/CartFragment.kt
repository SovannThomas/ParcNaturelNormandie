package com.example.parcnaturelnormandie.ui.cart

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.parcnaturelnormandie.R
import com.example.parcnaturelnormandie.databinding.FragmentCartBinding
import java.time.LocalDateTime
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.example.parcnaturelnormandie.data.AppDatabase
import com.example.parcnaturelnormandie.data.CartItemEntity

class CartFragment : Fragment() {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!

    private val cartDao by lazy {
        AppDatabase.getInstance(requireContext()).cartDao()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val recyclerView = binding.recyclerViewCart
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // 1) insérer des exemples en base
        viewLifecycleOwner.lifecycleScope.launch {
            val demoItems = listOf(
                CartItemEntity(
                    title = "Belvédère d'En-Vau",
                    duration = "2h00",
                    price = "20€",
                    dateTimeMillis = System.currentTimeMillis()
                ),
                CartItemEntity(
                    title = "Sentier des Douaniers",
                    duration = "3h30",
                    price = "15€",
                    dateTimeMillis = System.currentTimeMillis()
                )
            )

            cartDao.clear()
            cartDao.insertAll(demoItems)

            // 2) lire depuis la base
            val itemsFromDb = cartDao.getAll()

            // 3) mapper vers ton modèle UI CartItem
            val cartItems = itemsFromDb.map {
                CartItem(
                    imageResId = R.drawable.ic_launcher_foreground,
                    title = it.title,
                    duration = it.duration,
                    price = it.price,
                    date = LocalDateTime.ofEpochSecond(
                        it.dateTimeMillis / 1000,
                        0,
                        java.time.ZoneOffset.UTC
                    )
                )
            }

            fun refreshAdapter() {
                viewLifecycleOwner.lifecycleScope.launch {
                    val updatedItems = cartDao.getAll().map {
                        CartItem(
                            imageResId = R.drawable.ic_launcher_foreground,
                            title = it.title,
                            duration = it.duration,
                            price = it.price,
                            date = LocalDateTime.ofEpochSecond(
                                it.dateTimeMillis / 1000,
                                0,
                                java.time.ZoneOffset.UTC
                            )
                        )
                    }
                    recyclerView.adapter = CartAdapter(updatedItems) { itemToDelete ->
                        viewLifecycleOwner.lifecycleScope.launch {
                            val entityToDelete = cartDao.getAll().find { it.title == itemToDelete.title }
                            if (entityToDelete != null) {
                                cartDao.delete(entityToDelete)
                                refreshAdapter()
                            }
                        }
                    }
                }
            }

            recyclerView.adapter = CartAdapter(cartItems) { cartItem ->
                viewLifecycleOwner.lifecycleScope.launch {
                    val entity = cartDao.getAll().find { it.title == cartItem.title }
                    if (entity != null) {
                        cartDao.delete(entity)
                        refreshAdapter()
                    }
                }
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}