package me.niwat.mvvm.presenter.dex.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import me.niwat.mvvm.R
import me.niwat.mvvm.data.models.Card
import me.niwat.mvvm.databinding.ItemPokedexBinding

class PokedexAdapter(private val pokedexList: MutableList<Card>) :
    RecyclerView.Adapter<PokedexAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = ItemPokedexBinding.bind(view)

        fun bindView(pokedexInfo: Card) {
            with(binding) {
                textViewName.text = pokedexInfo.name
                Glide.with(root.context)
                    .load(pokedexInfo.imageUrl)
                    .placeholder(R.drawable.default_image)
                    .error(R.drawable.default_image)
                    .into(imgPokemon)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PokedexAdapter.ViewHolder {
        val binding = ItemPokedexBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: PokedexAdapter.ViewHolder, position: Int) {
        holder.bindView(pokedexList[position])
    }

    override fun getItemCount(): Int = pokedexList.size
}