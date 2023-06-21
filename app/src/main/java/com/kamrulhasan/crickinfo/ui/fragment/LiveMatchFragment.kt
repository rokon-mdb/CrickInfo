package com.kamrulhasan.crickinfo.ui.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import com.kamrulhasan.crickinfo.R
import com.kamrulhasan.crickinfo.adapter.FixtureAdapter
import com.kamrulhasan.crickinfo.databinding.FragmentLiveMatchBinding
import com.kamrulhasan.crickinfo.model.fixture.FixturesData
import com.kamrulhasan.crickinfo.network.NetworkConnection
import com.kamrulhasan.crickinfo.viewmodel.CrickInfoViewModel

private const val TAG = "LiveMatchFragment"

class LiveMatchFragment : Fragment() {
    private var _binding: FragmentLiveMatchBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: CrickInfoViewModel
    private lateinit var viewOwner: LifecycleOwner

    private var matchList = emptyList<FixturesData>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentLiveMatchBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[CrickInfoViewModel::class.java]

        viewOwner = viewLifecycleOwner
        viewModel.readUpcomingMatchShortList(3)

        NetworkConnection().observe(viewLifecycleOwner) { network ->
            if (matchList.isEmpty() && !network) {
                // load news
                Log.d(TAG, "onViewCreated: cloud off")
                binding.ivCloudOff.visibility = View.VISIBLE
                binding.tvCloudOff.visibility = View.VISIBLE
            } else if (matchList.isEmpty()) {
                viewModel.getLiveMatches()
                Log.d(TAG, "onViewCreated: hey jan")
                binding.ivCloudOff.setImageResource(R.drawable.icon_loading)
                binding.ivCloudOff.visibility = View.VISIBLE
                binding.tvCloudOff.visibility = View.GONE
            } else {
                binding.ivCloudOff.visibility = View.GONE
                binding.tvCloudOff.visibility = View.GONE
            }

            Handler(Looper.getMainLooper()).postDelayed({
                if (matchList.isEmpty() && !network) {
                    binding.ivCloudOff.setImageResource(R.drawable.icon_cloud_off_24)
                    binding.ivCloudOff.visibility = View.VISIBLE
                    binding.tvCloudOff.visibility = View.VISIBLE
                } else if (matchList.isEmpty()) {
                    binding.ivCloudOff.setImageResource(R.drawable.icon_sync_problem_24)
                    binding.ivCloudOff.visibility = View.VISIBLE
                    binding.tvCloudOff.visibility = View.GONE
                } else {
                    binding.ivCloudOff.visibility = View.GONE
                    binding.tvCloudOff.visibility = View.GONE
                }
            }, 5000)
        }

        viewModel.liveMatches.observe(viewLifecycleOwner) {
            it?.let {
                matchList = it
                binding.recyclerViewLive.adapter =
                    FixtureAdapter(it, viewModel, viewLifecycleOwner)
            }
        }
        if (matchList.isNotEmpty()) {
            binding.recyclerViewLive.adapter =
                FixtureAdapter(matchList, viewModel, viewLifecycleOwner)
            binding.ivCloudOff.visibility = View.GONE
            binding.tvCloudOff.visibility = View.GONE
        }

        Handler(Looper.getMainLooper()).postDelayed({
            if (viewModel.liveMatches.value == null) {
                viewModel.shortList.observe(viewOwner) {
                    it?.let {
                        if (it != matchList) {
                            matchList = it
                            if (matchList.isNotEmpty()) {
                                binding.ivCloudOff.visibility = View.GONE
                                binding.tvCloudOff.visibility = View.GONE
                            }
                            binding.recyclerViewLive.adapter =
                                FixtureAdapter(matchList, viewModel, viewOwner)
                        }
                    }
                }
            }

        }, 2000)
    }
}

