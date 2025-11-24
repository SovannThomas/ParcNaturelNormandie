package com.example.parcnaturelnormandie.ui.activities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
            // ex: ouvrir un fragment de détail, etc.
        }

        binding.recyclerActivitiesView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerActivitiesView.adapter = adapter

        // Chargement des données depuis l'API
        loadActivitiesFromApi()
    }

    private fun loadActivitiesFromApi() {
        // Ne jamais faire de requête réseau sur le thread principal
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

                    // On parse le JSON -> List<ActivityItem>
                    val activities = parseActivitiesJson(response)

                    // Mise à jour de l’UI sur le thread principal
                    activity?.runOnUiThread {
                        adapter.updateItems(activities)
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
