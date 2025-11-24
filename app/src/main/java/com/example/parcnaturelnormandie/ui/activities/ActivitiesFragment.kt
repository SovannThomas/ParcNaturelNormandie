package com.example.parcnaturelnormandie.ui.activities

import android.app.AlertDialog
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.parcnaturelnormandie.databinding.FragmentActivitiesBinding
import com.example.parcnaturelnormandie.model.ActivityItem
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL

class ActivitiesFragment : Fragment() {

    private var _binding: FragmentActivitiesBinding? = null
    private val binding get() = _binding!!

    val activityName = arguments?.getString("activity_name")

    private lateinit var adapter: MyItemActivitiesRecyclerViewAdapter
    private val activitiesUrl = "http://172.17.23.200:8002/api/activities"

    // REMIS
    private var fullActivities: List<ActivityItem> = emptyList()

    private var currentQuery: String = ""
    private var maxPrice: Int? = null               // en €
    private var maxDurationMinutes: Int? = null     // en minutes

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentActivitiesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Adapter avec liste vide au départ
        adapter = MyItemActivitiesRecyclerViewAdapter(emptyList()) { item ->
            // callback clic: gérer la navigation ou l’affichage de détails
        }

        binding.recyclerActivitiesView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerActivitiesView.adapter = adapter

        // REMIS
        setupSearchView()
        setupFilterButton()

        // Chargement des données depuis l'API
        loadActivitiesFromApi()
    }

    // REMIS
    private fun setupSearchView() {
        binding.searchActivitiesView.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                currentQuery = query.orEmpty()
                applyFilters()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                currentQuery = newText.orEmpty()
                applyFilters()
                return true
            }
        })
    }

    // REMIS
    private fun setupFilterButton() {
        binding.filterButton.setOnClickListener {
            showFilterDialog()
        }
    }

    // REMIS
    private fun showFilterDialog() {
        val context = requireContext()

        val container = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            val padding = (16 * resources.displayMetrics.density).toInt()
            setPadding(padding, padding, padding, padding)
        }

        val priceEditText = EditText(context).apply {
            hint = "Prix max (€)"
            inputType = InputType.TYPE_CLASS_NUMBER
            maxPrice?.let { setText(it.toString()) }
        }

        val durationEditText = EditText(context).apply {
            hint = "Durée max (minutes)"
            inputType = InputType.TYPE_CLASS_NUMBER
            maxDurationMinutes?.let { setText(it.toString()) }
        }

        container.addView(priceEditText)
        container.addView(durationEditText)

        AlertDialog.Builder(context)
            .setTitle("Filtrer les activités")
            .setView(container)
            .setPositiveButton("Appliquer") { _, _ ->
                maxPrice = priceEditText.text.toString().toIntOrNull()
                maxDurationMinutes = durationEditText.text.toString().toIntOrNull()
                applyFilters()
            }
            .setNegativeButton("Réinitialiser") { _, _ ->
                maxPrice = null
                maxDurationMinutes = null
                currentQuery = ""
                binding.searchActivitiesView.setQuery("", false)
                applyFilters()
            }
            .setNeutralButton("Annuler", null)
            .show()
    }

    // REMIS
    private fun applyFilters() {
        var result = fullActivities

        val q = currentQuery.trim()
        if (q.isNotEmpty()) {
            result = result.filter { activity ->
                activity.nom.contains(q, ignoreCase = true)
            }
        }

        maxPrice?.let { maxP ->
            result = result.filter { activity ->
                activity.tarif <= maxP
            }
        }

        maxDurationMinutes?.let { maxD ->
            result = result.filter { activity ->
                val minutes = parseDurationToMinutes(activity.duree) ?: Int.MAX_VALUE
                minutes <= maxD
            }
        }

        adapter.updateItems(result)
    }

    // REMIS
    private fun parseDurationToMinutes(duree: String): Int? {
        if (duree.isBlank()) return null
        return try {
            val parts = duree.split(":")
            val hours = parts.getOrNull(0)?.toIntOrNull() ?: 0
            val minutes = parts.getOrNull(1)?.toIntOrNull() ?: 0
            val seconds = parts.getOrNull(2)?.toIntOrNull() ?: 0
            hours * 60 + minutes + if (seconds > 0) 1 else 0
        } catch (_: Exception) {
            null
        }
    }

    private fun loadActivitiesFromApi() {
        Thread {
            try {
                val url = URL(activitiesUrl)
                val connection = (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "GET"
                    connectTimeout = 5000
                    readTimeout = 5000
                }

                val responseCode = connection.responseCode

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val response = connection.inputStream.bufferedReader().use { it.readText() }

                    val activities = parseActivitiesJson(response)

                    fullActivities = activities.sortedBy { it.nom.lowercase() }

                    activity?.runOnUiThread {
                        applyFilters() // REMIS
                    }
                } else {
                    activity?.runOnUiThread {
                        Toast.makeText(
                            requireContext(),
                            "Erreur serveur : $responseCode",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                connection.disconnect()
            } catch (e: Exception) {
                e.printStackTrace()
                activity?.runOnUiThread {
                    Toast.makeText(
                        requireContext(),
                        "Erreur de chargement des activités",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }.start()
    }

    private fun parseActivitiesJson(json: String): List<ActivityItem> {
        val list = mutableListOf<ActivityItem>()

        val jsonArray = JSONArray(json)

        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)

            val item = ActivityItem(
                id = obj.optString("id"),
                nom = obj.optString("nom"),
                description = obj.optString("description"),
                duree = obj.optString("duree"),
                tarif = obj.optInt("tarif", 0),
                type_id = obj.optInt("type_id", 0),
                image_url = obj.optString("image_url")
            )

            list.add(item)
        }

        return list
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
