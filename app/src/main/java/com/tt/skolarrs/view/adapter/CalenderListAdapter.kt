package com.tt.skolarrs.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tt.skolarrs.databinding.CardLeaveListBinding
import com.tt.skolarrs.model.response.CalenderData

class CalenderListAdapter(
    var context: Context,
    var list: List<CalenderData>,
    var itemSelectListener: CalenderItemSelectListener
) : RecyclerView.Adapter<CalenderListAdapter.ViewHolder>() {

    lateinit var subMenuListAdapter: SubMenuAdapter

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = CardLeaveListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var list = list[position]

        holder.binding.date.text = list.date
        holder.binding.status.text = list.holiday

    }

    class ViewHolder(itemView: CardLeaveListBinding) : RecyclerView.ViewHolder(itemView.root) {
        var binding: CardLeaveListBinding

        init {
            binding = itemView
        }
    }

    interface CalenderItemSelectListener {
        fun onSelect(position: Int)
        fun unSelect(position: Int)

    }
}




