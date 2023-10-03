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
import com.tt.skolarrs.databinding.FragmentReportBinding
import com.tt.skolarrs.model.response.ReportData
import com.tt.skolarrs.utilz.Constant
import com.tt.skolarrs.utilz.MyFunctions
import com.tt.skolarrs.view.activity.MainActivity
import com.tt.skolarrs.view.adapter.ReportListAdapter
import com.tt.skolarrs.viewmodel.ReportResponseViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ReportFragment : Fragment() {

    lateinit var binding: FragmentReportBinding
    var intentType: String = ""

    var token: String = "0"
    private lateinit var reportListAdapter : ReportListAdapter
    private lateinit var viewModel: ReportResponseViewModel
    private var reportDataArrayList = ArrayList<ReportData>()

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentReportBinding.inflate(inflater, container, false)
        val view = binding.root


        (requireActivity() as MainActivity).title = MyFunctions.changeToProperCase(getString(R.string.report))

        token = MyFunctions.getSharedPreference(requireActivity(), Constant.TOKEN, Constant.TOKEN)!!
        viewModel = ViewModelProvider(this)[ReportResponseViewModel::class.java]

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val filterIcon = requireActivity().findViewById<ImageView>(R.id.filterIcon)

        filterIcon.visibility = View.GONE


    }

    override fun onResume() {
        super.onResume()
        if (MyFunctions.isConnected(requireActivity())) {
            val scope = CoroutineScope(Dispatchers.IO)
            scope.launch(Dispatchers.Main) {

                getReports()


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

    private suspend fun getReports() {
        reportDataArrayList.clear()
        MyFunctions.progressDialogShow(requireActivity())
        viewModel.getReportList(token, context)
        //ViewModel.getFollowUpList((token))

        try {
            MyFunctions.progressDialogDismiss()
            val data = viewModel.response.value?.data
            val response = viewModel.response.value?.data

            if (viewModel.response.value!= null && response!!.isNotEmpty()) {
                binding.reportList.visibility = View.VISIBLE
                binding.noData.visibility = View.GONE
                reportDataArrayList = response as ArrayList<ReportData>
            }
            else {
                binding.reportList.visibility = View.GONE
                binding.noData.visibility = View.VISIBLE
            }

            reportListAdapter = ReportListAdapter(requireActivity(),reportDataArrayList, object : ReportListAdapter.ReportListItemSelectListener{
                override fun onSelect(position: Int) {
                    TODO("Not yet implemented")
                }

                override fun unSelect(position: Int) {
                    TODO("Not yet implemented")
                }
            })

            binding.reportList.adapter = reportListAdapter


        }
        catch (e:java.lang.Exception) {
            MyFunctions.progressDialogDismiss()
            e.printStackTrace()
            binding.reportList.visibility = View.GONE
            binding.noData.visibility = View.VISIBLE
        }
    }
    }
