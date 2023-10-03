package com.tt.skolarrs.view.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.tt.skolarrs.R
import com.tt.skolarrs.databinding.ActivityFollowUp2Binding
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
import java.util.HashMap
import kotlin.math.log

class FollowUpActivity2 : AppCompatActivity() {

    lateinit var binding: ActivityFollowUp2Binding
    var status: String = ""
    var duration: String = ""
    var lead_id: String = ""
    var previous_status: String = ""
    var number: String = ""
    var name: String = ""


    lateinit var viewModel: LeadStatusResponseViewModel
    lateinit var updateViewModel: UpdateLeadStatusViewModel
    lateinit var uploadLeadCallRecord: UploadLeadCallRecord
    var token: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFollowUp2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.include.backArrow.visibility = View.GONE
        binding.include.title.text = getString(R.string.follow_up)


        token =
            MyFunctions.getSharedPreference(
                this@FollowUpActivity2,
                Constant.TOKEN,
                Constant.TOKEN
            )!!
        lead_id =
            MyFunctions.getSharedPreference(
                this@FollowUpActivity2,
                Constant.LEAD_ID,
                Constant.LEAD_ID
            )!!
        duration =
            MyFunctions.getSharedPreference(
                this@FollowUpActivity2,
                Constant.DURATION,
                Constant.DURATION
            )!!
        previous_status =
            MyFunctions.getSharedPreference(
                this@FollowUpActivity2,
                Constant.LEAD_TYPE,
                Constant.LEAD_TYPE
            )!!
        number =
            MyFunctions.getSharedPreference(
                this@FollowUpActivity2,
                Constant.LEAD_NUMBER,
                Constant.LEAD_NUMBER
            )!!
        name =
            MyFunctions.getSharedPreference(
                this@FollowUpActivity2,
                Constant.LEAD_NAME,
                Constant.LEAD_NAME
            )!!

        binding.number.text = number
        binding.callDuration.text = duration
        binding.name.text = name
        binding.status.text = previous_status

        viewModel = ViewModelProvider(this)[LeadStatusResponseViewModel::class.java]
        updateViewModel = ViewModelProvider(this)[UpdateLeadStatusViewModel::class.java]
        uploadLeadCallRecord = ViewModelProvider(this)[UploadLeadCallRecord::class.java]

        binding.submit.setOnClickListener(View.OnClickListener {
            val selectedRadioButtonId = binding.radioGroup1.checkedRadioButtonId

            if (binding.radio0.isChecked) {
                status = "cold"
            }
            if (binding.radio1.isChecked) {
                status = "notInterested"
            }
            if (binding.radio2.isChecked) {
                status = "didNotPick"
            }
            if (binding.radio3.isChecked) {
                status = "completed"
            }
            if (selectedRadioButtonId == -1) {
                Toast.makeText(
                    this@FollowUpActivity2,
                    getString(R.string.please_select_the_status),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                if (MyFunctions.isConnected(this@FollowUpActivity2)) {
                    val scope = CoroutineScope(Dispatchers.IO)
                    scope.launch(Dispatchers.Main) {
                        updateStatus()
                    }
                } else {
                    Toast.makeText(
                        this@FollowUpActivity2,
                        getString(R.string.please_turn_on_your_internet_connection),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })


    }


    private suspend fun updateLeadStatus() {
        // MyFunctions.progressDialogShow(this@LeadConnectedActivity)
        val userId = MyFunctions.getSharedPreference(
            this@FollowUpActivity2,
            Constant.USER_ID,
            Constant.USER_ID
        )!!
        val id = MyFunctions.getSharedPreference(this@FollowUpActivity2, Constant.ID, Constant.ID)!!

        viewModel.getLeadStatus(
            token,
            id,
            lead_id,
            duration,
            status,
            userId,
            binding.notes.text.toString().trim(),
            "",
            "", applicationContext
        )

        val response = viewModel.response
        val failes = viewModel.responseError

        try {
            MyFunctions.progressDialogDismiss()
            if (response.value != null) {
                // Toast.makeText(this@FollowUpActivity2, response.value!!.message, Toast.LENGTH_SHORT).show()
                storeRecordedFiles(response.value!!.data._id, response.value!!.data.leadId)

            } else {
                val responseError = viewModel.responseError.value!!
                Toast.makeText(this@FollowUpActivity2, responseError, Toast.LENGTH_SHORT).show()
            }

        } catch (e: Exception) {
            MyFunctions.progressDialogDismiss()
            val responseError = viewModel.responseError.value!!
            Toast.makeText(this@FollowUpActivity2, responseError, Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        } catch (e: java.lang.NullPointerException) {
            MyFunctions.progressDialogDismiss()
            e.message
        }

    }

    private suspend fun updateStatus() {
        MyFunctions.progressDialogShow(this@FollowUpActivity2)
        updateViewModel.UpdateLeadStatus(
            token,
            status, lead_id, previous_status, applicationContext
        )

        val response = updateViewModel.response
        val failes = updateViewModel.responseError

        try {
            //   MyFunctions.progressDialogDismiss()
            if (response.value != null) {
//                Toast.makeText(
//                    this@FollowUpActivity2,
//                    response.value!!.message,
//                    Toast.LENGTH_SHORT
//                )
//                    .show()

                updateLeadStatus()

            } else {
                MyFunctions.progressDialogDismiss()
                val responseError = updateViewModel.responseError.value!!
                Toast.makeText(this@FollowUpActivity2, responseError, Toast.LENGTH_SHORT).show()
            }

        } catch (e: Exception) {
            MyFunctions.progressDialogDismiss()
            val responseError = updateViewModel.responseError.value!!
            Toast.makeText(this@FollowUpActivity2, responseError, Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        } catch (e: java.lang.NullPointerException) {
            MyFunctions.progressDialogDismiss()
            e.message
        }


    }

    private fun getRecordedFiles(): List<File> {
        val storageDir = applicationContext?.cacheDir
        val recordedFiles = mutableListOf<File>()

        return if (storageDir !== null && storageDir?.listFiles() !== null) {
            storageDir?.listFiles()?.let { files ->
                for (file in files) {
                    if (file.isFile) {
                        recordedFiles.add(file)
                    }
                }
            }
            recordedFiles
        } else {
            recordedFiles
        }
    }

    private suspend fun storeRecordedFiles(reportID: String, LeadId: String) {
        Log.d("TAG", "storeRecordedFiles: " + getRecordedFiles())
        if (getRecordedFiles().isNotEmpty()) {
            val uri: File = getRecordedFiles()[0]!!

            val userId = MyFunctions.getSharedPreference(
                this@FollowUpActivity2,
                Constant.USER_ID,
                Constant.USER_ID
            )!!
            val file =
                MyFunctions.prepareFilePart("file", uri!!, this@FollowUpActivity2)

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
                    MyFunctions.deleteCache(this@FollowUpActivity2)
                    if (status != "notInterested" || status != "completed") {
                        startActivity(
                            Intent(this@FollowUpActivity2, MainActivity::class.java)
                                .putExtra(Constant.LEAD_ID, LeadId)
                                .putExtra(Constant.LEAD_STATUS, status)
                        )
                        finish()
                    } else {
                        startActivity(
                            Intent(this@FollowUpActivity2, FollowUpActivity::class.java)
                                .putExtra(Constant.LEAD_ID, LeadId)
                                .putExtra(Constant.LEAD_STATUS, status)
                        )
                        finish()
                    }
                }

            } catch (e: Exception) {
                MyFunctions.progressDialogDismiss()
                val responseError = uploadLeadCallRecord.responseError.value!!
                Toast.makeText(this@FollowUpActivity2, responseError, Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            } catch (e: java.lang.NullPointerException) {
                MyFunctions.progressDialogDismiss()
                e.message
            }
        } else {
            Toast.makeText(this@FollowUpActivity2, "Success", Toast.LENGTH_SHORT).show()
            if (status != "notInterested" || status != "completed") {
                startActivity(
                    Intent(this@FollowUpActivity2, MainActivity::class.java)
                        .putExtra(Constant.LEAD_ID, LeadId)
                        .putExtra(Constant.LEAD_STATUS, status)
                )
                finish()
            } else {
                startActivity(
                    Intent(this@FollowUpActivity2, FollowUpActivity::class.java)
                        .putExtra(Constant.LEAD_ID, LeadId)
                        .putExtra(Constant.LEAD_STATUS, status)
                )
                finish()
            }
        }
    }

}