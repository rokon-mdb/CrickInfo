package com.kamrulhasan.crickinfo.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kamrulhasan.crickinfo.R
import com.kamrulhasan.crickinfo.model.news.Article
import com.kamrulhasan.crickinfo.utils.DateConverter
import com.kamrulhasan.crickinfo.utils.MyApplication
import com.kamrulhasan.crickinfo.utils.URL_KEY

class NewsAdapter(
    private val newsList: List<Article>
) : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    class NewsViewHolder(binding: View) : RecyclerView.ViewHolder(binding.rootView) {

        val publishedDate: TextView = binding.findViewById(R.id.tv_publish_date)
        val title: TextView = binding.findViewById(R.id.tv_news_title)
        val desc: TextView = binding.findViewById(R.id.tv_news_description)
        val img: ImageView = binding.findViewById(R.id.iv_news_photo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view =
            LayoutInflater.from(MyApplication.appContext).inflate(R.layout.news_item, parent, false)
        return NewsViewHolder(view)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {

        val newsItem = newsList[position]

        holder.publishedDate.text = newsItem.publishedAt?.let { DateConverter.zoneToDate(it) }
        holder.title.text = newsItem.title
        holder.desc.text = newsItem.description

        //loading news image with glide
        Glide
            .with(holder.itemView.context)
            .load(newsItem.urlToImage)
            .centerCrop()
            .placeholder(R.drawable.icon_loading)
            .into(holder.img)

        holder.itemView.setOnClickListener {
            val bundle = Bundle()
            bundle.putString(URL_KEY, newsItem.url)
            //navigate to web view fragment
            holder.itemView.findNavController().navigate(R.id.webViewFragment, bundle)
        }
    }

    override fun getItemCount(): Int {
        return newsList.size
    }
}