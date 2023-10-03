package com.tt.skolarrs.view.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.tt.skolarrs.R
import com.tt.skolarrs.databinding.CardMenuBinding
import com.tt.skolarrs.model.Menu
import com.tt.skolarrs.utilz.Constant
import com.tt.skolarrs.utilz.MyFunctions
import com.tt.skolarrs.view.activity.LoginActivity
import com.tt.skolarrs.view.activity.MainActivity
import com.tt.skolarrs.viewmodel.LogoutViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MenuAdapter(
    var context: Context,
    var list: List<Menu>, var menu: String,
    var itemSelectListener: ItemSelectListener
) : RecyclerView.Adapter<MenuAdapter.ViewHolder>() {

    lateinit var subMenuListAdapter: SubMenuAdapter
    private lateinit var logoutViewModel: LogoutViewModel
    private lateinit var id: String

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = CardMenuBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val list = list[position]
        menu = "MENU_LIST"

        logoutViewModel = LogoutViewModel()

        list.image?.let { holder.binding.menuImage.setImageResource(it) }
        holder.binding.menuTittle.text = list.tittle.uppercase();


        subMenuListAdapter = SubMenuAdapter(
            context,
            Constant.SUBMENU_LIST,
            list.menuList,
            object : SubMenuAdapter.SubMenuSelectedListener {
                override fun onSelect(position: Int) {
                    itemSelectListener.subMenuSelect(position, list.tittle.uppercase())
                }

                override fun unSelect(position: Int) {
                    TODO("Not yet implemented")
                }
            })

        holder.binding.subMenuList.adapter = subMenuListAdapter

        holder.itemView.setOnClickListener(View.OnClickListener {
            itemSelectListener.onSelect(position)

            if (list.id == 6) {
                itemSelectListener.logout()

            }
            if (list.tittle == context.getString(R.string.follow_up)) {
                if (!holder.binding.subMenuList.isSelected) {
                    holder.binding.subMenuList.visibility = View.VISIBLE
                    holder.binding.subMenuList.isSelected = true
                    itemSelectListener.onSelect(position)

                } else {
                    holder.binding.subMenuList.visibility = View.GONE
                    holder.binding.subMenuList.isSelected = false
                    itemSelectListener.unSelect(position)
                }
            } else {
                if (list.tittle == context.getString(R.string.permission)) {
                    if (!holder.binding.subMenuList.isSelected) {
                        holder.binding.subMenuList.visibility = View.VISIBLE
                        holder.binding.subMenuList.isSelected = true
                        itemSelectListener.onSelect(position)

                    } else {
                        holder.binding.subMenuList.visibility = View.GONE
                        holder.binding.subMenuList.isSelected = false
                        itemSelectListener.unSelect(position)
                    }
                } else {
                    if (list.tittle == context.getString(R.string.leave)) {
                        if (!holder.binding.subMenuList.isSelected) {
                            holder.binding.subMenuList.visibility = View.VISIBLE
                            holder.binding.subMenuList.isSelected = true
                            itemSelectListener.onSelect(position)

                        } else {
                            holder.binding.subMenuList.visibility = View.GONE
                            holder.binding.subMenuList.isSelected = false
                            itemSelectListener.unSelect(position)
                        }
                    } else {
                        holder.binding.subMenuList.visibility = View.GONE
                        holder.binding.subMenuList.isSelected = false
                    }
                }
            }
        })

    }

    class ViewHolder(itemView: CardMenuBinding) : RecyclerView.ViewHolder(itemView.root) {
        var binding: CardMenuBinding

        init {
            binding = itemView
        }
    }

}

interface ItemSelectListener {
    fun onSelect(position: Int)
    fun unSelect(position: Int)
    fun subMenuSelect(position: Int, subMenuSelectedTitle: String)
    fun logout()

}
