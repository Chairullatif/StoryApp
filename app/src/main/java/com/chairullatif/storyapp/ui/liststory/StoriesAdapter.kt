package com.chairullatif.storyapp.ui.liststory

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chairullatif.storyapp.data.model.StoryModel
import com.chairullatif.storyapp.databinding.ItemListStoryBinding
import com.chairullatif.storyapp.helper.GlideHelper.loadImage
import com.chairullatif.storyapp.ui.liststory.detailstory.DetailStoryActivity

class StoriesAdapter: RecyclerView.Adapter<StoriesAdapter.ViewHolder>() {

    private var listStory = ArrayList<StoryModel>()

    fun setListStory(listStory: List<StoryModel>) {
        this.listStory.clear()
        this.listStory.addAll(listStory)
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ItemListStoryBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(storyModel: StoryModel) {
            binding.apply {
                tvUserName.text = storyModel.name
                tvDesc.text = storyModel.description
                ivStory.loadImage(storyModel.photoUrl)
            }

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, DetailStoryActivity::class.java)
                intent.putExtra(DetailStoryActivity.EXTRA_ID_STORY, storyModel.id)
                itemView.context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoriesAdapter.ViewHolder {
        val binding = ItemListStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoriesAdapter.ViewHolder, position: Int) {
        holder.bind(listStory[position])
    }

    override fun getItemCount(): Int {
        return listStory.size
    }
}