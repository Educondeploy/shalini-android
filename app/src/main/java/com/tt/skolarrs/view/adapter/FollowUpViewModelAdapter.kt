package com.tt.skolarrs.view.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.tt.skolarrs.R
import com.tt.skolarrs.databinding.CardFollowUpListBinding
import com.tt.skolarrs.model.response.FollowUpData

class FollowUpViewModelAdapter(
    var context: Context,
    var list: List<FollowUpData>,
    var listener: FollowUpListener
) : RecyclerView.Adapter<FollowUpViewModelAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            CardFollowUpListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var list1 = list[position]
        holder.binding.name.text = list1.leadId.name
        holder.binding.dateTime.text = list1.date + "| " + list1.time
        holder.binding.number.text = list1.leadId.mobileNo

        Log.d("TAG", "onBindViewHolder: " +list1.followUp )

        if (list1.followUp == "whatsapp") {
            holder.binding.call.setImageResource(R.drawable.whatsapp)
            holder.binding.call.clearColorFilter();
        } else if (list1.followUp == "call") {
            holder.binding.call.setImageResource(R.drawable.phone)
            holder.binding.call.setColorFilter(
                ContextCompat.getColor(
                    context,
                    R.color.primary_color
                ), android.graphics.PorterDuff.Mode.SRC_IN
            )
        } else {
            holder.binding.call.setImageResource(R.drawable.whatsapp)
            holder.binding.call.clearColorFilter();

        }


        holder.binding.call.setOnClickListener(View.OnClickListener {

        })

    }

    class ViewHolder(itemView: CardFollowUpListBinding) : RecyclerView.ViewHolder(itemView.root) {
        var binding: CardFollowUpListBinding

        init {
            binding = itemView
        }
    }


}

interface FollowUpListener {

    fun onSelect(position: Int)
    fun unSelect(position: Int)


}
