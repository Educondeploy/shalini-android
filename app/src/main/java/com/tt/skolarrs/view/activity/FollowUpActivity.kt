package com.tt.skolarrs.view.activity

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.tt.skolarrs.R
import com.tt.skolarrs.databinding.ActivityFollowUpBinding
import com.tt.skolarrs.utilz.Constant
import com.tt.skolarrs.utilz.MyFunctions
import com.tt.skolarrs.viewmodel.FollowUpUpdateViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*


class FollowUpActivity : AppCompatActivity() {

    lateinit var binding: ActivityFollowUpBinding
    var year = 0
    var month: Int = 0
    var day: Int = 0
    var token: String = ""

    lateinit var viewModel: FollowUpUpdateViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFollowUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.include.title.text = getString(R.string.follow_up)

        token =
            MyFunctions.getSharedPreference(this@FollowUpActivity, Constant.TOKEN, Constant.TOKEN)!!


        viewModel = ViewModelProvider(this)[FollowUpUpdateViewModel::class.java]

        binding.include.backArrow.visibility = View.GONE

        binding.include.backArrow.setOnClickListener(View.OnClickListener {
            onBackPressed()
        })

        binding.submit.setOnClickListener(View.OnClickListener {
            if (isValid()) {
                if (MyFunctions.isConnected(this@FollowUpActivity)) {
                    val scope = CoroutineScope(Dispatchers.IO)
                    scope.launch(Dispatchers.Main) {
                        updateStatus()
                    }
                } else {
                    Toast.makeText(
                        this@FollowUpActivity,
                        getString(R.string.please_turn_on_your_internet_connection),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })

        binding.date.setOnClickListener(View.OnClickListener {
            val datePickerDialog = DatePickerDialog(
                this@FollowUpActivity,
                { datePicker, year, month, date ->
                    val formattedDate = String.format("%02d-%02d-%04d", date, month + 1, year)
                    binding.date.text = formattedDate
                }, year, month, day
            )
            datePickerDialog.datePicker.minDate = System.currentTimeMillis()
            datePickerDialog.show()
        })


        /* binding.date.setOnClickListener(View.OnClickListener {
             val datePickerDialog = DatePickerDialog(
                 this@FollowUpActivity,
                 { datePicker, year, month, date ->
                     binding.date.setText(date.toString() + "-" + (month + 1) + "-" + year)
                 }, year, month, day
             )

             datePickerDialog.datePicker.minDate = System.currentTimeMillis()
             datePickerDialog.show()

         })*/

        binding.time.setOnClickListener(View.OnClickListener {

            val timePicker: TimePickerDialog =
                TimePickerDialog(this, timePickerDialogListener, 12, 0, false)
            timePicker.show()
        })


    }

    private fun isValid(): Boolean {
        if (MyFunctions.isEmptyTextView(
                binding.date,
                this@FollowUpActivity,
                getString(R.string.please_select_the_date),
                layoutInflater
            )
        ) return false;
        if (MyFunctions.isEmptyTextView(
                binding.time,
                this@FollowUpActivity,
                getString(R.string.please_select_the_time),
                layoutInflater
            )
        ) return false;

        return true
    }

    private suspend fun updateStatus() {
        MyFunctions.progressDialogShow(this@FollowUpActivity)
        val leadId = intent.getStringExtra(Constant.LEAD_ID)!!
        val leadStatus = intent.getStringExtra(Constant.LEAD_STATUS)!!
        val userId = MyFunctions.getSharedPreference(
            this@FollowUpActivity,
            Constant.USER_ID,
            Constant.USER_ID
        )!!
        val id =
            MyFunctions.getSharedPreference(this@FollowUpActivity, Constant.ID, Constant.ID)!!

        viewModel.updateFollowUpStatus(
            token,
            id,
            leadId,
            userId,
            binding.date.text.toString(),
            binding.time.text.toString(),
            binding.followUpType.selectedItem.toString().lowercase(),
            leadStatus, applicationContext
        )

        val response = viewModel.response
        val failes = viewModel.responseError

        try {
            MyFunctions.progressDialogDismiss()
            if (response.value != null) {
                MyFunctions.setSharedPreference(this, Constant.LEAD_ID, "")
                MyFunctions.setSharedPreference(this, Constant.LEAD_NUMBER, "")
                MyFunctions.setSharedPreference(this, Constant.LEAD_NAME, "")
                MyFunctions.setSharedPreference(this, Constant.LEAD_TYPE, "")
                MyFunctions.setSharedPreference(this, Constant.LEAD_STATUS, "")
                MyFunctions.setSharedPreference(this, Constant.DURATION, "")

                Toast.makeText(this@FollowUpActivity, response.value!!.message, Toast.LENGTH_SHORT)
                    .show()
                updateRecordedFile();
                startActivity(Intent(this@FollowUpActivity, FollowUpListActivity::class.java))
                finish()
            } else {
                val responseError = viewModel.responseError.value!!
                Toast.makeText(this@FollowUpActivity, responseError, Toast.LENGTH_SHORT).show()
            }

        } catch (e: Exception) {
            if (viewModel.responseError.value != null) {
                val responseError = viewModel.responseError.value!!
                Toast.makeText(this@FollowUpActivity, responseError, Toast.LENGTH_SHORT).show()
            }
            Log.d("TAG", "getLeads: " + e.message)
            e.printStackTrace()
        } catch (e: java.lang.NullPointerException) {
            e.message
            Log.d("TAG", "getLeads: " + e.message)

        }

    }

    private suspend fun updateRecordedFile() {

    }

    private val timePickerDialogListener: TimePickerDialog.OnTimeSetListener =
        TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->

            val formattedHour = String.format("%02d", if (hourOfDay == 12) 12 else hourOfDay % 12)
            val formattedMinute = String.format("%02d", minute)
            val amPm = if (hourOfDay < 12) "AM" else "PM"
            val formattedTime = "$formattedHour:$formattedMinute $amPm"
            binding.time.text = formattedTime

        }

}



