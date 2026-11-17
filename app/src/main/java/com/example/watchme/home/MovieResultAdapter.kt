package com.example.watchme.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.watchme.databinding.ItemMovieResultBinding
import com.example.watchme.model.OmdbSearchItem
import com.example.watchme.utils.loadMoviePoster

class MovieResultAdapter(
    private val onMovieClick: (OmdbSearchItem) -> Unit
) : ListAdapter<OmdbSearchItem, MovieResultAdapter.ViewHolder>(DiffCallback) {

    class ViewHolder(val binding: ItemMovieResultBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMovieResultBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val movie = getItem(position)
        with(holder.binding) {
            titleTv.text = movie.Title ?: "Unknown title"
            metaTv.text = listOfNotNull(
                movie.Year?.takeIf { it.isNotBlank() },
                movie.Type?.replaceFirstChar { it.uppercase() }
            ).joinToString("  •  ")
            posterIv.loadMoviePoster(movie.Poster)
            root.setOnClickListener { onMovieClick(movie) }
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<OmdbSearchItem>() {
        override fun areItemsTheSame(oldItem: OmdbSearchItem, newItem: OmdbSearchItem) =
            oldItem.imdbID == newItem.imdbID

        override fun areContentsTheSame(oldItem: OmdbSearchItem, newItem: OmdbSearchItem) =
            oldItem == newItem
    }
}
