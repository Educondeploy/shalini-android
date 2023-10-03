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
import com.tt.skolarrs.databinding.FragmentCalenderViewBinding
import com.tt.skolarrs.model.response.CalenderData
import com.tt.skolarrs.utilz.Constant
import com.tt.skolarrs.utilz.MyFunctions
import com.tt.skolarrs.view.activity.MainActivity
import com.tt.skolarrs.view.adapter.CalenderListAdapter
import com.tt.skolarrs.viewmodel.CalenderViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class CalenderViewFragment : Fragment() {
    lateinit var binding: FragmentCalenderViewBinding

    lateinit var viewModel : CalenderViewModel
    lateinit var token : String
    private var calenderData = ArrayList<CalenderData>()
    private lateinit var calenderListAdapter: CalenderListAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                startActivity(Intent(requireActivity(), MainActivity::class.java))
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
        binding = FragmentCalenderViewBinding.inflate(inflater,container, false)
        val view = binding.root

        (requireActivity() as MainActivity).title = MyFunctions.changeToProperCase(getString(R.string.calender_view))

        token = MyFunctions.getSharedPreference(requireActivity(), Constant.TOKEN, Constant.TOKEN)!!
        viewModel = ViewModelProvider(this)[CalenderViewModel::class.java]

        return view
    }

    override fun onResume() {
        super.onResume()
        if (MyFunctions.isConnected(requireActivity())) {
            val scope = CoroutineScope(Dispatchers.IO)
            scope.launch(Dispatchers.Main) {

                getCalenderDetails()


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

    private suspend fun getCalenderDetails() {
        calenderData.clear()
        MyFunctions.progressDialogShow(requireActivity())
        viewModel.getCalenderList(token, context)
        //ViewModel.getFollowUpList((token))

        try {
            MyFunctions.progressDialogDismiss()
            val data = viewModel.response.value?.data
            val response = viewModel.response.value?.data

            if (viewModel.response.value!= null && response!!.isNotEmpty()) {
                binding.container.visibility = View.VISIBLE
                binding.noData.visibility = View.GONE
                calenderData = response as ArrayList<CalenderData>
            }
            else {
                binding.container.visibility = View.GONE
                binding.noData.visibility = View.VISIBLE
            }

            calenderListAdapter = CalenderListAdapter(requireActivity(),calenderData, object : CalenderListAdapter.CalenderItemSelectListener{
                override fun onSelect(position: Int) {
                    TODO("Not yet implemented")
                }

                override fun unSelect(position: Int) {
                    TODO("Not yet implemented")
                }
            })

            binding.leaveList.adapter = calenderListAdapter


        }
        catch (e:java.lang.Exception) {
            MyFunctions.progressDialogDismiss()
            e.printStackTrace()
            binding.container.visibility = View.GONE
            binding.noData.visibility = View.VISIBLE
        }
    }

}