package com.example.watchme.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.watchme.R
import com.example.watchme.databinding.FragmentHomePageBinding
import com.example.watchme.model.OmdbSearchItem
import com.example.watchme.model.SearchHistoryItem
import com.example.watchme.viewmodel.HomeViewModel
import com.example.watchme.viewmodel.SearchResultsState
import kotlinx.coroutines.launch

class HomePage : Fragment() {

    private var _binding: FragmentHomePageBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()

    private val resultsAdapter = MovieResultAdapter { movie -> openMovieDetails(movie) }

    private val historyAdapter = SearchHistoryAdapter(
        onItemClick = { item -> searchAgain(item) },
        onDeleteClick = { item -> viewModel.deleteHistory(item) }
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomePageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.resultsRv.layoutManager = LinearLayoutManager(requireContext())
        binding.resultsRv.adapter = resultsAdapter
        binding.historyRv.layoutManager = LinearLayoutManager(requireContext())
        binding.historyRv.adapter = historyAdapter

        binding.searchBtn.setOnClickListener { searchClicked() }
        binding.searchEdt.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchClicked()
                true
            } else {
                false
            }
        }

        observeState()
    }

    private fun searchClicked() {
        val query = binding.searchEdt.text.toString().trim()
        if (query.isEmpty()) {
            binding.searchEdt.error = getString(R.string.home_search_required)
            binding.searchEdt.requestFocus()
            return
        }

        binding.searchEdt.error = null
        hideKeyboard()
        viewModel.search(query)
    }

    private fun searchAgain(item: SearchHistoryItem) {
        binding.searchEdt.setText(item.query)
        binding.searchEdt.setSelection(item.query.length)
        searchClicked()
    }

    private fun openMovieDetails(movie: OmdbSearchItem) {
        val args = bundleOf(
            "imdbID" to movie.imdbID,
            "title" to movie.Title
        )
        findNavController().navigate(R.id.action_homePage_to_movieDetailsFragment, args)
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { viewModel.searchState.collect { renderSearchState(it) } }
                launch { viewModel.history.collect { renderHistory(it) } }
            }
        }
    }

    private fun renderSearchState(state: SearchResultsState) {
        with(binding) {
            resultsProgress.isVisible = state is SearchResultsState.Loading
            resultsRv.isVisible = state is SearchResultsState.Success
            resultsMessage.isVisible =
                state !is SearchResultsState.Loading && state !is SearchResultsState.Success
            searchBtn.isEnabled = state !is SearchResultsState.Loading

            when (state) {
                is SearchResultsState.Idle ->
                    resultsMessage.text = getString(R.string.home_results_idle)

                is SearchResultsState.Loading -> Unit

                is SearchResultsState.Success ->
                    resultsAdapter.submitList(state.movies) {
                        _binding?.resultsRv?.scrollToPosition(0)
                    }

                is SearchResultsState.Empty ->
                    resultsMessage.text = getString(R.string.home_results_empty, state.query)

                is SearchResultsState.Error ->
                    resultsMessage.text = state.message
            }
        }
    }

    private fun renderHistory(items: List<SearchHistoryItem>) {
        historyAdapter.submitList(items)
        binding.historyRv.isVisible = items.isNotEmpty()
        binding.historyEmpty.isVisible = items.isEmpty()
    }

    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(InputMethodManager::class.java)
        imm?.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
