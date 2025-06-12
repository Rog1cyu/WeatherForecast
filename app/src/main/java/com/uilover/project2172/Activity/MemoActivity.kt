package com.uilover.project2172.Activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.uilover.project2172.Adapter.MemoAdapter
import com.uilover.project2172.Model.AppDatabase
import com.uilover.project2172.Model.Memo
import com.uilover.project2172.databinding.ActivityMemoBinding
import kotlinx.coroutines.launch

class MemoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMemoBinding
    private lateinit var adapter: MemoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMemoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = MemoAdapter { memo ->
            lifecycleScope.launch {
                val dao = AppDatabase.getInstance(this@MemoActivity).memoDao()
                dao.delete(memo)
                loadMemos()
            }
        }
        binding.memoList.layoutManager = LinearLayoutManager(this)
        binding.memoList.adapter = adapter

        val db = AppDatabase.getInstance(this)
        val dao = db.memoDao()

        binding.addButton.setOnClickListener {
            val text = binding.memoInput.text.toString().trim()
            if (text.isNotEmpty()) {
                lifecycleScope.launch {
                    dao.insert(Memo(content = text))
                    loadMemos()
                    binding.memoInput.setText("")
                }
            }
        }

        loadMemos()
        binding.chipNavigator.setOnItemSelectedListener { id ->
            when (id) {
                com.uilover.project2172.R.id.home -> {
                    finish() // 返回主界面
                }
                com.uilover.project2172.R.id.explorer -> {
                    // 暂时无跳转逻辑，可弹出提示
                }
                com.uilover.project2172.R.id.bookmark -> {
                    // 当前页面，无需处理
                }
                com.uilover.project2172.R.id.profile -> {
                    // 暂时不做跳转
                }
            }
        }

// 设置当前选中为 Bookmark
        binding.chipNavigator.setItemSelected(com.uilover.project2172.R.id.bookmark, true)
    }

    private fun loadMemos() {
        val dao = AppDatabase.getInstance(this).memoDao()
        lifecycleScope.launch {
            val memos = dao.getAll()
            adapter.submitList(memos)
        }
    }
}
