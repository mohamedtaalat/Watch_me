package com.example.watchme.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.watchme.databinding.ItemSearchHistoryBinding
import com.example.watchme.model.SearchHistoryItem

class SearchHistoryAdapter(
    private val onItemClick: (SearchHistoryItem) -> Unit,
    private val onDeleteClick: (SearchHistoryItem) -> Unit
) : ListAdapter<SearchHistoryItem, SearchHistoryAdapter.ViewHolder>(DiffCallback) {

    class ViewHolder(val binding: ItemSearchHistoryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSearchHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        with(holder.binding) {
            queryTv.text = item.query
            root.setOnClickListener { onItemClick(item) }
            deleteBtn.setOnClickListener { onDeleteClick(item) }
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<SearchHistoryItem>() {
        override fun areItemsTheSame(oldItem: SearchHistoryItem, newItem: SearchHistoryItem) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: SearchHistoryItem, newItem: SearchHistoryItem) =
            oldItem == newItem
    }
}
