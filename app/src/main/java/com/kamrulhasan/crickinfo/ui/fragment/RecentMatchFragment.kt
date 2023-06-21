package com.kamrulhasan.crickinfo.ui.fragment

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.annotation.RequiresApi
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.kamrulhasan.crickinfo.R
import com.kamrulhasan.crickinfo.adapter.FixtureAdapter
import com.kamrulhasan.crickinfo.databinding.FragmentRecentMatchBinding
import com.kamrulhasan.crickinfo.model.fixture.FixturesData
import com.kamrulhasan.crickinfo.network.NetworkConnection
import com.kamrulhasan.crickinfo.utils.MyApplication
import com.kamrulhasan.crickinfo.viewmodel.CrickInfoViewModel

private const val TAG = "RecentMatchFragment"

class RecentMatchFragment : Fragment() {

    private var _binding: FragmentRecentMatchBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: CrickInfoViewModel
    private lateinit var viewOwner: LifecycleOwner

    private var matchList = emptyList<FixturesData>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentRecentMatchBinding.inflate(layoutInflater)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[CrickInfoViewModel::class.java]
        viewOwner = viewLifecycleOwner

        NetworkConnection().observe(viewLifecycleOwner) { network ->
            if (matchList.isEmpty() && !network) {

                binding.ivCloudOff.setImageResource(R.drawable.icon_cloud_off_24)
                binding.ivCloudOff.visibility = View.VISIBLE
                binding.tvCloudOff.visibility = View.VISIBLE

            } else if (matchList.isEmpty()) {

                binding.ivCloudOff.setImageResource(R.drawable.icon_loading)
                binding.ivCloudOff.visibility = View.VISIBLE
                binding.tvCloudOff.visibility = View.GONE

                viewModel.getRecentMatches()

                //handle data loading error
                Handler(Looper.getMainLooper()).postDelayed({
                    if (matchList.isEmpty() && network) {
                        binding.ivCloudOff.setImageResource(R.drawable.icon_sync_problem_24)
                        binding.ivCloudOff.visibility = View.VISIBLE
                        Toast.makeText(
                            MyApplication.appContext, "Data Sync Failed", Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        binding.ivCloudOff.visibility = View.GONE
                        binding.tvCloudOff.visibility = View.GONE
                    }
                }, 2000)
            }
        }

        if (matchList.isNotEmpty()) {
            binding.matchRecyclerView.adapter =
                FixtureAdapter(matchList, viewModel, viewLifecycleOwner)
            binding.ivCloudOff.visibility = View.GONE
            binding.tvCloudOff.visibility = View.GONE
        }

        viewModel.recentMatch.observe(viewLifecycleOwner) {

            if (it != null) {
                if (it != matchList) {
                    matchList = it
                    if (matchList.isNotEmpty()) {
                        binding.ivCloudOff.visibility = View.GONE
                        binding.tvCloudOff.visibility = View.GONE
                    }
                    val adapterViewState =
                        binding.matchRecyclerView.layoutManager?.onSaveInstanceState()
                    binding.matchRecyclerView.layoutManager?.onRestoreInstanceState(adapterViewState)

                    binding.matchRecyclerView.adapter =
                        FixtureAdapter(matchList, viewModel, viewLifecycleOwner)
                }
            } else {
                Log.d(TAG, "onViewCreated: null value")
            }
        }

        val bottomNav: BottomNavigationView = requireActivity().findViewById(R.id.bottom_nav_bar)

        binding.matchRecyclerView.setOnScrollChangeListener { _: View?, _: Int, scrollY: Int, _: Int, oldScrollY: Int ->
            if (scrollY > oldScrollY) {
                bottomNav.visibility = View.GONE
            } else {
                bottomNav.visibility = View.VISIBLE
            }
        }
    }

    override fun onPause() {
        super.onPause()

        val bottomNav: BottomNavigationView = requireActivity().findViewById(R.id.bottom_nav_bar)
        bottomNav.visibility = View.VISIBLE
    }
}