package com.example.parcnaturelnormandie.ui.home
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.parcnaturelnormandie.MainActivity
import com.example.parcnaturelnormandie.R
import com.example.parcnaturelnormandie.databinding.FragmentHomeBinding
import com.example.parcnaturelnormandie.model.ActivityItem
import com.example.parcnaturelnormandie.model.SharedViewModel
import java.lang.Character.toString

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: HomeAdapter
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        // ViewModels
        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]

        // Adapter
        adapter = HomeAdapter(listOf()) { activity ->
            // id pour le filtre, nom/libellé pour l’affichage
            sharedViewModel.selectActivity(
                id = activity.id.toString(),
                label = activity.libelle
            )

            (requireActivity() as MainActivity).binding.navView.selectedItemId =
                R.id.navigation_activities
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        // Observer le LiveData des activités
        homeViewModel.activities.observe(viewLifecycleOwner) { list ->
            adapter.updateData(list)
        }

        // Charger les données
        homeViewModel.loadData()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
