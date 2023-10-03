package com.tt.skolarrs.view.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.tt.skolarrs.R
import com.tt.skolarrs.databinding.FragmentPermissionListBinding
import com.tt.skolarrs.model.response.PermissionDetails
import com.tt.skolarrs.utilz.Constant
import com.tt.skolarrs.utilz.MyFunctions
import com.tt.skolarrs.view.activity.MainActivity
import com.tt.skolarrs.view.activity.PermissionApplyActivity
import com.tt.skolarrs.view.adapter.PermissionListAdapter
import com.tt.skolarrs.viewmodel.PermissionViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class PermissionListFragment : Fragment() {

    lateinit var binding: FragmentPermissionListBinding
    var token: String = "0"
    private var backPressedTime: Long = 0
    private val doubleBackToExitInterval = 2000
    private var permissionList = ArrayList<PermissionDetails>()
    private lateinit var permissionViewModel: PermissionViewModel

    private lateinit var permissionListAdapter: PermissionListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                startActivity(Intent(requireActivity(),MainActivity::class.java))
                requireActivity().finish()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallback)

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val filterIcon = requireActivity().findViewById<ImageView>(R.id.filterIcon)

        filterIcon.visibility = View.GONE


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPermissionListBinding.inflate(inflater, container, false)
        val view = binding.root

        (requireActivity() as MainActivity).title =  MyFunctions.changeToProperCase(getString(R.string.permission))

        token = MyFunctions.getSharedPreference(requireActivity(), Constant.TOKEN, Constant.TOKEN)!!
        permissionViewModel = ViewModelProvider(this)[PermissionViewModel::class.java]

        binding.apply.setOnClickListener(View.OnClickListener {
            startActivity(Intent(requireActivity(), PermissionApplyActivity::class.java))
        })



        return view
    }

    override fun onResume() {
        permissionList.clear()
        super.onResume()
        if (MyFunctions.isConnected(requireActivity())) {
            val scope = CoroutineScope(Dispatchers.IO)

            scope.launch(Dispatchers.Main) {
                getPermissionDetails()

            }
        }
        else {
            Toast.makeText(
                requireActivity(),
                getString(R.string.please_turn_on_your_internet_connection),
                Toast.LENGTH_SHORT
            )
                .show()
        }
    }


    private suspend fun getPermissionDetails() {
       // MyFunctions.progressDialogShow(requireActivity())
        permissionViewModel.getPermissionDetails(token, requireContext())
        //ViewModel.getFollowUpList((token))

        try {
          //  MyFunctions.progressDialogDismiss()
            val data = permissionViewModel.LeaveResponse.value?.data
            val response = permissionViewModel.LeaveResponse.value?.data

            if (permissionViewModel.LeaveResponse.value != null && response!!.isNotEmpty()) {
                binding.container.visibility = View.VISIBLE
                binding.noData.visibility = View.GONE
                permissionList = response as ArrayList<PermissionDetails>
            } else {
                binding.container.visibility = View.GONE
                binding.noData.visibility = View.VISIBLE
            }

            permissionListAdapter = PermissionListAdapter(requireActivity(),permissionList, object : PermissionListAdapter.PermissionListItemSelectListener{
                override fun onSelect(position: Int) {
                    TODO("Not yet implemented")
                }

                override fun unSelect(position: Int) {
                    TODO("Not yet implemented")
                }
            })

            binding.leaveList.adapter = permissionListAdapter


        } catch (e: java.lang.Exception) {
        //    MyFunctions.progressDialogDismiss()
            e.printStackTrace()
            binding.container.visibility = View.GONE
            binding.noData.visibility = View.VISIBLE
        }
    }

}


