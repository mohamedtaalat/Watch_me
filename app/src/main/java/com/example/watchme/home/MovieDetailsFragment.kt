package com.example.watchme.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.watchme.R
import com.example.watchme.api.RetrofitClient
import com.example.watchme.model.OmdbMovieResponse
import com.example.watchme.utils.loadMoviePoster
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.util.Log

class MovieDetailsFragment : Fragment() {

    private lateinit var backBtn: Button
    private lateinit var contentScroll: ScrollView
    private lateinit var detailsProgress: ProgressBar
    private lateinit var posterIv: ImageView
    private lateinit var titleTv: TextView
    private lateinit var metaTv: TextView
    private lateinit var ratingTv: TextView
    private lateinit var genreTv: TextView
    private lateinit var plotTitleTv: TextView
    private lateinit var plotTv: TextView
    private lateinit var detailsTitleTv: TextView
    private lateinit var detailsContainer: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_movie_details, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        backBtn = view.findViewById(R.id.back_btn)
        contentScroll = view.findViewById(R.id.content_scroll)
        detailsProgress = view.findViewById(R.id.details_progress)
        posterIv = view.findViewById(R.id.poster_iv)
        titleTv = view.findViewById(R.id.title_tv)
        metaTv = view.findViewById(R.id.meta_tv)
        ratingTv = view.findViewById(R.id.rating_tv)
        genreTv = view.findViewById(R.id.genre_tv)
        plotTitleTv = view.findViewById(R.id.plot_title_tv)
        plotTv = view.findViewById(R.id.plot_tv)
        detailsTitleTv = view.findViewById(R.id.details_title_tv)
        detailsContainer = view.findViewById(R.id.details_container)

        backBtn.setOnClickListener { findNavController().popBackStack() }

        loadMovie(arguments?.getString("imdbID").orEmpty())
    }

    private fun loadMovie(imdbId: String) {
        if (imdbId.isBlank()) {
            detailsProgress.visibility = View.GONE
            Toast.makeText(requireContext(), "Movie not found", Toast.LENGTH_SHORT).show()
            return
        }

        detailsProgress.visibility = View.VISIBLE
        contentScroll.visibility = View.GONE

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            try {
                val movie = RetrofitClient.omdbApi.getMovieById(imdbId)
                withContext(Dispatchers.Main) {
                    detailsProgress.visibility = View.GONE
                    if (movie.Response.equals("True", ignoreCase = true)) {
                        showMovie(movie)
                    } else {
                        Toast.makeText(
                            requireContext(),
                            movie.Error ?: "Movie not found",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    detailsProgress.visibility = View.GONE
                    Toast.makeText(
                        requireContext(),
                        "Could not load movie details",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun showMovie(movie: OmdbMovieResponse) {
        contentScroll.visibility = View.VISIBLE

        posterIv.loadMoviePoster(movie.Poster)

        titleTv.text = movie.Title.clean() ?: getString(R.string.details_unknown_title)

        val meta = listOfNotNull(
            movie.Year.clean(),
            movie.Type.clean()?.replaceFirstChar { it.uppercase() },
            movie.Rated.clean(),
            movie.Runtime.clean()
        ).joinToString("  •  ")
        metaTv.text = meta
        metaTv.visibility = if (meta.isEmpty()) View.GONE else View.VISIBLE

        val rating = movie.imdbRating.clean()
        val votes = movie.imdbVotes.clean()
        if (rating == null) {
            ratingTv.visibility = View.GONE
        } else {
            ratingTv.visibility = View.VISIBLE
            ratingTv.text = if (votes != null) {
                getString(R.string.details_imdb_rating_votes, rating, votes)
            } else {
                getString(R.string.details_imdb_rating, rating)
            }
        }

        val genre = movie.Genre.clean()
        genreTv.text = genre
        genreTv.visibility = if (genre == null) View.GONE else View.VISIBLE

        val plot = movie.Plot.clean()
        plotTv.text = plot
        val plotVisibility = if (plot == null) View.GONE else View.VISIBLE
        plotTitleTv.visibility = plotVisibility
        plotTv.visibility = plotVisibility

        detailsContainer.removeAllViews()
        addRow(getString(R.string.details_director), movie.Director)
        addRow(getString(R.string.details_writer), movie.Writer)
        addRow(getString(R.string.details_actors), movie.Actors)
        addRow(getString(R.string.details_released), movie.Released)
        addRow(getString(R.string.details_language), movie.Language)
        addRow(getString(R.string.details_country), movie.Country)
        addRow(getString(R.string.details_awards), movie.Awards)
        addRow(getString(R.string.details_seasons), movie.totalSeasons)
        addRow(getString(R.string.details_metascore), movie.Metascore)
        movie.Ratings.orEmpty().forEach { addRow(it.Source.orEmpty(), it.Value) }

        detailsTitleTv.visibility =
            if (detailsContainer.childCount > 0) View.VISIBLE else View.GONE
    }

    private fun addRow(label: String, value: String?) {
        val text = value.clean() ?: return
        if (label.isBlank()) return
        val row = layoutInflater.inflate(R.layout.item_detail_row, detailsContainer, false)
        row.findViewById<TextView>(R.id.label_tv).text = label
        row.findViewById<TextView>(R.id.value_tv).text = text
        detailsContainer.addView(row)
    }

    private fun String?.clean(): String? =
        this?.trim()?.takeIf { it.isNotEmpty() && !it.equals("N/A", ignoreCase = true) }
}