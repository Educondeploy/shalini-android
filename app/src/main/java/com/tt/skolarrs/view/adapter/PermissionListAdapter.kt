package com.tt.skolarrs.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tt.skolarrs.R
import com.tt.skolarrs.databinding.CardPermissionListBinding
import com.tt.skolarrs.model.response.PermissionDetails
import com.tt.skolarrs.utilz.MyFunctions

class PermissionListAdapter(
    var context: Context,
    var list: List<PermissionDetails>,
    var itemSelectListener: PermissionListItemSelectListener
) : RecyclerView.Adapter<PermissionListAdapter.ViewHolder>() {

    lateinit var subMenuListAdapter: SubMenuAdapter

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = CardPermissionListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var list = list[position]

        holder.binding.date.text = list.date
        holder.binding.time.text =list.timeFrom  + " -" + "\n"  + list.timeTo
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

    class ViewHolder(itemView: CardPermissionListBinding) : RecyclerView.ViewHolder(itemView.root) {
        var binding: CardPermissionListBinding

        init {
            binding = itemView
        }
    }

    interface PermissionListItemSelectListener {
        fun onSelect(position: Int)
        fun unSelect(position: Int)

    }
}




