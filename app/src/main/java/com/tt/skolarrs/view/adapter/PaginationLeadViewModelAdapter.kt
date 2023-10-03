package com.tt.skolarrs.view.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tt.skolarrs.R
import com.tt.skolarrs.databinding.CardLeadListBinding
import com.tt.skolarrs.model.response.DataX
import com.tt.skolarrs.model.response.UpdatedList
import com.tt.skolarrs.model.response.UpdatedPaginationList
import com.tt.skolarrs.utilz.Constant
import com.tt.skolarrs.utilz.MyFunctions
import com.tt.skolarrs.view.activity.FollowUpActivity

class PaginationLeadViewModelAdapter(
    var context: Context, var listType: String,
    var list: ArrayList<UpdatedPaginationList>,
    var listener: PaginationListener,
) : RecyclerView.Adapter<PaginationLeadViewModelAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = CardLeadListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun filterList(filterlist: ArrayList<UpdatedPaginationList>) {

        list = filterlist

        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var updatedList = list[position]

        holder.binding.name.text = updatedList.modifiedList.name
        holder.binding.number.text = updatedList.modifiedList.mobileNo



        if (listType == context.getString(R.string.disconnect_the_call).uppercase()
            || listType == context.getString(R.string.not_interested).uppercase()
            || listType == context.getString(R.string.invalid_number).uppercase() || listType == context.getString(R.string.admission_closed).uppercase()
        ) {
            holder.binding.call.visibility = View.GONE
            holder.binding.reminder.visibility = View.GONE
        } else {
            holder.binding.call.visibility = View.VISIBLE
            holder.binding.reminder.visibility = View.VISIBLE
        }

        holder.binding.call.setOnClickListener(View.OnClickListener {
            listener.askPermission(
                updatedList.modifiedList.mobileNo,
                updatedList.modifiedList._id,
                updatedList.extraParam,
                updatedList.modifiedList.name
            )
        })

        holder.binding.view.setOnClickListener(View.OnClickListener {
            listener.viewProfile(updatedList.modifiedList._id)
        })
        Log.d("TAG", "onBindViewHolder: " +updatedList.extraParam  )
        if (updatedList.extraParam == "incomplete"   ) {
            holder.binding.reminder.visibility = View.GONE
        }
        else if (updatedList.extraParam == "completed"   ) {
            holder.binding.reminder.visibility = View.GONE
        }
        else  {
            holder.binding.reminder.visibility = View.VISIBLE
        }


        holder.binding.reminder.setOnClickListener(View.OnClickListener {
            if (updatedList.extraParam != "incomplete") {
                context.startActivity(
                    Intent(context, FollowUpActivity::class.java).putExtra(
                        Constant.LEAD_ID,
                        updatedList.modifiedList._id,
                    ).putExtra(
                        Constant.LEAD_STATUS, updatedList.extraParam
                    )
                )
            }
        })

        //Double click to open report history page

//         holder.binding.container.setOnClickListener(object : PaginationDoubleClickListener() {
//             override fun onDoubleClick(v: View?) {
//                 val date = updatedList.modifiedList.date
//                 if (date != null) {
//                     if (updatedList.extraParam != "incomplete") {
//                         listener.openDialogBox(updatedList.modifiedList._id, MyFunctions.dateFormat(date)!!)
//                     }
//                   //  listener.openDialogBox(updatedList.modifiedList._id, MyFunctions.dateFormat(date)!!)
//                 } else {
//                     // Handle the case when date is null
//                 }
////                 listener.openDialogBox(
////
////                     updatedList.modifiedList._id,
////                     MyFunctions.dateFormat(updatedList.modifiedList.date)!!
////                 )
//             }
//         })

    }

    class ViewHolder(itemView: CardLeadListBinding) : RecyclerView.ViewHolder(itemView.root) {
        var binding: CardLeadListBinding

        init {
            binding = itemView
        }
    }


}

interface PaginationListener {
    fun onSelect(position: Int)
    fun unSelect(position: Int)
    fun askPermission(mobileNo: String, _id: String, listType: String, name: String)
    fun openDialogBox(_id: String, createdDate: String)
    fun viewProfile(_id: String)

}

abstract class PaginationDoubleClickListener : View.OnClickListener {
    var lastClickTime: Long = 0
    override fun onClick(v: View?) {
        val clickTime = System.currentTimeMillis()
        if (clickTime - lastClickTime < DOUBLE_CLICK_TIME_DELTA) {
            onDoubleClick(v)
        }
        lastClickTime = clickTime
    }

    abstract fun onDoubleClick(v: View?)

    companion object {
        private const val DOUBLE_CLICK_TIME_DELTA: Long = 300 //milliseconds
    }
}
