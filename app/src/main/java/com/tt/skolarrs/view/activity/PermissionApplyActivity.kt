package com.tt.skolarrs.view.activity

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.DatePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.tt.skolarrs.R
import com.tt.skolarrs.databinding.ActivityPermissionApplyBinding
import com.tt.skolarrs.utilz.Constant
import com.tt.skolarrs.utilz.MyFunctions
import com.tt.skolarrs.viewmodel.PermissionViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class PermissionApplyActivity : AppCompatActivity() {

    lateinit var binding: ActivityPermissionApplyBinding
    var year = 0
    var month: Int = 0
    var day: Int = 0
    lateinit var viewModel: PermissionViewModel
    lateinit var token: String
    var selectedYear: Int = 0
    var selectedMonth: Int = 0
    var selectedDay: Int = 0
    var currentYear : Int = 0
    var currentMonth: Int = 0
    var currentDay: Int = 0
    var currentHour: Int = 0
    var currentMinute : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPermissionApplyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val calendar = Calendar.getInstance()
        currentYear = calendar.get(Calendar.YEAR)
        currentMonth = calendar.get(Calendar.MONTH)
        currentDay = calendar.get(Calendar.DAY_OF_MONTH)
        currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        currentMinute = calendar.get(Calendar.MINUTE)

// check if the selected date is today


        binding.include.backArrow.setOnClickListener(View.OnClickListener {
            onBackPressed()
        })

        binding.include.title.text = getString(R.string.permission_apply)

        viewModel = ViewModelProvider(this)[PermissionViewModel::class.java]

        binding.date.setOnClickListener(View.OnClickListener {
            val datePickerDialog = DatePickerDialog(
                this@PermissionApplyActivity,
                { datePicker, year, month, date ->
                    val formattedDate = String.format("%02d-%02d-%04d", date, month + 1, year)
                    selectedYear = datePicker.year
                    selectedMonth = datePicker.month
                    selectedDay = datePicker.dayOfMonth

                    binding.date.text = formattedDate
                }, year, month, day
            )

            datePickerDialog.datePicker.minDate = System.currentTimeMillis()
            datePickerDialog.show()
        })

        binding.fromTime.setOnClickListener(View.OnClickListener {

            if (selectedYear == currentYear && selectedMonth == currentMonth && selectedDay == currentDay) {
                // if the selected date is today, set the time picker to the current time
                val timePicker = TimePickerDialog(this, fromTimePickerDialogListener, currentHour, currentMinute, false)
                timePicker.show()
            } else {
                // if the selected date is a future date, set the time picker to 12:00
                val timePicker = TimePickerDialog(this, fromTimePickerDialogListener, 12, 0, false)
                timePicker.show()
            }

          /*  val timePicker: TimePickerDialog =
                TimePickerDialog(this, fromTimePickerDialogListener, 12, 0, false)
            timePicker.show()*/
        })

        binding.toTime.setOnClickListener(View.OnClickListener {

            if (selectedYear == currentYear && selectedMonth == currentMonth && selectedDay == currentDay) {
                // if the selected date is today, set the time picker to the current time
                val timePicker = TimePickerDialog(this, toTimePickerDialogListener, currentHour, currentMinute, false)
                timePicker.show()
            } else {
                // if the selected date is a future date, set the time picker to 12:00
                val timePicker = TimePickerDialog(this, toTimePickerDialogListener, 12, 0, false)
                timePicker.show()
            }

         /*   val timePicker: TimePickerDialog =
                TimePickerDialog(this, toTimePickerDialogListener, 12, 0, false)
            timePicker.show()*/
        })



        binding.submit.setOnClickListener(View.OnClickListener {
            if (isValid()) {
                if (MyFunctions.isConnected(this@PermissionApplyActivity)) {
                    val scope = CoroutineScope(Dispatchers.IO)
                    scope.launch(Dispatchers.Main) {
                        applyPermission()
                    }
                } else {
                    Toast.makeText(
                        this@PermissionApplyActivity,
                        getString(R.string.please_turn_on_your_internet_connection),
                        Toast.LENGTH_SHORT
                    ).show()

                }
            }

        })

    }

    private fun isValid(): Boolean {
        if (MyFunctions.isEmptyTextView(
                binding.date,
                this@PermissionApplyActivity,
                getString(R.string.please_select_date),
                layoutInflater
            )
        ) return false;
        if (MyFunctions.isEmptyTextView(
                binding.fromTime,
                this@PermissionApplyActivity,
                getString(R.string.please_select_fromTime),
                layoutInflater
            )
        ) return false;
        if (MyFunctions.isEmptyTextView(
                binding.toTime,
                this@PermissionApplyActivity,
                getString(R.string.please_select_toTime),
                layoutInflater
            )
        ) return false; if (MyFunctions.isEmpty(
                binding.reason,
                this@PermissionApplyActivity
            )
        ) return false; if (MyFunctions.isEmpty(
                binding.message,
                this@PermissionApplyActivity,
            )
        ) return false;




        return true
    }

    suspend fun applyPermission() {
        MyFunctions.progressDialogShow(this@PermissionApplyActivity)
        token =
            MyFunctions.getSharedPreference(
                this@PermissionApplyActivity,
                Constant.TOKEN,
                Constant.TOKEN
            )!!

        viewModel.applyPermission(
            token,
            binding.date.text.toString().trim(),
            binding.fromTime.text.toString().trim(),
            binding.toTime.text.toString().trim(),
            binding.reason.text.toString().trim(),
            binding.message.text.toString().trim(), applicationContext
        )

        val response = viewModel.response
        val failes = viewModel.failedResponse
        try {
            MyFunctions.progressDialogDismiss()
            if (response.value != null) {
                Toast.makeText(
                    this@PermissionApplyActivity,
                    response.value!!.message,
                    Toast.LENGTH_SHORT
                )
                    .show()

                finish()
            } else {
                val responseError = viewModel.failedResponse.value!!
                Toast.makeText(this@PermissionApplyActivity, responseError, Toast.LENGTH_SHORT)
                    .show()
            }

        } catch (e: Exception) {
            MyFunctions.progressDialogDismiss()
            val responseError = viewModel.failedResponse.value!!
            Toast.makeText(this@PermissionApplyActivity, responseError, Toast.LENGTH_SHORT).show()
            Log.d("TAG", "getLeads: " + e.message)
            e.printStackTrace()
        }
    }

    private val fromTimePickerDialogListener: TimePickerDialog.OnTimeSetListener =
        TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute -> // logic to properly handle
            val formattedHour = String.format("%02d", if (hourOfDay == 12) 12 else hourOfDay % 12)
            val formattedMinute = String.format("%02d", minute)
            val amPm = if (hourOfDay < 12) "AM" else "PM"
            val formattedTime = "$formattedHour:$formattedMinute $amPm"
            binding.fromTime.text = formattedTime

        }

    private val toTimePickerDialogListener: TimePickerDialog.OnTimeSetListener =
        TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute -> // logic to properly handle


            val formattedHour = String.format("%02d", if (hourOfDay == 12) 12 else hourOfDay % 12)
            val formattedMinute = String.format("%02d", minute)
            val amPm = if (hourOfDay < 12) "AM" else "PM"
            val formattedTime = "$formattedHour:$formattedMinute $amPm"
            binding.toTime.text = formattedTime

        }

}