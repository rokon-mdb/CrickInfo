package com.kamrulhasan.crickinfo.ui.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import com.kamrulhasan.crickinfo.R
import com.kamrulhasan.crickinfo.adapter.PlayerAdapter
import com.kamrulhasan.crickinfo.databinding.FragmentPlayersBinding
import com.kamrulhasan.crickinfo.model.custom.CustomPlayer
import com.kamrulhasan.crickinfo.network.NetworkConnection
import com.kamrulhasan.crickinfo.utils.MyApplication
import com.kamrulhasan.crickinfo.viewmodel.CrickInfoViewModel
import java.util.*

private const val TAG = "PlayersFragment"

class PlayersFragment : Fragment() {

    private var _binding: FragmentPlayersBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: CrickInfoViewModel
    private lateinit var viewLifecycle: LifecycleOwner

    private var playerList = emptyList<CustomPlayer>()
    private var tempPlayerMutableList = mutableListOf<CustomPlayer>()
    private var tempPlayerList = playerList

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentPlayersBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[CrickInfoViewModel::class.java]
        viewLifecycle = viewLifecycleOwner

        NetworkConnection().observe(viewLifecycleOwner) { network ->
            if (playerList.isEmpty() && !network) {
                // load news
                Log.d(TAG, "onViewCreated: cloud off")
                binding.ivCloudOff.visibility = View.VISIBLE
                binding.tvCloudOff.visibility = View.VISIBLE
            } else if (playerList.isEmpty()) {
                Log.d(TAG, "onViewCreated: hey jan")
                binding.ivCloudOff.setImageResource(R.drawable.icon_loading)
                binding.ivCloudOff.visibility = View.VISIBLE
                binding.tvCloudOff.visibility = View.GONE
            } else {
                binding.ivCloudOff.visibility = View.GONE
                binding.tvCloudOff.visibility = View.GONE
            }

            Handler(Looper.getMainLooper()).postDelayed({
                if (playerList.isEmpty() && !network) {
                    binding.ivCloudOff.setImageResource(R.drawable.icon_cloud_off_24)
                    binding.ivCloudOff.visibility = View.VISIBLE
                    binding.tvCloudOff.visibility = View.VISIBLE
                } else if (playerList.isEmpty()) {
                    binding.ivCloudOff.setImageResource(R.drawable.icon_sync_problem_24)
                    binding.ivCloudOff.visibility = View.VISIBLE
                    binding.tvCloudOff.visibility = View.GONE
                } else {
                    binding.ivCloudOff.visibility = View.GONE
                    binding.tvCloudOff.visibility = View.GONE
                }
            }, 5000)
        }
        if (playerList.isNotEmpty()) {
            binding.ivCloudOff.visibility = View.GONE
            binding.tvCloudOff.visibility = View.GONE
        }
        viewModel.readAllPlayers()
        viewModel.playerList.observe(viewLifecycleOwner) {
            it?.let {
                playerList = it
                tempPlayerList = it
                if (playerList.isNotEmpty()) {
                    binding.ivCloudOff.visibility = View.GONE
                    binding.tvCloudOff.visibility = View.GONE
                }
                binding.playerRecyclerView.adapter =
                    PlayerAdapter(tempPlayerList, viewModel, viewLifecycleOwner)
            }
        }
    }

    // Search menu
    @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

        menu.clear()
        inflater.inflate(R.menu.search_menu, menu)
        val search = menu.findItem(R.id.search_item)
        val searchView = search?.actionView as SearchView
        searchView.queryHint = "Search"

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {

                Toast.makeText(
                    MyApplication.appContext,
                    "Result has been showed.",
                    Toast.LENGTH_SHORT
                ).show()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchPlayer(newText.toString())

                return true
            }
        })
    }

    private fun searchPlayer(text: String) {

        tempPlayerMutableList.clear()
        val searchText = text.toLowerCase(Locale.getDefault())
        tempPlayerList = if (searchText.isNotEmpty()) {

            playerList.forEach {
                if (it.name?.toLowerCase(Locale.getDefault())?.contains(searchText) == true) {
                    tempPlayerMutableList.add(it)
                }
            }
            tempPlayerMutableList
        } else {
            playerList
        }

        if (tempPlayerList.isEmpty()) {
            binding.tvItemNa.visibility = View.VISIBLE
        } else {
            binding.tvItemNa.visibility = View.GONE
        }
        binding.playerRecyclerView.adapter = PlayerAdapter(tempPlayerList, viewModel, viewLifecycle)
    }

}