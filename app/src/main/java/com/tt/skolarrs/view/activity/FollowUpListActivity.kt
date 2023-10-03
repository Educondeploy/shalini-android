package com.tt.skolarrs.view.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.tt.skolarrs.R
import com.tt.skolarrs.databinding.ActivityFollowUpListBinding
import com.tt.skolarrs.model.response.FollowUpData
import com.tt.skolarrs.utilz.Constant
import com.tt.skolarrs.utilz.MyFunctions
import com.tt.skolarrs.view.adapter.FollowUpListener
import com.tt.skolarrs.view.adapter.FollowUpViewModelAdapter
import com.tt.skolarrs.viewmodel.FollowUpViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FollowUpListActivity : AppCompatActivity() {

    var year = 0
    var month: Int = 0
    var day: Int = 0
    lateinit var token: String
    lateinit var notes:String

    private lateinit var leadViewModelAdapter: FollowUpViewModelAdapter
    private lateinit var leadViewModel: FollowUpViewModel

    private var list = ArrayList<FollowUpData>()

    lateinit var binding: ActivityFollowUpListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFollowUpListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        token = MyFunctions.getSharedPreference(
            this@FollowUpListActivity,
            Constant.TOKEN,
            Constant.TOKEN
        )!!

        leadViewModel = ViewModelProvider(this)[FollowUpViewModel::class.java]

        binding.include.title.text = "Follow Up Details"

        binding.include.backArrow.setOnClickListener(View.OnClickListener {
            onBackPressed()
        })

        if (MyFunctions.isConnected(this@FollowUpListActivity)) {

            val scope = CoroutineScope(Dispatchers.IO)
            scope.launch(Dispatchers.Main) {
                getFollowUpList()
                leadViewModelAdapter =
                    FollowUpViewModelAdapter(this@FollowUpListActivity, list, object :
                        FollowUpListener {
                        override fun onSelect(position: Int) {
                            TODO("Not yet implemented")
                        }

                        override fun unSelect(position: Int) {
                            TODO("Not yet implemented")
                        }
                    })

                Log.d("TAG", "onCreateView: " + leadViewModelAdapter.itemCount)
                binding.leadList.adapter = leadViewModelAdapter
            }
        } else {
            Toast.makeText(
                this@FollowUpListActivity,
                getString(R.string.please_turn_on_your_internet_connection),
                Toast.LENGTH_SHORT
            ).show()
        }

    }


    private suspend fun getFollowUpList() {
        list.clear()
        MyFunctions.progressDialogShow(this@FollowUpListActivity)
        leadViewModel.getFollowUpList(token, applicationContext)
        //ViewModel.getFollowUpList((token))

        try {
            MyFunctions.progressDialogDismiss()
            val data = leadViewModel.response.value!!.data
            Log.d("TAG", "getFollowUpList: " + data.size)
            if (data.isNotEmpty()) {
                val response = leadViewModel.response.value!!.data
                if (response.isNotEmpty()) {
                    binding.leadList.visibility = View.VISIBLE
                    binding.noData.visibility = View.GONE
                    list =
                        leadViewModel.response.value!!.data as ArrayList<FollowUpData>
                } else {
                    binding.leadList.visibility = View.GONE
                    binding.noData.visibility = View.VISIBLE
                    val responseError = leadViewModel.responseError.value ?: "Unknown error"
                    // Toast.makeText(requireActivity(), responseError, Toast.LENGTH_SHORT).show()
                }
            } else {
                Log.d("TAG", "getFollowUpList: ")
                binding.leadList.visibility = View.GONE
                binding.noData.visibility = View.VISIBLE
            }

        } catch (e: Exception) {
            MyFunctions.progressDialogDismiss()
            val responseError = leadViewModel.responseError.value ?: "Unknown error"
            binding.leadList.visibility = View.GONE
            binding.noData.visibility = View.VISIBLE
            //   Toast.makeText(requireActivity(), responseError, Toast.LENGTH_SHORT).show()
            Log.d("TAG", "getLeads: " + e.message)
            e.printStackTrace()
        } catch (e: java.lang.NullPointerException) {
            MyFunctions.progressDialogDismiss()
            e.message
            binding.leadList.visibility = View.GONE
            binding.noData.visibility = View.VISIBLE
            Log.d("TAG", "getLeads: " + e.message)
        }

    }

}