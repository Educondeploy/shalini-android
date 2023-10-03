package com.tt.skolarrs.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tt.skolarrs.R
import com.tt.skolarrs.databinding.CardLeaveListBinding
import com.tt.skolarrs.model.response.LeaveDetails
import com.tt.skolarrs.utilz.MyFunctions

class LeaveListAdapter(
    var context: Context,
    var list: List<LeaveDetails>,
    var itemSelectListener: LeaveListItemSelectListener
) : RecyclerView.Adapter<LeaveListAdapter.ViewHolder>() {

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

        holder.binding.date.text = list.dateFrom + " -" + "\n" +list.dateTo
        holder.binding.status.text = list.status

        if (list.status == "processing") {
            holder.binding.status.text = MyFunctions.changeToProperCase(list.status)
            holder.binding.status.setTextColor(context.getColor(R.color.yellow))
        }

        if (list.status == "rejected") {
            holder.binding.status.text = MyFunctions.changeToProperCase(list.status)
            holder.binding.status.setTextColor(context.getColor(R.color.red))
        }

        if (list.status == "approved") {
            holder.binding.status.text = MyFunctions.changeToProperCase(list.status)
            holder.binding.status.setTextColor(context.getColor(R.color.status_text_color))
        }

    }

    class ViewHolder(itemView: CardLeaveListBinding) : RecyclerView.ViewHolder(itemView.root) {
        var binding: CardLeaveListBinding

        init {
            binding = itemView
        }
    }

    interface LeaveListItemSelectListener {
        fun onSelect(position: Int)
        fun unSelect(position: Int)

    }
}




