package com.tt.skolarrs.view.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.tt.skolarrs.R
import com.tt.skolarrs.databinding.ActivityLeaveListBinding
import com.tt.skolarrs.model.response.LeaveDetails
import com.tt.skolarrs.utilz.Constant
import com.tt.skolarrs.utilz.MyFunctions
import com.tt.skolarrs.view.adapter.LeaveListAdapter
import com.tt.skolarrs.viewmodel.LeaveViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LeaveListActivity : AppCompatActivity() {

    lateinit var binding: ActivityLeaveListBinding
    lateinit var viewModel : LeaveViewModel
    lateinit var token : String
    private var leaveList = ArrayList<LeaveDetails>()
    private lateinit var leaveListAdapter: LeaveListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLeaveListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[LeaveViewModel::class.java]

        binding.include.backArrow.setOnClickListener(View.OnClickListener {
            onBackPressed()
        })

        binding.include.title.text = getString(R.string.leave_apply)

        token = MyFunctions.getSharedPreference(this, Constant.TOKEN, Constant.TOKEN)!!

        if (MyFunctions.isConnected(this@LeaveListActivity)) {
            val scope = CoroutineScope(Dispatchers.IO)

            scope.launch(Dispatchers.Main) {

                getLeaveDetails()


            }
        } else {
            Toast.makeText(
                this@LeaveListActivity,
                getString(R.string.please_turn_on_your_internet_connection),
                Toast.LENGTH_SHORT
            )
                .show()
        }


    }

    private suspend fun getLeaveDetails() {
        MyFunctions.progressDialogShow(this@LeaveListActivity)
        viewModel.getLeaveDetails(token, applicationContext)
        //ViewModel.getFollowUpList((token))

        try {
            MyFunctions.progressDialogDismiss()
            val data = viewModel.LeaveResponse.value?.data
            val response = viewModel.LeaveResponse.value?.data

            if (viewModel.LeaveResponse.value!= null && response!!.isNotEmpty()) {
                leaveList = response as ArrayList<LeaveDetails>
            }
            else {

            }

            leaveListAdapter = LeaveListAdapter(this@LeaveListActivity,leaveList, object : LeaveListAdapter.LeaveListItemSelectListener{
                override fun onSelect(position: Int) {
                    TODO("Not yet implemented")
                }

                override fun unSelect(position: Int) {
                    TODO("Not yet implemented")
                }
            })

            binding.leaveList.adapter = leaveListAdapter


        }
        catch (e:java.lang.Exception) {
            e.printStackTrace()
        }
    }
}