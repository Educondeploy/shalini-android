package com.tt.skolarrs.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tt.skolarrs.databinding.CardReportListBinding
import com.tt.skolarrs.model.response.ReportData

class ReportListAdapter(
    var context: Context,
    var list: List<ReportData>,
    var itemSelectListener: ReportListItemSelectListener
) : RecyclerView.Adapter<ReportListAdapter.ViewHolder>() {

    lateinit var subMenuListAdapter: SubMenuAdapter

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = CardReportListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var list = list[position]

        holder.binding.name.text = list.lead.name
        holder.binding.duration.text = list.callCount.toString()
        holder.binding.totalDuration.text = list.totalReportTime



    }

    class ViewHolder(itemView: CardReportListBinding) : RecyclerView.ViewHolder(itemView.root) {
        var binding: CardReportListBinding

        init {
            binding = itemView
        }
    }

    interface ReportListItemSelectListener {
        fun onSelect(position: Int)
        fun unSelect(position: Int)

    }
}




