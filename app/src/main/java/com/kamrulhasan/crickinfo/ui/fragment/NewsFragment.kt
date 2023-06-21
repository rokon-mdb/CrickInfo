package com.kamrulhasan.crickinfo.ui.fragment

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.kamrulhasan.crickinfo.R
import com.kamrulhasan.crickinfo.adapter.NewsAdapter
import com.kamrulhasan.crickinfo.databinding.FragmentNewsBinding
import com.kamrulhasan.crickinfo.model.news.Article
import com.kamrulhasan.crickinfo.network.NetworkConnection
import com.kamrulhasan.crickinfo.utils.MyApplication
import com.kamrulhasan.crickinfo.viewmodel.CrickInfoViewModel

class NewsFragment : Fragment() {

    private var _binding: FragmentNewsBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: CrickInfoViewModel

    private var newsList: List<Article> = listOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentNewsBinding.inflate(layoutInflater)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[CrickInfoViewModel::class.java]

        NetworkConnection().observe(viewLifecycleOwner) {
            if (newsList.isEmpty() && !it) {

                binding.ivCloudOff.setImageResource(R.drawable.icon_cloud_off_24)
                binding.ivCloudOff.visibility = View.VISIBLE
                binding.tvCloudOff.visibility = View.VISIBLE

            } else if (newsList.isEmpty()) {

                binding.ivCloudOff.setImageResource(R.drawable.icon_loading)
                binding.ivCloudOff.visibility = View.VISIBLE
                binding.tvCloudOff.visibility = View.GONE

                viewModel.getNewsArticle()

                //handle data loading error
                Handler(Looper.getMainLooper()).postDelayed({
                    if (newsList.isEmpty()) {
                        binding.ivCloudOff.setImageResource(R.drawable.icon_sync_problem_24)
                        binding.ivCloudOff.visibility = View.VISIBLE
                        Toast.makeText(
                            MyApplication.appContext, "Data Sync Failed", Toast.LENGTH_SHORT
                        ).show()
                    }
                }, 5000)
            }
        }
        viewModel.news.observe(viewLifecycleOwner) {

            if (it != null) {
                newsList = it
                binding.recyclerView.adapter = NewsAdapter(newsList)
                if (newsList.isNotEmpty()) {
                    binding.ivCloudOff.visibility = View.GONE
                    binding.tvCloudOff.visibility = View.GONE
                }
            }
        }

        val bottomNav: BottomNavigationView = requireActivity().findViewById(R.id.bottom_nav_bar)

        binding.recyclerView.setOnScrollChangeListener { _: View?, _: Int, scrollY: Int, _: Int, oldScrollY: Int ->
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