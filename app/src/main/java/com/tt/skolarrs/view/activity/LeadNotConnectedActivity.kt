package com.tt.skolarrs.view.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.tt.skolarrs.R
import com.tt.skolarrs.databinding.ActivityLeadNotConnectedBinding
import com.tt.skolarrs.utilz.Constant
import com.tt.skolarrs.utilz.MyFunctions
import com.tt.skolarrs.viewmodel.LeadStatusResponseViewModel
import com.tt.skolarrs.viewmodel.UpdateLeadStatusViewModel
import com.tt.skolarrs.viewmodel.UploadLeadCallRecord
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class LeadNotConnectedActivity : AppCompatActivity() {

    lateinit var binding: ActivityLeadNotConnectedBinding

    var newStatus: String = ""
    lateinit var status: String
    var duration: String = "00:00:00"
    private lateinit var selectedRadioButton: RadioButton
    lateinit var viewModel: LeadStatusResponseViewModel
    lateinit var updateViewModel: UpdateLeadStatusViewModel
    var selectedPosition: Int = 0
    var token: String = ""
    var leadId: String = ""
    var selectedRadioButtonId: Int = -1

    lateinit var uploadLeadCallRecord: UploadLeadCallRecord

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLeadNotConnectedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.include.title.text = getString(R.string.lead_not_connected)
        binding.include.backArrow.visibility = View.GONE

        token = MyFunctions.getSharedPreference(
            this@LeadNotConnectedActivity,
            Constant.TOKEN,
            Constant.TOKEN
        )!!

        /* duration = MyFunctions.getSharedPreference(this, Constant.DURATION, Constant.DURATION)!!*/

        leadId = MyFunctions.getSharedPreference(
            this,
            Constant.LEAD_ID,
            Constant.LEAD_ID
        )!!

        viewModel = ViewModelProvider(this)[LeadStatusResponseViewModel::class.java]
        updateViewModel = ViewModelProvider(this)[UpdateLeadStatusViewModel::class.java]
        uploadLeadCallRecord = ViewModelProvider(this)[UploadLeadCallRecord::class.java]

        binding.include.backArrow.setOnClickListener(View.OnClickListener {
            onBackPressed()
        })

        binding.submit.setOnClickListener(View.OnClickListener {

            selectedRadioButtonId = binding.intrestedRadioGroup.checkedRadioButtonId
            selectedRadioButton = findViewById(selectedRadioButtonId)
            status = selectedRadioButton.text.toString()

            if (selectedRadioButtonId == -1) {
                Toast.makeText(
                    this@LeadNotConnectedActivity,
                    getString(R.string.please_select_the_item),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                if (MyFunctions.isConnected(this@LeadNotConnectedActivity)) {
                    val scope = CoroutineScope(Dispatchers.IO)
                    scope.launch(Dispatchers.Main) {
                        updateStatus()
                    }
                } else {
                    Toast.makeText(
                        this@LeadNotConnectedActivity,
                        getString(R.string.please_turn_on_your_internet_connection),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        })


    }


    private fun getRecordedFiles(): List<File> {
        val storageDir = applicationContext?.cacheDir
        val recordedFiles = mutableListOf<File>()

        storageDir?.listFiles()?.let { files ->
            for (file in files) {
                if (file.isFile) {
                    recordedFiles.add(file)
                }
            }
        }
        return recordedFiles
    }


    private suspend fun storeRecordedFiles(reportID: String, LeadId: String) {
        if (getRecordedFiles().isNotEmpty()) {
            val uri: File = getRecordedFiles()[0]!!

            val userId = MyFunctions.getSharedPreference(
                this@LeadNotConnectedActivity,
                Constant.USER_ID,
                Constant.USER_ID
            )!!
            val file =
                MyFunctions.prepareFilePart("file", uri, this@LeadNotConnectedActivity)

            val leadId1 = MyFunctions.createPartFromString(LeadId)
            val userID = MyFunctions.createPartFromString(userId)
            val reportId = MyFunctions.createPartFromString(reportID)
            val callMode = MyFunctions.createPartFromString("outgoing")

            val map = HashMap<String, RequestBody>()
            map["leadId"] = leadId1!!
            map["reportId"] = reportId!!
            map["userId"] = userID!!
            map["callMode"] = callMode!!

            uploadLeadCallRecord.storeRecordedFile(token, map, file!!, applicationContext)

            val response = uploadLeadCallRecord.response
            val failes = uploadLeadCallRecord.responseError


            try {
                MyFunctions.progressDialogDismiss()
                if (response.value != null) {
                    Log.d("TAG", "storeRecordedFiles: " + response.value)
                    MyFunctions.deleteCache(this@LeadNotConnectedActivity)
                    Toast.makeText(
                        this@LeadNotConnectedActivity,
                        response.value!!.message,
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    startActivity(
                        Intent(
                            this@LeadNotConnectedActivity,
                            ShareActivity::class.java
                        ).putExtra(Constant.LEAD_ID, leadId)
                            .putExtra(Constant.LEAD_STATUS, newStatus)
                            .putExtra(Constant.DURATION, duration)
                    )

                    /* startActivity(
                         Intent(
                             this@LeadNotConnectedActivity,
                             FollowUpActivity::class.java
                         ).putExtra(Constant.LEAD_ID, leadId).putExtra(Constant.LEAD_STATUS,newStatus)
                     )*/
                    finish()

                } else {
                    val responseError = uploadLeadCallRecord.responseError.value!!
                    Toast.makeText(this@LeadNotConnectedActivity, responseError, Toast.LENGTH_SHORT)
                        .show()
                }

            } catch (e: Exception) {
                MyFunctions.progressDialogDismiss()
//            val responseError = uploadLeadCallRecord.responseError.value!!
//            Toast.makeText(this@LeadNotConnectedActivity, responseError, Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            } catch (e: java.lang.NullPointerException) {
                MyFunctions.progressDialogDismiss()
                e.message
            }
        } else {
            Toast.makeText(
                this@LeadNotConnectedActivity,
                "success",
                Toast.LENGTH_SHORT
            )
                .show()
            startActivity(
                Intent(
                    this@LeadNotConnectedActivity,
                    ShareActivity::class.java
                ).putExtra(Constant.LEAD_ID, leadId).putExtra(Constant.LEAD_STATUS, newStatus)
                    .putExtra(Constant.DURATION, duration)
            )
            finish()
        }

    }

    private suspend fun updateLeadStatus() {
        // MyFunctions.progressDialogShow(this@LeadNotConnectedActivity)

        val userId = MyFunctions.getSharedPreference(
            this@LeadNotConnectedActivity,
            Constant.USER_ID,
            Constant.USER_ID
        )!!
        val id = MyFunctions.getSharedPreference(
            this@LeadNotConnectedActivity,
            Constant.ID,
            Constant.ID
        )!!


        val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy")
        val currentDate: String = simpleDateFormat.format(Date())

        viewModel.getLeadStatus(
            token,
            id,
            leadId,
            duration,
            newStatus,
            userId,
            "",
            currentDate,
            "",
            applicationContext
        )

        val response = viewModel.response
        val failes = viewModel.responseError

        try {
            MyFunctions.progressDialogDismiss()
            if (response.value != null) {
                //     storeRecordedFiles(response!!.value!!.data._id, response.value!!.data.leadId)

                //Need to hide while adding store record file api
                if (response.value != null) {
                    Log.d("TAG", "storeRecordedFiles: " + response.value)
                    MyFunctions.deleteCache(this@LeadNotConnectedActivity)
                    Toast.makeText(
                        this@LeadNotConnectedActivity,
                        response.value!!.message,
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    startActivity(
                        Intent(
                            this@LeadNotConnectedActivity,
                            ShareActivity::class.java
                        ).putExtra(Constant.LEAD_ID, leadId)
                            .putExtra(Constant.LEAD_STATUS, newStatus)
                            .putExtra(Constant.DURATION, duration)
                    )

                    /* startActivity(
                         Intent(
                             this@LeadNotConnectedActivity,
                             FollowUpActivity::class.java
                         ).putExtra(Constant.LEAD_ID, leadId).putExtra(Constant.LEAD_STATUS,newStatus)
                     )*/
                    finish()

                } else {
                    val responseError = uploadLeadCallRecord.responseError.value!!
                    Toast.makeText(this@LeadNotConnectedActivity, responseError, Toast.LENGTH_SHORT)
                        .show()
                }

            } else {
                val responseError = viewModel.responseError.value!!
                Toast.makeText(this@LeadNotConnectedActivity, responseError, Toast.LENGTH_SHORT)
                    .show()
            }

        } catch (e: Exception) {
            MyFunctions.progressDialogDismiss()
            val responseError = viewModel.responseError.value!!
            Toast.makeText(this@LeadNotConnectedActivity, responseError, Toast.LENGTH_SHORT).show()
            Log.d("TAG", "getLeads: " + e.message)
            e.printStackTrace()
        } catch (e: java.lang.NullPointerException) {
            MyFunctions.progressDialogDismiss()
            e.message
            Log.d("TAG", "getLeads: " + e.message)

        }

    }


    private suspend fun updateStatus() {
        MyFunctions.progressDialogShow(this@LeadNotConnectedActivity)

        val previousLead = MyFunctions.getSharedPreference(
            this@LeadNotConnectedActivity,
            Constant.LEAD_TYPE,
            Constant.LEAD_TYPE
        )!!
        val id =
            MyFunctions.getSharedPreference(
                this@LeadNotConnectedActivity,
                Constant.ID,
                Constant.ID
            )!!

        when (status) {
            getString(R.string.out_of_coverage) -> {
                newStatus = "outofCoverage"
            }
            getString(R.string.invalid_number) -> {
                newStatus = "invalidNumber"
            }
            getString(R.string.did_not_pick) -> {
                newStatus = "didNotPick"
            }
            getString(R.string.busy_on_another_call) -> {
                newStatus = "busyOnOtherCall"
            }
            getString(R.string.switchoff) -> {
                newStatus = "switchOff"
            }
        }

        updateViewModel.UpdateLeadStatus(
            token,
            newStatus, leadId, previousLead, applicationContext
        )


        val response = updateViewModel.response
        val failes = updateViewModel.responseError

        try {
            // MyFunctions.progressDialogDismiss()
            if (response.value != null) {
                /* Toast.makeText(
                     this@LeadNotConnectedActivity,
                     response.value!!.message,
                     Toast.LENGTH_SHORT
                 )
                     .show()*/


                updateLeadStatus()


                /* if (s == Constant.Connected) {
                     startActivity(
                         Intent(
                             this@LeadConnectedActivity,
                             LeadConnectedActivity::class.java
                         ).putExtra(Constant.LEAD_ID, lead_id).putExtra(Constant.DURATION, duration)
                     )
                     finish()
                 } else if (s == Constant.Connected) {
                     startActivity(
                         Intent(
                             this@LeadConnectedActivity,
                             LeadConnectedActivity::class.java
                         ).putExtra(Constant.LEAD_ID, lead_id).putExtra(Constant.DURATION, duration)
                     )
                     finish()
                 }
                 finish()*/
                // startActivity(Intent(this@LeadResponseActivity, MainActivity::class.java))
                // finish()
            } else {
                val responseError = updateViewModel.responseError.value!!
                Toast.makeText(this@LeadNotConnectedActivity, responseError, Toast.LENGTH_SHORT)
                    .show()
            }

        } catch (e: Exception) {
            MyFunctions.progressDialogDismiss()
            val responseError = updateViewModel.responseError.value!!
            Toast.makeText(this@LeadNotConnectedActivity, responseError, Toast.LENGTH_SHORT).show()
            Log.d("TAG", "getLeads: " + e.message)
            e.printStackTrace()
        } catch (e: java.lang.NullPointerException) {
            MyFunctions.progressDialogDismiss()
            e.message
            Log.d("TAG", "getLeads: " + e.message)

        }


    }

}



