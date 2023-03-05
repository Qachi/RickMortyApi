package com.example.rickandmortyapi.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.rickandmortyapi.R
import com.example.rickandmortyapi.model.Character
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_list.view.*

class CharacterAdapter(
    private val clickListener: OnCharacterClickListener
) : PagingDataAdapter<Character, CharacterAdapter.MyViewHolder>(diffUtil) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_list, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = getItem(position) ?: return
        holder.initialize(currentItem)

    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var imageView: ImageView = itemView.user_avatar

        fun initialize(character: Character) {
            itemView.apply {
                name_Txt.text = character.name
                species_Txt.text = character.species
                gender_Txt.text = character.gender

                Picasso.get()
                    .load(character.image)
                    .into(imageView)
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