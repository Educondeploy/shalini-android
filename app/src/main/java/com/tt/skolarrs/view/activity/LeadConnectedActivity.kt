package com.tt.skolarrs.view.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.AdapterView
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.tt.skolarrs.R
import com.tt.skolarrs.databinding.ActivityLeadConnectedBinding
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

class LeadConnectedActivity : AppCompatActivity() {

    lateinit var binding: ActivityLeadConnectedBinding

    lateinit var status: String
    var newStatus: String = ""
    var duration: String = ""
    private lateinit var selectedRadioButton: RadioButton
    lateinit var viewModel: LeadStatusResponseViewModel
    lateinit var updateViewModel: UpdateLeadStatusViewModel
    lateinit var uploadLeadCallRecord: UploadLeadCallRecord
    var selectedPosition: Int = 0
    var token: String = ""
    var leadId: String = ""
    var selectedRadioButtonId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLeadConnectedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.include.title.text = getString(R.string.lead_connected)


        duration = MyFunctions.getSharedPreference(this, Constant.DURATION, Constant.DURATION)!!

        token = MyFunctions.getSharedPreference(
            this@LeadConnectedActivity,
            Constant.TOKEN,
            Constant.TOKEN
        )!!

        leadId = MyFunctions.getSharedPreference(
            this,
            Constant.LEAD_ID,
            Constant.LEAD_ID
        )!!


        viewModel = ViewModelProvider(this)[LeadStatusResponseViewModel::class.java]
        updateViewModel = ViewModelProvider(this)[UpdateLeadStatusViewModel::class.java]
        uploadLeadCallRecord = ViewModelProvider(this)[UploadLeadCallRecord::class.java]


        binding.include.backArrow.visibility = View.GONE

        binding.include.backArrow.setOnClickListener(View.OnClickListener {
            onBackPressed()
        })

        binding.submit.setOnClickListener(View.OnClickListener {
            if (selectedPosition == 0) {
                selectedRadioButtonId = binding.intrestedRadioGroup.checkedRadioButtonId
                selectedRadioButton = findViewById(selectedRadioButtonId)
                status = selectedRadioButton.text.toString()
            } else if (selectedPosition == 1) {
                selectedRadioButtonId = binding.notIntrestedRadioGroup.checkedRadioButtonId
                selectedRadioButton = findViewById(selectedRadioButtonId)
                status = selectedRadioButton.text.toString()
            }

            if (selectedRadioButtonId == -1) {
                Toast.makeText(
                    this@LeadConnectedActivity,
                    getString(R.string.please_select_the_item),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                if (MyFunctions.isConnected(this@LeadConnectedActivity)) {
                    val scope = CoroutineScope(Dispatchers.IO)
                    scope.launch(Dispatchers.Main) {
                       updateStatus()
                   ///     storeRecordedFiles("64f1e811c076f6b7bd3fa758")
                    }
                } else {
                    Toast.makeText(
                        this@LeadConnectedActivity,
                        getString(R.string.please_turn_on_your_internet_connection),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }


        })

        binding.leadResponse.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long
            ) {
                selectedPosition = position
                Log.d("TAG", "onItemSelected:$position")

                if (selectedPosition == 0) {
                    binding.interestedGroup.visibility = VISIBLE
                    binding.notInterestedGroup.visibility = GONE
                 }
                else if (selectedPosition == 1) {
                    binding.interestedGroup.visibility = GONE
                    binding.notInterestedGroup.visibility = VISIBLE
                  }

            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }


    }


    private suspend fun updateLeadStatus() {
        val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy")
        val currentDate: String = simpleDateFormat.format(Date())

        val userId = MyFunctions.getSharedPreference(this@LeadConnectedActivity, Constant.USER_ID, Constant.USER_ID)!!
        val id = MyFunctions.getSharedPreference(this@LeadConnectedActivity, Constant.ID, Constant.ID)!!

        viewModel.getLeadStatus(token, id, leadId, duration, status, userId, binding.notes.text.toString().trim(), currentDate, "", applicationContext)

        val response = viewModel.response
        val failes = viewModel.responseError

        try {
            MyFunctions.progressDialogDismiss()
            if (response.value != null) {
            // storeRecordedFiles(response.value!!.data._id, response.value!!.data.leadId)

                //Need to hide while adding store record file api
                if (newStatus == "notInterested" || newStatus == "userDisconnectTheCall" || newStatus == "invalidNumber" || newStatus == "completed") {

                    MyFunctions.setSharedPreference(this, Constant.LEAD_ID, "")
                    MyFunctions.setSharedPreference(this, Constant.LEAD_TYPE, "")
                    MyFunctions.setSharedPreference(this, Constant.LEAD_NAME, "")
                    MyFunctions.setSharedPreference(this, Constant.LEAD_NUMBER, "")

                    startActivity(Intent(this@LeadConnectedActivity, MainActivity::class.java))
                    finish()
                } else {

                    startActivity(Intent(this@LeadConnectedActivity, ShareActivity::class.java)
                        .putExtra(Constant.LEAD_ID, leadId)
                        .putExtra(Constant.LEAD_STATUS, newStatus)
                        .putExtra(Constant.DURATION, duration))
                    finish()
                }
            } else {
                val responseError = viewModel.responseError.value!!
                Toast.makeText(this@LeadConnectedActivity, responseError, Toast.LENGTH_SHORT).show()
            }

        } catch (e: Exception) {
            MyFunctions.progressDialogDismiss()
//            val responseError = viewModel.responseError.value!!
//            Toast.makeText(this@LeadConnectedActivity, responseError, Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        } catch (e: java.lang.NullPointerException) {
            MyFunctions.progressDialogDismiss()
            e.message
        }

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


    private suspend fun storeRecordedFiles(reportID:String,leadID:String) {

        Log.d("TAG", "storeRecordedFiles: " +getRecordedFiles())
        if (getRecordedFiles().isNotEmpty()) {
        val uri: File = getRecordedFiles()[0]!!



        val userId = MyFunctions.getSharedPreference(this@LeadConnectedActivity, Constant.USER_ID, Constant.USER_ID)!!
        val  file =
            MyFunctions.prepareFilePart("file", uri, this@LeadConnectedActivity)

        val leadId1 = MyFunctions.createPartFromString(leadID)
        val userID = MyFunctions.createPartFromString(userId)
        val reportId = MyFunctions.createPartFromString(reportID)
        val callMode = MyFunctions.createPartFromString("outgoing")

        val map = HashMap<String, RequestBody>()
        map["leadId"] = leadId1!!
        map["reportId"] = reportId!!
        map["userId"] = userID!!
        map["callMode"] = callMode!!


        uploadLeadCallRecord.storeRecordedFile(token,map, file!!,applicationContext)

        val response = uploadLeadCallRecord.response
        val failes = uploadLeadCallRecord.responseError


        try {
            MyFunctions.progressDialogDismiss()
            if (response.value != null) {
                Log.d("TAG", "storeRecordedFiles: " + response.value )
                MyFunctions.deleteCache(this@LeadConnectedActivity)
                Toast.makeText(this@LeadConnectedActivity, response.value!!.message, Toast.LENGTH_SHORT).show()

                if (newStatus == "notInterested" || newStatus == "userDisconnectTheCall" || newStatus == "invalidNumber" || newStatus == "completed") {

                    MyFunctions.setSharedPreference(this, Constant.LEAD_ID, "")
                    MyFunctions.setSharedPreference(this, Constant.LEAD_TYPE, "")
                    MyFunctions.setSharedPreference(this, Constant.LEAD_NAME, "")
                    MyFunctions.setSharedPreference(this, Constant.LEAD_NUMBER, "")

                    startActivity(Intent(this@LeadConnectedActivity, MainActivity::class.java))
                    finish()
                } else {
                       startActivity(Intent(this@LeadConnectedActivity, ShareActivity::class.java)
                        .putExtra(Constant.LEAD_ID, leadId)
                        .putExtra(Constant.LEAD_STATUS, newStatus)
                        .putExtra(Constant.DURATION, duration))
                    finish()
                }
            } else {
               val responseError = uploadLeadCallRecord.responseError.value!!
                Toast.makeText(this@LeadConnectedActivity, responseError, Toast.LENGTH_SHORT).show()
            }

        } catch (e: Exception) {
            MyFunctions.progressDialogDismiss()
            val responseError = uploadLeadCallRecord.responseError.value!!
            Toast.makeText(this@LeadConnectedActivity, responseError, Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        } catch (e: java.lang.NullPointerException) {
            MyFunctions.progressDialogDismiss()
            e.message
        } } else {
            Toast.makeText(this@LeadConnectedActivity,"success", Toast.LENGTH_SHORT).show()

            if (newStatus == "notInterested" || newStatus == "userDisconnectTheCall" || newStatus == "invalidNumber" || newStatus == "completed") {

                MyFunctions.setSharedPreference(this, Constant.LEAD_ID, "")
                MyFunctions.setSharedPreference(this, Constant.LEAD_TYPE, "")
                MyFunctions.setSharedPreference(this, Constant.LEAD_NAME, "")
                MyFunctions.setSharedPreference(this, Constant.LEAD_NUMBER, "")

                startActivity(Intent(this@LeadConnectedActivity, MainActivity::class.java))
                finish()
            } else {
                startActivity(Intent(this@LeadConnectedActivity, ShareActivity::class.java)
                    .putExtra(Constant.LEAD_ID, leadId)
                    .putExtra(Constant.LEAD_STATUS, newStatus)
                    .putExtra(Constant.DURATION, duration))
                finish()
            }
        }

    }

    private suspend fun updateStatus() {
        MyFunctions.progressDialogShow(this@LeadConnectedActivity)

        when (status) {
            getString(R.string.user_disconnect_the_call) -> {
                newStatus = "userDisconnectTheCall"
            }
            getString(R.string.customer_say_not_interest) -> {
                newStatus = "notInterested"
            }
            getString(R.string.hot) -> {
                newStatus = "hot"
            }
            getString(R.string.cold) -> {
                newStatus = "cold"
            }
            getString(R.string.warm) -> {
                newStatus = "warm"
            }
            getString(R.string.invalid_number) -> {
                newStatus = "invalidNumber"
            }
            getString(R.string.completed) -> {
                newStatus = "completed"
            }
        }

//        val leadId = intent.getStringExtra(Constant.LEAD_ID)!!
        val userId = MyFunctions.getSharedPreference(
            this@LeadConnectedActivity,
            Constant.USER_ID,
            Constant.USER_ID
        )!!
        val lead_id = MyFunctions.getSharedPreference(
            this@LeadConnectedActivity,
            Constant.LEAD_ID,
            Constant.LEAD_ID
        )!!
        val previousLead = MyFunctions.getSharedPreference(
            this@LeadConnectedActivity,
            Constant.LEAD_TYPE,
            Constant.LEAD_TYPE
        )!!

        val id =
            MyFunctions.getSharedPreference(this@LeadConnectedActivity, Constant.ID, Constant.ID)!!

        updateViewModel.UpdateLeadStatus(
            token,
            newStatus!!, lead_id, previousLead, applicationContext
        )

        val response = updateViewModel.response
        val failes = updateViewModel.responseError

        try {
            //   MyFunctions.progressDialogDismiss()
            if (response.value != null) {
                Toast.makeText(this@LeadConnectedActivity, response.value!!.message, Toast.LENGTH_SHORT).show()

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
                MyFunctions.progressDialogDismiss()
                val responseError = updateViewModel.responseError.value!!
                Toast.makeText(this@LeadConnectedActivity, responseError, Toast.LENGTH_SHORT).show()
            }

        } catch (e: Exception) {
            MyFunctions.progressDialogDismiss()
//            val responseError = updateViewModel.responseError.value!!
//            Toast.makeText(this@LeadConnectedActivity, responseError, Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        } catch (e: java.lang.NullPointerException) {
            MyFunctions.progressDialogDismiss()
            e.message
        }


    }


}
