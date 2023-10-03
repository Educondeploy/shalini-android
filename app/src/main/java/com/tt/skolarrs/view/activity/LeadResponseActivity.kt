package com.tt.skolarrs.view.activity

import android.content.Intent
import android.os.Bundle
import android.provider.CallLog
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.tt.skolarrs.R
import com.tt.skolarrs.databinding.ActivityLeadResponseBinding
import com.tt.skolarrs.utilz.Constant
import com.tt.skolarrs.utilz.MyFunctions
import com.tt.skolarrs.viewmodel.FollowUpUpdateViewModel
import com.tt.skolarrs.viewmodel.UpdateLeadStatusViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LeadResponseActivity : AppCompatActivity() {

    lateinit var binding: ActivityLeadResponseBinding
    var lead_id: String = ""
    var token: String = ""
    var duration: String = "0"
    lateinit var viewModel: UpdateLeadStatusViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLeadResponseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.include.title.text = getString(R.string.lead)

        viewModel = ViewModelProvider(this)[UpdateLeadStatusViewModel::class.java]

        binding.include.backArrow.setOnClickListener(View.OnClickListener {
            onBackPressed()
        })

        if (MyFunctions.getSharedPreference(this,Constant.DURATION,Constant.DURATION)!= null) {
            duration = MyFunctions.getSharedPreference(this,Constant.DURATION,Constant.DURATION)!!
        }


        lead_id = MyFunctions.getSharedPreference(
            this@LeadResponseActivity,
            Constant.LEAD_ID,
            Constant.LEAD_ID
        )!!
        token = MyFunctions.getSharedPreference(
            this@LeadResponseActivity,
            Constant.TOKEN,
            Constant.TOKEN
        )!!
        Log.d("TAG", "onCreate: " + lead_id)


        binding.connected.setOnClickListener(View.OnClickListener {
            /*val scope = CoroutineScope(Dispatchers.IO)
            scope.launch(Dispatchers.Main) {
                updateStatus(Constant.Connected)
            }*/
            startActivity(
                Intent(
                    this@LeadResponseActivity,
                    LeadConnectedActivity::class.java
                )
            )
            finish()

        })
        binding.notConnected.setOnClickListener(View.OnClickListener {
           /* val scope = CoroutineScope(Dispatchers.IO)
            scope.launch(Dispatchers.Main) {
                updateStatus(Constant.Not_Connected)
            }*/
            startActivity(
                Intent(
                    this@LeadResponseActivity,
                    LeadNotConnectedActivity::class.java
                )
            )
            finish()
        })

    }

    override fun onResume() {
        super.onResume()
        //getCallDuration("9751665327")
        getLastCallDuration()
    }


    fun getCallDuration(phoneNumber: String): String? {
        val selection =
            "${CallLog.Calls.NUMBER} = ? AND ${CallLog.Calls.TYPE} IN (${CallLog.Calls.INCOMING_TYPE},${CallLog.Calls.OUTGOING_TYPE}) AND ${CallLog.Calls.DURATION} IS NOT NULL"
        val selectionArgs = arrayOf(phoneNumber)
        val cursor = contentResolver.query(
            CallLog.Calls.CONTENT_URI,
            arrayOf(CallLog.Calls.DURATION),
            selection,
            selectionArgs,
            "${CallLog.Calls.DATE}"
        )
        if (cursor != null && cursor.moveToFirst()) {
            val durationIndex = cursor.getColumnIndex(CallLog.Calls.DURATION)
            val duration = cursor.getInt(durationIndex)
            cursor.close()
            return duration.toString()
        }
        return null

    }


    private fun getLastCallDuration(): String? {
        var duration = ""
        try {
            val cursor = contentResolver.query(
                CallLog.Calls.CONTENT_URI,
                null,
                null,
                null,
                CallLog.Calls.DATE + " DESC"
            )
            if (cursor != null && cursor.moveToFirst()) {
                val durationIndex = cursor.getColumnIndex(CallLog.Calls.DURATION)
                duration = cursor.getString(durationIndex)
                cursor.close()
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
        return duration
    }


}