package com.tt.skolarrs.view.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import com.tt.skolarrs.R
import com.tt.skolarrs.databinding.FragmentLeaveListBinding
import com.tt.skolarrs.model.response.LeaveDetails
import com.tt.skolarrs.utilz.Constant
import com.tt.skolarrs.utilz.MyFunctions
import com.tt.skolarrs.view.activity.LeaveApplyActivity
import com.tt.skolarrs.view.activity.MainActivity
import com.tt.skolarrs.view.adapter.LeaveListAdapter
import com.tt.skolarrs.viewmodel.LeaveViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LeaveListFragment : Fragment() {

    lateinit var binding: FragmentLeaveListBinding
    lateinit var viewModel : LeaveViewModel
    lateinit var token : String
    private var leaveList = ArrayList<LeaveDetails>()
    private lateinit var leaveListAdapter: LeaveListAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                startActivity(Intent(requireActivity(),MainActivity::class.java))
                requireActivity().finish()
            }
        }

        // Add the onBackPressedCallback to the Fragment's OnBackPressedDispatcher
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
        binding = FragmentLeaveListBinding.inflate(inflater, container, false)
        val view: View = binding.root

        binding.apply.setOnClickListener(View.OnClickListener {
            startActivity(Intent(requireActivity(),LeaveApplyActivity::class.java))
        })

        (requireActivity() as MainActivity).title = MyFunctions.changeToProperCase(getString(R.string.leave))

        viewModel = ViewModelProvider(this)[LeaveViewModel::class.java]

        token = MyFunctions.getSharedPreference(requireActivity(), Constant.TOKEN, Constant.TOKEN)!!

        return view
    }

    override fun onResume() {
        super.onResume()
        if (MyFunctions.isConnected(requireActivity())) {
            val scope = CoroutineScope(Dispatchers.IO)

            scope.launch(Dispatchers.Main) {

                getLeaveDetails()


            }
        } else {
            Toast.makeText(
                requireActivity(),
                getString(R.string.please_turn_on_your_internet_connection),
                Toast.LENGTH_SHORT
            )
                .show()
        }

    }

    private suspend fun getLeaveDetails() {
        leaveList.clear()
      //  MyFunctions.progressDialogShow(requireActivity())
        context?.let { viewModel.getLeaveDetails(token, it) }
        //ViewModel.getFollowUpList((token))

        try {
      //      MyFunctions.progressDialogDismiss()
            val data = viewModel.LeaveResponse.value?.data
            val response = viewModel.LeaveResponse.value?.data

            if (viewModel.LeaveResponse.value!= null && response!!.isNotEmpty()) {
                binding.container.visibility = View.VISIBLE
                binding.noData.visibility = View.GONE
                leaveList = response as ArrayList<LeaveDetails>
            }
            else {
                binding.container.visibility = View.GONE
                binding.noData.visibility = View.VISIBLE
            }

            leaveListAdapter = LeaveListAdapter(requireActivity(),leaveList, object : LeaveListAdapter.LeaveListItemSelectListener{
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
          //  MyFunctions.progressDialogDismiss()
            e.printStackTrace()
            binding.container.visibility = View.GONE
            binding.noData.visibility = View.VISIBLE
        }
    }


}