package com.tt.skolarrs.view.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.tt.skolarrs.R
import com.tt.skolarrs.databinding.CardDashBoardBinding
import com.tt.skolarrs.model.DashBoardList

class DashBoardAdapter(
    var context: Context,
    var list: List<DashBoardList>,
    var itemSelectListener: DashBoardItemSelectListener
) : RecyclerView.Adapter<DashBoardAdapter.ViewHolder>() {

    lateinit var subMenuListAdapter: SubMenuAdapter

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = CardDashBoardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var list = list[position]

        holder.binding.tittle.text = list.tittle
        holder.binding.count.text = list.count


        if (list.tittle.equals(context.getString(R.string.interested))) {
            holder.binding.tittle.setTextColor(context.getColor(R.color.status_text_color))
        } else if (list.tittle.equals(context.getString(R.string.not_interested))) {
            holder.binding.tittle.setTextColor(context.getColor(R.color.red))
        } else if (list.tittle.equals(context.getString(R.string.hot))) {
            holder.binding.tittle.setTextColor((context.getColor(R.color.violet)))
        } else if (list.tittle.equals(context.getString(R.string.cold))) {
            holder.binding.tittle.setTextColor(context.getColor(R.color.blue))
        } else if (list.tittle.equals(context.getString(R.string.warm))) {
            holder.binding.tittle.setTextColor(context.getColor(R.color.teal_700));
        } else if (list.tittle.equals(context.getString(R.string.user_disconnect_the_call))) {
            holder.binding.tittle.setTextColor(context.getColor(R.color.blue));
        } else if (list.tittle.equals(context.getString(R.string.switchoff))) {
            holder.binding.tittle.setTextColor(context.getColor(R.color.red));
        } else if (list.tittle.equals(context.getString(R.string.out_of_coverage))) {
            holder.binding.tittle.setTextColor(context.getColor(R.color.yellow));
        } else if (list.tittle.equals(context.getString(R.string.invalid_number))) {
            holder.binding.tittle.setTextColor(context.getColor(R.color.blue));
        } else if (list.tittle.equals(context.getString(R.string.did_not_pick))) {
            holder.binding.tittle.setTextColor(context.getColor(R.color.violet));
        } else if (list.tittle.equals(context.getString(R.string.busy_on_another_call))) {
            holder.binding.tittle.setTextColor(context.getColor(R.color.teal_700))
        } else if (list.tittle.equals(context.getString(R.string.admission_closed))) {
            holder.binding.tittle.setTextColor(context.getColor(R.color.primary_color));
        }

        holder.itemView.setOnClickListener(View.OnClickListener {

            itemSelectListener.onSelect(position)

            /*if (position ==0 ) {
                Toast.makeText(
                    context,
                    context.getString(R.string.sorry_this_feature_is_not_supported_yet),
                    Toast.LENGTH_SHORT
                ).show()
            }
            if (position ==1) {
                Toast.makeText(
                    context,
                    context.getString(R.string.sorry_this_feature_is_not_supported_yet),
                    Toast.LENGTH_SHORT
                ).show()
            }
            if (position ==2 ) {
                Toast.makeText(
                    context,
                    context.getString(R.string.sorry_this_feature_is_not_supported_yet),
                    Toast.LENGTH_SHORT
                ).show()
            }
            if (position ==3 ) {
                Toast.makeText(
                    context,
                    context.getString(R.string.sorry_this_feature_is_not_supported_yet),
                    Toast.LENGTH_SHORT
                ).show()
            }
            if (position ==4 ) {
                Toast.makeText(
                    context,
                    context.getString(R.string.sorry_this_feature_is_not_supported_yet),
                    Toast.LENGTH_SHORT
                ).show()
            }
            if (position ==5 ) {
                Toast.makeText(
                    context,
                    context.getString(R.string.sorry_this_feature_is_not_supported_yet),
                    Toast.LENGTH_SHORT
                ).show()
            }
            if (position ==6 ) {
                Toast.makeText(
                    context,
                    context.getString(R.string.sorry_this_feature_is_not_supported_yet),
                    Toast.LENGTH_SHORT
                ).show()
            }
            if (position ==7 ) {
                Toast.makeText(
                    context,
                    context.getString(R.string.sorry_this_feature_is_not_supported_yet),
                    Toast.LENGTH_SHORT
                ).show()
            }
            if (position ==8) {
                Toast.makeText(
                    context,
                    context.getString(R.string.sorry_this_feature_is_not_supported_yet),
                    Toast.LENGTH_SHORT
                ).show()
            }
            if (position ==9 ) {
                Toast.makeText(
                    context,
                    context.getString(R.string.sorry_this_feature_is_not_supported_yet),
                    Toast.LENGTH_SHORT
                ).show()
            }
            if (position ==10) {
                Toast.makeText(
                    context,
                    context.getString(R.string.sorry_this_feature_is_not_supported_yet),
                    Toast.LENGTH_SHORT
                ).show()
            }
            if (position ==11 ) {
                Toast.makeText(
                    context,
                    context.getString(R.string.sorry_this_feature_is_not_supported_yet),
                    Toast.LENGTH_SHORT
                ).show()
            }
*/

        })

    }

    class ViewHolder(itemView: CardDashBoardBinding) : RecyclerView.ViewHolder(itemView.root) {
        var binding: CardDashBoardBinding

        init {
            binding = itemView
        }
    }

    interface DashBoardItemSelectListener {
        fun onSelect(position: Int)
        fun unSelect(position: Int)

    }
}




