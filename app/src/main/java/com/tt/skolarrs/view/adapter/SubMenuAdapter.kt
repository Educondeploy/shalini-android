package com.tt.skolarrs.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tt.skolarrs.databinding.CardSubMenuBinding
import com.tt.skolarrs.model.SubMenu

class SubMenuAdapter(var context: Context, var menu: String,var list: List<SubMenu>,var subMenuSelecttedListener : SubMenuSelectedListener): RecyclerView.Adapter<SubMenuAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubMenuAdapter.ViewHolder {
        val view = CardSubMenuBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val list = list[position]

        holder.binding.menuTittle.text=list.tittle.uppercase()

        holder.itemView.setOnClickListener(
            View.OnClickListener {
                subMenuSelecttedListener.onSelect(position)

            }
        )

    }

    override fun getItemCount(): Int {
       return  list.size
    }



    class ViewHolder(itemView: CardSubMenuBinding): RecyclerView.ViewHolder(itemView.root) {
        var binding: CardSubMenuBinding

        init {
            binding = itemView
        }
    }

    interface SubMenuSelectedListener {
        fun onSelect(position: Int)
        fun unSelect(position: Int)

    }

}
