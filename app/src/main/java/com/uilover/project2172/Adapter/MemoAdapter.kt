package com.uilover.project2172.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.uilover.project2172.databinding.ItemMemoBinding
import com.uilover.project2172.Model.Memo
import java.text.SimpleDateFormat
import java.util.*

class MemoAdapter(
    private val onDelete: ((Memo) -> Unit)? = null
) : RecyclerView.Adapter<MemoAdapter.MemoViewHolder>() {

    private val memoList = ArrayList<Memo>()

    inner class MemoViewHolder(val binding: ItemMemoBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemoViewHolder {
        val binding = ItemMemoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MemoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MemoViewHolder, position: Int) {
        val memo = memoList[position]
        holder.binding.memoContent.text = memo.content
        holder.binding.memoTime.text = formatDate(memo.timestamp)

        holder.binding.deleteButton.setOnClickListener {
            onDelete?.invoke(memo)
        }
    }

    override fun getItemCount(): Int = memoList.size

    fun submitList(list: List<Memo>) {
        memoList.clear()
        memoList.addAll(list)
        notifyDataSetChanged()
    }

    private fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}
