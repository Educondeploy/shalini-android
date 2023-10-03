package com.tt.skolarrs.view.activity

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.tt.skolarrs.R
import com.tt.skolarrs.databinding.ActivityLeaveApplyBinding
import com.tt.skolarrs.utilz.Constant
import com.tt.skolarrs.utilz.MyFunctions
import com.tt.skolarrs.viewmodel.LeaveViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LeaveApplyActivity : AppCompatActivity() {

    lateinit var binding: ActivityLeaveApplyBinding
    var year = 0
    var month: Int = 0
    var day: Int = 0
    lateinit var viewModel: LeaveViewModel
    lateinit var token: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLeaveApplyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.include.backArrow.setOnClickListener(View.OnClickListener {
            onBackPressed()
        })

        binding.include.title.text = getString(R.string.leave_apply)

        viewModel = ViewModelProvider(this)[LeaveViewModel::class.java]

        binding.fromDate.setOnClickListener(View.OnClickListener {
            val datePickerDialog = DatePickerDialog(
                this@LeaveApplyActivity,
                { datePicker, year, month, date ->
                    val formattedDate = String.format("%02d-%02d-%04d", date, month + 1, year)
                    binding.fromDate.text = formattedDate
                }, year, month, day
            )

            datePickerDialog.datePicker.minDate = System.currentTimeMillis()
            datePickerDialog.show()
        })

        binding.toDate.setOnClickListener(View.OnClickListener {
            val datePickerDialog = DatePickerDialog(
                this@LeaveApplyActivity,
                { datePicker, year, month, date ->
                    val formattedDate = String.format("%02d-%02d-%04d", date, month + 1, year)
                    binding.toDate.text = formattedDate
                }, year, month, day
            )

            datePickerDialog.datePicker.minDate = System.currentTimeMillis()
            datePickerDialog.show()
        })

        binding.submit.setOnClickListener(View.OnClickListener {
            if (isValid()) {
                if (MyFunctions.isConnected(this@LeaveApplyActivity)) {
                    val scope = CoroutineScope(Dispatchers.IO)
                    scope.launch(Dispatchers.Main) {
                        applyLeave()
                    }
                }else {
                    Toast.makeText(
                        this@LeaveApplyActivity,
                        getString(R.string.please_turn_on_your_internet_connection),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        })

    }

    private fun isValid(): Boolean {

        if (MyFunctions.isEmptyTextView(binding.fromDate,this@LeaveApplyActivity,getString(R.string.please_select_from_date), layoutInflater)) return false;
        if (MyFunctions.isEmptyTextView(binding.toDate,this@LeaveApplyActivity,getString(R.string.please_select_to_date), layoutInflater)) return false;
        if (MyFunctions.isEmpty(binding.reason,this@LeaveApplyActivity)) return false
        if (MyFunctions.isEmpty(binding.message,this@LeaveApplyActivity)) return false

        return true
    }

    suspend fun applyLeave() {
        MyFunctions.progressDialogShow(this@LeaveApplyActivity)
        token =
            MyFunctions.getSharedPreference(
                this@LeaveApplyActivity,
                Constant.TOKEN,
                Constant.TOKEN
            )!!

        viewModel.applyLeave(
            token,
            binding.fromDate.text.toString().trim(),
            binding.toDate.text.toString().trim(),
            binding.reason.text.toString().trim(),
            binding.message.text.toString().trim(), applicationContext
        )

        val response = viewModel.response
        val failes = viewModel.failedResponse
        try {
            MyFunctions.progressDialogDismiss()
            if (response.value != null) {
                Toast.makeText(
                    this@LeaveApplyActivity,
                    response.value!!.message,
                    Toast.LENGTH_SHORT
                )
                    .show()

                finish()
            } else {
                val responseError = viewModel.failedResponse.value!!
                Toast.makeText(this@LeaveApplyActivity, responseError, Toast.LENGTH_SHORT).show()
            }

        } catch (e: Exception) {
            MyFunctions.progressDialogDismiss()
            val responseError = viewModel.failedResponse.value!!
            Toast.makeText(this@LeaveApplyActivity, responseError, Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }


}