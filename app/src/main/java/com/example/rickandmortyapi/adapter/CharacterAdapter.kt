package com.example.rickandmortyapi.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.rickandmortyapi.databinding.ItemListBinding
import com.example.rickandmortyapi.model.Character
import com.squareup.picasso.Picasso

class CharacterAdapter(
    private val clickListener: OnCharacterClickListener
) : PagingDataAdapter<Character, CharacterAdapter.MyViewHolder>(diffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = getItem(position) ?: return
        holder.initialize(currentItem)
    }

    inner class MyViewHolder(private val binding: ItemListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun initialize(character: Character) {
            binding.apply {
                nameTxt.text = character.name
                speciesTxt.text = character.species
                genderTxt.text = character.gender

                Picasso.get()
                    .load(character.image)
                    .into(userAvatar)
            }
            itemView.setOnClickListener {
                clickListener.itemClicked(character, absoluteAdapterPosition)
            }
        }
    }

    companion object {

        val diffUtil = object : DiffUtil.ItemCallback<Character>() {
            override fun areItemsTheSame(
                oldItem: Character,
                newItem: Character
            ): Boolean {

                return oldItem.name == newItem.name
            }

            override fun areContentsTheSame(
                oldItem: Character,
                newItem: Character
            ): Boolean {
                return oldItem.name == newItem.name
            }
        }
    }

    interface OnCharacterClickListener {
        fun itemClicked(character: Character, position: Int)
    }
}