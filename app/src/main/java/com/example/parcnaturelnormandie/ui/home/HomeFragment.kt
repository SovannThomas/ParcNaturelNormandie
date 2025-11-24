package com.example.parcnaturelnormandie.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.parcnaturelnormandie.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: HomeAdapter
    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Initialisation ViewBinding
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Initialisation ViewModel
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        // Initialisation RecyclerView et Adapter
        adapter = HomeAdapter(listOf()) { activity ->
            // Action au clic sur un item, par exemple :
            // Toast.makeText(requireContext(), activity.name, Toast.LENGTH_SHORT).show()
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        // Observer le LiveData des activités
        homeViewModel.activities.observe(viewLifecycleOwner) { list ->
            adapter.updateData(list)
        }

        // Charger les données depuis le ViewModel
        homeViewModel.loadData()

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}