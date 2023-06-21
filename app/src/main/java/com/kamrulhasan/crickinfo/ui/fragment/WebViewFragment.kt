package com.kamrulhasan.crickinfo.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.kamrulhasan.crickinfo.R
import com.kamrulhasan.crickinfo.databinding.FragmentWebViewBinding
import com.kamrulhasan.crickinfo.network.NetworkConnection
import com.kamrulhasan.crickinfo.utils.DEFAULT_NEWS_PAGE
import com.kamrulhasan.crickinfo.utils.URL_KEY

private const val TAG = "WebViewFragment"

class WebViewFragment : Fragment() {

    private lateinit var bottomNav: BottomNavigationView

    private var _binding: FragmentWebViewBinding? = null
    private val binding get() = _binding!!

    private var newsUrl: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            newsUrl = it.getString(URL_KEY, DEFAULT_NEWS_PAGE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentWebViewBinding.inflate(layoutInflater)
        return binding.root
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bottomNav = requireActivity().findViewById(R.id.bottom_nav_bar)

        binding.webView.webViewClient = WebViewClient()
        binding.webView.visibility = View.VISIBLE

        binding.webView.webViewClient = WebViewClient()
        binding.webView.visibility = View.VISIBLE
        // this will load the url of the website
        NetworkConnection().observe(viewLifecycleOwner) { network ->
            if (network) {
                // load news
                Log.d(TAG, "onViewCreated: news url loading")
                binding.webView.visibility = View.VISIBLE
                binding.webView.loadUrl(newsUrl)
                binding.ivCloudOff.visibility = View.GONE
                binding.tvCloudOff.visibility = View.GONE
            } else {
                binding.webView.visibility = View.GONE
                binding.ivCloudOff.visibility = View.VISIBLE
                binding.tvCloudOff.visibility = View.VISIBLE
            }
        }

        // this will enable the javascript settings, it can also allow xss vulnerabilities
        binding.webView.settings.javaScriptEnabled = true

        // if you want to enable zoom feature
        binding.webView.settings.setSupportZoom(true)

        bottomNav.visibility = View.GONE

    }

    override fun onPause() {
        super.onPause()
        bottomNav.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}