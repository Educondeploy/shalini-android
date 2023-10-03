package com.tt.skolarrs.view.fragment

//import com.tt.skolarrs.service.CallRecordingService
import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Context.TELEPHONY_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.CallLog
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.util.Log
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tt.skolarrs.R
import com.tt.skolarrs.com.github.axet.callrecorder.services.RecordingService
import com.tt.skolarrs.databinding.DialogFilterBinding
import com.tt.skolarrs.databinding.FragmentLeadBinding
import com.tt.skolarrs.databinding.ReportHistoryBinding
import com.tt.skolarrs.model.response.*
import com.tt.skolarrs.utilz.Constant
import com.tt.skolarrs.utilz.MyFunctions
import com.tt.skolarrs.view.activity.LeadResponseActivity
import com.tt.skolarrs.view.activity.MainActivity
import com.tt.skolarrs.view.adapter.*
import com.tt.skolarrs.viewmodel.GetIndividualReportViewModel
import com.tt.skolarrs.viewmodel.GetPaginationLeadsViewModel
import com.tt.skolarrs.viewmodel.LeadViewModel
import kotlinx.coroutines.*
import java.io.File
import java.util.*
import kotlin.math.ceil


class LeadFragment : Fragment() {

    private lateinit var binding: FragmentLeadBinding

    private lateinit var leadViewModel: LeadViewModel

    private lateinit var leadViewModelAdapter: LeadViewModelAdapter
    private lateinit var paginationLeadViewModelAdapter: PaginationLeadViewModelAdapter

    private lateinit var paginationViewModel: GetPaginationLeadsViewModel

    private var limit: Int = 10
    private var leadStatus: String = ""

    private var leadStatus1: String = ""

    private var leadStatus2: String = ""

    private var totalPage: Int = 0
    private var currentPage: Int = 1

    private lateinit var individualReportResponseViewModel: GetIndividualReportViewModel
    private var interestedArrayList = ArrayList<TemperatureData>()
    private var modifiedList = ArrayList<UpdatedList>()
    private var dateWiseFilterList = ArrayList<UpdatedList>()
    private var followUpList = ArrayList<IndividualReportData>()
    private lateinit var telephonyManager: TelephonyManager
    private lateinit var phoneStateListener: PhoneStateListener
//    var recordings: Recordings? = null
//    var storage: Storage? = null


    private var list = ArrayList<UpdatedPaginationList>()

    var token: String = ""
    var assignedOn: String = ""
    var phoneNo: String = ""
    lateinit var MOBILE_NO: String


    var type: String = ""
    var leadId: String = ""
    var leadName: String = ""
    var intentType: String = ""
    var listType: String = ""
    var status: String = ""
    var listCount: String = "0"

    var year = 0
    var month: Int = 0
    var day: Int = 0

    private var fileName: String = ""

    var dialog: Dialog? = null

    val mp = MediaPlayer()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                startActivity(Intent(requireActivity(), MainActivity::class.java))
                requireActivity().finish()
            }
        }

        //getProducts();
        dialog = ProgressDialog(requireActivity())

        telephonyManager = context?.getSystemService(TELEPHONY_SERVICE) as TelephonyManager

        phoneStateListener = object : PhoneStateListener() {
            override fun onCallStateChanged(state: Int, phoneNumber: String?) {
                super.onCallStateChanged(state, phoneNumber)

//                if(state.equals(  TelephonyManager.CALL_STATE_IDLE)) {
//                    val service = Intent(requireContext(), CallRecorder::class.java)
//                    requireContext().startService(service)
//                } else {
//                    val service = Intent(requireContext(), CallRecorder::class.java)
//                    requireContext().stopService(service)
//                }

//                when (state) {
//                    TelephonyManager.CALL_STATE_IDLE ->
//
////                        CallRecordingService.stopRecording()
//                    TelephonyManager.CALL_STATE_OFFHOOK -> CallRecordingService.startRecording(requireContext())
//                }
            }
        }

        // getRecordedFiles();

        requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallback)


    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val bundle = this.arguments

        intentType =
            if (bundle != null && bundle!!.getString(Constant.TYPE, Constant.LEAD) != null) {
                bundle!!.getString(Constant.TYPE, Constant.LEAD)
            } else {
                Constant.LEAD
            }

        Log.d("TAG", "onCreateView: " + intentType)



        if (intentType == "ADMISSION CLOSED") {
            (requireActivity() as MainActivity).title =
                "Lead Closed($listCount)"
        } else {
            (requireActivity() as MainActivity).title =
                MyFunctions.changeToProperCase(intentType) + "(" + listCount + ")"
        }

        binding = FragmentLeadBinding.inflate(inflater, container, false)
        val view: View = binding.root

        token = MyFunctions.getSharedPreference(requireActivity(), Constant.TOKEN, Constant.TOKEN)!!
        assignedOn =
            MyFunctions.getSharedPreference(requireActivity(), Constant.USER_ID, Constant.USER_ID)!!
        leadViewModel = ViewModelProvider(this)[LeadViewModel::class.java]
        paginationViewModel = ViewModelProvider(this)[GetPaginationLeadsViewModel::class.java]
        individualReportResponseViewModel =
            ViewModelProvider(this)[GetIndividualReportViewModel::class.java]

        binding.searchButton.setOnClickListener(View.OnClickListener {
            val scope = CoroutineScope(Dispatchers.IO)

            scope.launch(Dispatchers.IO) {
                getPaginationLeads("search");

            }

        })


        binding.search.setOnEditorActionListener { textView, i, keyEvent ->
            if (i == EditorInfo.IME_ACTION_SEARCH) {
                val scope = CoroutineScope(Dispatchers.IO)
                scope.launch(Dispatchers.IO) {

                    getPaginationLeads("search");

                }
                true
            } else {
                false
            }

        }


        binding.leadList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

                val lastVisibleItemPosition =
                    (recyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
                val totalItemCount = recyclerView.adapter?.itemCount ?: 0

                Log.d("TAG", "onScrolled: $totalItemCount$lastVisibleItemPosition")

                if (lastVisibleItemPosition == totalItemCount - 1) {
                    if (currentPage < totalPage) {
                        if (dialog!!.isShowing) {
                            Toast.makeText(
                                requireActivity(),
                                "Wait data is loading ",
                                Toast.LENGTH_SHORT
                            ).show();
                        } else {

                            val scope = CoroutineScope(Dispatchers.IO)

                            scope.launch(Dispatchers.IO) {
                                currentPage += 1

                                getPaginationLeads("")
                                withContext(Dispatchers.Main) {
                                    paginationLeadViewModelAdapter.notifyDataSetChanged()
                                }

                            }
                        }
                    }

                }
            }
        })

        //getRecordedCallFiles();

//        getRecordedFiles()

        //playAudio("/data/user/0/com.tt.skyappz/cache/Call_20230907_145425.mp3")


        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val filterIcon = requireActivity().findViewById<ImageView>(R.id.filterIcon)

        filterIcon.visibility = View.VISIBLE

        filterIcon.setOnClickListener {
            // requestPermission("8124227606", "call")

            //  requestPermission("9944732994", "call")
            //callNumber("9629796215")
            //  dateFilter()
        }
    }

    override fun onResume() {
        super.onResume()
        setUpAdapter();
        list.clear();
        if (MyFunctions.isConnected(requireActivity())) {
            val scope = CoroutineScope(Dispatchers.IO)

            scope.launch(Dispatchers.IO) {

                getPaginationLeads("")


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

    private fun setUpAdapter() {
        paginationLeadViewModelAdapter = PaginationLeadViewModelAdapter(
            requireContext(),
            intentType,
            list,
            object : PaginationListener {
                override fun onSelect(position: Int) {
                    TODO("Not yet implemented")
                }

                override fun unSelect(position: Int) {
                    TODO("Not yet implemented")
                }

                override fun askPermission(
                    mobileNo: String,
                    _id: String,
                    listType: String,
                    name: String
                ) {
                    phoneNo = mobileNo
                    MyFunctions.setSharedPreference(
                        requireActivity(),
                        Constant.LEAD_TYPE,
                        listType
                    )
                    //   phoneNo = "9751665327"
                    leadId = _id
                    leadName = name

                    MyFunctions.setSharedPreference(
                        requireActivity(),
                        Constant.LEAD_ID,
                        _id
                    )
                    MyFunctions.setSharedPreference(
                        requireActivity(),
                        Constant.LEAD_NUMBER,
                        mobileNo
                    )
                    MyFunctions.setSharedPreference(
                        requireActivity(),
                        Constant.LEAD_NAME,
                        name
                    )
                    requestPermission(phoneNo, "call")

                    //  requestPermission("8124227606", "call")
                    // requestPermission("7339567180", "call")

                }

                override fun openDialogBox(_id: String, createdDate: String) {
                    reportHistoryDialog(_id, createdDate)
                }

                override fun viewProfile(_id: String) {
                    val profileLink =
                        Constant.BASE_URL1 + "event/download-expo?_id=$_id"
                    val urlIntent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(profileLink)
                    )
                    startActivity(urlIntent)
                }
            })

        (requireActivity() as MainActivity).title =
            MyFunctions.changeToProperCase(intentType) + "(" + listCount + ")"

        binding.leadList.adapter = paginationLeadViewModelAdapter
    }

//    private fun requestPermission(mobileNo: String) {
//        if (requireActivity().checkSelfPermission(Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED && requireActivity().checkSelfPermission(
//                Manifest.permission.WRITE_CALL_LOG
//            ) != PackageManager.PERMISSION_GRANTED && requireActivity().checkSelfPermission(
//                Manifest.permission.READ_CONTACTS
//            ) != PackageManager.PERMISSION_GRANTED && requireActivity().checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
//            && requireActivity().checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED && requireActivity().checkSelfPermission(
//                Manifest.permission.SET_PROCESS_LIMIT
//            ) != PackageManager.PERMISSION_GRANTED && requireActivity().checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
//        )
    //{
//            requestPermissions(
//                arrayOf(
//                    Manifest.permission.WRITE_CALL_LOG,
//                    Manifest.permission.READ_CALL_LOG,
//                    Manifest.permission.READ_CONTACTS,
//                    Manifest.permission.RECORD_AUDIO,
//                    Manifest.permission.PROCESS_OUTGOING_CALLS,
//                ), 1
//            )
//        }
//
//
//
//        else {
//            callNumber(mobileNo)
//            //  callNumber("")
//        }
//
//    }

    private fun requestPermission(mobileNo: String, type: String) {
        val requiredPermissions = arrayOf(
            Manifest.permission.WRITE_CALL_LOG,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.PROCESS_OUTGOING_CALLS
        )

        val anyPermissionNotGranted = requiredPermissions.all { permission ->
            requireActivity().checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
        }



        Log.d("TAG", "requestPermission: " + anyPermissionNotGranted)

        if (!anyPermissionNotGranted) {
            requestPermissions(requiredPermissions, 1)
        } else {
            if (type == "call") {
                callNumber(mobileNo)
            } else {
                // RecordingService.stopButton(requireContext())
                //   CallRRecorder.onRecord(requireContext(),false);
                // CallRecordingService.onRecord(false, requireContext());
                getLastCalledNumber(requireActivity())
                getLastCallDuration()
            }
        }
    }


//    private fun requestPermission(mobileNo: String) {
//        val requiredPermissions = arrayOf(
//            Manifest.permission.WRITE_CALL_LOG,
//            Manifest.permission.READ_CALL_LOG,
//            Manifest.permission.READ_CONTACTS,
//            Manifest.permission.RECORD_AUDIO,
//            Manifest.permission.PROCESS_OUTGOING_CALLS,
//            Manifest.permission.CALL_PHONE
//        )
//
//        val allPermissionsGranted = requiredPermissions.all { permission ->
//            requireActivity().checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
//        }
//
//        Log.d("TAG", "requestPermission: $allPermissionsGranted")
//
//        if (allPermissionsGranted) {
//            callNumber(mobileNo)
//        } else {
//            Toast.makeText(
//                requireActivity(),
//                "Please allow all permission, Go to the app info-> Permission and allow all permission",
//                Toast.LENGTH_SHORT
//            )
//                .show()
//        }
//    }


    private fun callNumber(contactNo: String) {
        type = Constant.CALL
        // RecordingService.startIfEnabled(requireContext());
        // CallRRecorder.onRecord(requireContext(),true)
        val u = Uri.parse("tel:$contactNo")
        val i = Intent(Intent.ACTION_DIAL, u)
        startActivity(i);
//        val intent = Intent(Intent.ACTION_CALL)
//        intent.data = Uri.parse("tel:" + Uri.encode(contactNo))
//        startActivity(intent)
//        val u = Uri.parse("tel:$contactNo")
//
//        val i = Intent(Intent.ACTION_CALL, u)
//        startActivity(i)

        /*

         try {
             startActivity(i)
             activity?.finish()
         } catch (s: SecurityException) {

             Toast.makeText(requireActivity(), "An error occurred", Toast.LENGTH_LONG)
                 .show()
         }*/

    }

    override fun onStart() {
        super.onStart()

        if (type == Constant.CALL) {
            requestPermission(phoneNo, "record");

            //  getRecordedFiles()
        }

    }

    @SuppressLint("Range")
    fun getLastCalledNumber(context: Context): String? {
        val cursor = context.contentResolver.query(
            CallLog.Calls.CONTENT_URI,
            null,
            null,
            null,
            CallLog.Calls.DATE + " DESC"
        )
        if (cursor?.moveToFirst() == true) {
            val number = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER))
            cursor.close()
            MOBILE_NO = number
            return number
        }
        cursor?.close()
        return null
    }

    private fun getLastCallDuration(): String {
        type = ""
        var duration = "0"
        try {
            val cursor = requireActivity().contentResolver.query(
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

        // if (MOBILE_NO == phoneNo) {
        MyFunctions.setSharedPreference(
            requireActivity(),
            Constant.DURATION,
            MyFunctions.formatSeconds(duration.toInt())
        )
        startActivity(
            Intent(
                requireActivity(),
                LeadResponseActivity::class.java
            )
        )
//        } else {
//            Toast.makeText(requireActivity(), "Please make a call", Toast.LENGTH_SHORT).show()
//            startActivity(
//                Intent(
//                    requireActivity(),
//                    MainActivity::class.java
//                )
//            )
//        }


        return duration
    }

    private suspend fun getPaginationLeads(type: String) {
        if (type.equals("search")) {
            list.clear()
            currentPage = 1
        }
        withContext(Dispatchers.Main) {
            if (dialog!!.isShowing) {
                dialog!!.dismiss()
                dialog!!.cancel()
                //  MyFunctions.progressDialogShow(WarehouseStockActivity.this);
            } else {
                dialog!!.show()
                dialog!!.setCancelable(false)
            }
            //   MyFunctions.progressDialogShow(requireActivity())

            when (intentType) {
                "USER DISCONNECT THE CALL" -> {
                    leadStatus = "userDisconnectTheCall"
                    listType = Constant.USERDISCONNECTTHECALL
                }
                Constant.LEAD -> {
                    leadStatus = "incomplete"
                    listType = Constant.INCOMPLETE
                }
                requireActivity().getString(R.string.switchOff)
                    .uppercase(), getString(R.string.switchoff).uppercase() -> {
                    leadStatus = "switchOff"
                    listType = Constant.INCOMPLETE
                }
                requireActivity().getString(R.string.interest)
                    .uppercase(), requireActivity().getString(R.string.interested)
                    .uppercase() -> {
                    leadStatus = "hot"
                    leadStatus1 = "cold"
                    leadStatus2 = "warm"
                    listType = Constant.INTEREST
                }

                requireActivity().getString(R.string.not_interested)
                    .uppercase() -> {
                    leadStatus = "userDisconnectTheCall"
                    leadStatus1 = "notInterested"
                    leadStatus2 = "invalidNumber"
                    listType = Constant.NOTINTEREST
                }

                requireActivity().getString(R.string.out_of_coverage)
                    .uppercase() -> {
                    leadStatus = "outofCoverage"
                    listType = Constant.OUTOFCOVERAGE
                }
                requireActivity().getString(R.string.hot).uppercase() -> {
                    leadStatus = "hot"
                    listType = Constant.HOT
                }
                requireActivity().getString(R.string.cold).uppercase() -> {
                    leadStatus = "cold"
                    listType = Constant.COLD
                }
                requireActivity().getString(R.string.warm).uppercase() -> {
                    leadStatus = "warm"
                    listType = Constant.WARM
                }
                requireActivity().getString(R.string.did_not_pick)
                    .uppercase() -> {
                    leadStatus = "didNotPick"
                    listType = Constant.DIDNOTPICK

                }
                requireActivity().getString(R.string.busy_on_another_call)
                    .uppercase() -> {
                    leadStatus = "busyOnOtherCall"
                    listType = Constant.BUSYONANOTHERCALL
                }
                requireActivity().getString(R.string.invalid_number)
                    .uppercase() -> {
                    leadStatus = "invalidNumber"
                    listType = Constant.INVALIDNUMBER
                }
                requireActivity().getString(R.string.admission_closed)
                    .uppercase() -> {
                    leadStatus = "completed"
                    listType = Constant.COMPLETED
                }
                "LEAD CLOSED"
                -> {
                    leadStatus = "completed"
                    listType = Constant.COMPLETED
                }
            }
            withTimeoutOrNull(10000L) {
                paginationViewModel.getPaginationLeads(
                    token,
                    currentPage,
                    limit,
                    assignedOn,
                    leadStatus, leadStatus1, leadStatus2, binding.search.text.toString().trim(),
                    context
                )

                try {
//            MyFunctions.progressDialogDismiss()
                    dialog!!.dismiss()
                    dialog!!.cancel()
                    if (paginationViewModel.response.value != null && paginationViewModel.response.value!!.data.data.isNotEmpty()) {
                        binding.leadList.visibility = VISIBLE
                        binding.noData.visibility = GONE
                        listCount = paginationViewModel.response.value!!.data.total.toString()
                        totalPage =
                            ceil(paginationViewModel.response.value!!.data.total / 10.0).toInt()

                        for (i in paginationViewModel.response.value!!.data.data.indices) {
                            val list1 = UpdatedPaginationList(
                                paginationViewModel.response.value!!.data.data[i],
                                leadStatus
                            )
                            list.add(list1)
                        }


                        (requireActivity() as MainActivity).title =
                            MyFunctions.changeToProperCase(intentType) + "(" + listCount + ")"
                        paginationLeadViewModelAdapter.notifyDataSetChanged()


                    } else {
                        binding.leadList.visibility = GONE
                        binding.noData.visibility = VISIBLE
                    }

                    if (list.size == 0) {
                        binding.leadList.visibility = GONE
                        binding.noData.visibility = VISIBLE
                    } else {
                        binding.leadList.visibility = VISIBLE
                        binding.noData.visibility = GONE
                    }
                    if (paginationViewModel.response.value!!.data.total == 0) {
                        binding.leadList.visibility = GONE
                        binding.noData.visibility = VISIBLE
                    } else {
                        binding.leadList.visibility = VISIBLE
                        binding.noData.visibility = GONE
                    }

                } catch (e: Exception) {
                    dialog!!.dismiss()
                    dialog!!.cancel()
                    // MyFunctions.progressDialogDismiss()
                    leadViewModel.responseError.value
                    binding.leadList.visibility = GONE
                    binding.noData.visibility = VISIBLE
                    //   Toast.makeText(requireActivity(), responseError, Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                } catch (e: NullPointerException) {
                    // MyFunctions.progressDialogDismiss()
                    dialog!!.dismiss()
                    dialog!!.cancel()
                    e.message
                    binding.leadList.visibility = GONE
                    binding.noData.visibility = VISIBLE
                }
            }

        }
    }

    private suspend fun getLeads() {
        interestedArrayList.clear()
        modifiedList.clear()
        MyFunctions.progressDialogShow(requireActivity())
        withTimeoutOrNull(10000L) {
            leadViewModel.getLeads(token, context)

            try {
                MyFunctions.progressDialogDismiss()
                //   val data = leadViewModel.response.value!!.data
                // Log.d("TAG", "leadViewModel: "+leadViewModel.response.value!!)
                if (leadViewModel.response.value != null && leadViewModel.response.value!!.data.isNotEmpty()) {
                    for (i in leadViewModel.response.value!!.data.indices) {


                        if (intentType == "USER DISCONNECT THE CALL") {
                            listType = Constant.USERDISCONNECTTHECALL
                            if (leadViewModel.response.value!!.data[i].userDisconnectTheCall.isNotEmpty()) {
                                binding.leadList.visibility = VISIBLE
                                binding.noData.visibility = GONE
                                interestedArrayList.addAll(
                                    leadViewModel.response.value!!.data[i].userDisconnectTheCall
                                )
                                status = "userDisconnectTheCall"
                                interestedArrayList.forEach { userDisconnectTheCall ->
                                    val list = UpdatedList(userDisconnectTheCall, status)
                                    if (!modifiedList.contains(list)) {
                                        modifiedList.add(list)
                                    }
                                }

                                listCount = modifiedList.size.toString()
                            }
                        }

                        if (intentType == Constant.LEAD) {
                            listType = Constant.INCOMPLETE
                            binding.leadList.visibility = VISIBLE
                            binding.noData.visibility = GONE
                            interestedArrayList.addAll(
                                leadViewModel.response.value!!.data[i].incomplete
                            )
                            status = "incomplete"
                            interestedArrayList.forEach { userDisconnectTheCall ->
                                val list = UpdatedList(userDisconnectTheCall, status)
                                if (!modifiedList.contains(list)) {
                                    modifiedList.add(list)
                                }

                            }

                            listCount = modifiedList.size.toString()


                        }

                        if (intentType == requireActivity().getString(R.string.switchOff)
                                .uppercase() || intentType == getString(R.string.switchoff).uppercase()
                        ) {
                            listType = Constant.SWITCHOFF
                            binding.leadList.visibility = VISIBLE
                            binding.noData.visibility = GONE
                            interestedArrayList.addAll(
                                leadViewModel.response.value!!.data[i].switchOff
                            )
                            status = "switchOff"
                            interestedArrayList.forEach { userDisconnectTheCall ->
                                val list = UpdatedList(userDisconnectTheCall, status)
                                if (!modifiedList.contains(list)) {
                                    modifiedList.add(list)
                                }
                            }
                            listCount = modifiedList.size.toString()

                        }

                        if (intentType == requireActivity().getString(R.string.out_of_coverage)
                                .uppercase()
                        ) {
                            listType = Constant.OUTOFCOVERAGE
                            binding.leadList.visibility = VISIBLE
                            binding.noData.visibility = GONE
                            interestedArrayList.addAll(
                                leadViewModel.response.value!!.data[i].outofCoverage
                            )
                            status = "outofCoverage"
                            interestedArrayList.forEach { userDisconnectTheCall ->
                                val list = UpdatedList(userDisconnectTheCall, status)
                                if (!modifiedList.contains(list)) {
                                    modifiedList.add(list)
                                }
                            }
                            listCount = modifiedList.size.toString()

                        }

                        if (intentType == requireActivity().getString(R.string.hot).uppercase()) {
                            listType = Constant.HOT
                            binding.leadList.visibility = VISIBLE
                            binding.noData.visibility = GONE
                            interestedArrayList.addAll(
                                leadViewModel.response.value!!.data[i].hot
                            )
                            status = "hot"
                            interestedArrayList.forEach { userDisconnectTheCall ->
                                val list = UpdatedList(userDisconnectTheCall, status)
                                if (!modifiedList.contains(list)) {
                                    modifiedList.add(list)
                                }
                            }
                            listCount = modifiedList.size.toString()
                            /* } else {
                                 binding.leadList.visibility = GONE
                                 binding.noData.visibility = VISIBLE
                             }*/
                        }

                        if (intentType == requireActivity().getString(R.string.cold).uppercase()) {
                            listType = Constant.COLD
                            binding.leadList.visibility = VISIBLE
                            binding.noData.visibility = GONE
                            interestedArrayList.addAll(
                                leadViewModel.response.value!!.data[i].cold
                            )
                            status = "cold"
                            interestedArrayList.forEach { userDisconnectTheCall ->
                                val list = UpdatedList(userDisconnectTheCall, status)
                                if (!modifiedList.contains(list)) {
                                    modifiedList.add(list)
                                }
                            }
                            listCount = modifiedList.size.toString()

                        }

                        if (intentType == requireActivity().getString(R.string.warm).uppercase()) {
                            listType = Constant.WARM
                            binding.leadList.visibility = VISIBLE
                            binding.noData.visibility = GONE
                            interestedArrayList.addAll(
                                leadViewModel.response.value!!.data[i].warm
                            )
                            status = "warm"
                            interestedArrayList.forEach { userDisconnectTheCall ->
                                val list = UpdatedList(userDisconnectTheCall, status)
                                if (!modifiedList.contains(list)) {
                                    modifiedList.add(list)
                                }
                            }
                            listCount = modifiedList.size.toString()
                        }

                        if (intentType == requireActivity().getString(R.string.did_not_pick)
                                .uppercase()
                        ) {
                            listType = Constant.DIDNOTPICK
                            binding.leadList.visibility = VISIBLE
                            binding.noData.visibility = GONE
                            interestedArrayList.addAll(
                                leadViewModel.response.value!!.data[i].didNotPick
                            )
                            status = "didNotPick"
                            interestedArrayList.forEach { userDisconnectTheCall ->
                                val list = UpdatedList(userDisconnectTheCall, status)
                                if (!modifiedList.contains(list)) {
                                    modifiedList.add(list)
                                }
                            }
                            listCount = modifiedList.size.toString()

                        }

                        if (intentType == requireActivity().getString(R.string.busy_on_another_call)
                                .uppercase()
                        ) {
                            listType = Constant.BUSYONANOTHERCALL
                            binding.leadList.visibility = VISIBLE
                            binding.noData.visibility = GONE
                            interestedArrayList.addAll(
                                leadViewModel.response.value!!.data[i].busyOnOtherCall
                            )
                            status = "busyOnOtherCall"
                            interestedArrayList.forEach { userDisconnectTheCall ->
                                val list = UpdatedList(userDisconnectTheCall, status)
                                if (!modifiedList.contains(list)) {
                                    modifiedList.add(list)
                                }
                            }
                            listCount = modifiedList.size.toString()
                        }

                        if (intentType == requireActivity().getString(R.string.invalid_number)
                                .uppercase()
                        ) {
                            listType = Constant.INVALIDNUMBER
                            binding.leadList.visibility = VISIBLE
                            binding.noData.visibility = GONE
                            interestedArrayList.addAll(
                                leadViewModel.response.value!!.data[i].invalidNumber
                            )
                            status = "invalidNumber"
                            interestedArrayList.forEach { userDisconnectTheCall ->
                                val list = UpdatedList(userDisconnectTheCall, status)
                                if (!modifiedList.contains(list)) {
                                    modifiedList.add(list)
                                }
                            }
                            listCount = modifiedList.size.toString()
                        }
                        if (intentType == requireActivity().getString(R.string.admission_closed)
                                .uppercase()
                        ) {
                            listType = Constant.COMPLETED
                            binding.leadList.visibility = VISIBLE
                            binding.noData.visibility = GONE
                            interestedArrayList.addAll(
                                leadViewModel.response.value!!.data[i].complete
                            )
                            status = "complete"
                            interestedArrayList.forEach { userDisconnectTheCall ->
                                val list = UpdatedList(userDisconnectTheCall, status)
                                if (!modifiedList.contains(list)) {
                                    modifiedList.add(list)
                                }
                            }
                            listCount = modifiedList.size.toString()
                        }

                        if (intentType == requireActivity().getString(R.string.interest)
                                .uppercase() || intentType == requireActivity().getString(R.string.interested)
                                .uppercase()
                        ) {
                            listType = Constant.INTEREST
                            leadViewModel.response.value!!.data[i]
                            val data1 = leadViewModel.response.value!!.data[i]
                            if (data1.hot.isNotEmpty()) {
                                interestedArrayList.addAll(data1.hot)
                                status = "hot"
                                data1.hot.forEach { userDisconnectTheCall ->
                                    val list = UpdatedList(userDisconnectTheCall, status)
                                    if (!modifiedList.contains(list)) {
                                        modifiedList.add(list)
                                    }
                                }
                            }
                            if (data1.cold.isNotEmpty()) {
                                interestedArrayList.addAll(data1.cold)
                                status = "cold"
                                data1.cold.forEach { notInterested ->
                                    val list = UpdatedList(notInterested, status)
                                    if (!modifiedList.contains(list)) {
                                        modifiedList.add(list)
                                    }
                                }
                            }

                            if (data1.warm.isNotEmpty()) {
                                interestedArrayList.addAll(data1.warm)
                                status = "warm"
                                data1.warm.forEach { invalidNumber ->
                                    val list = UpdatedList(invalidNumber, status)
                                    if (!modifiedList.contains(list)) {
                                        modifiedList.add(list)
                                    }
                                }
                            }

                            listCount = modifiedList.size.toString()

                            binding.leadList.visibility = VISIBLE
                            binding.noData.visibility = GONE
                        }

                        if (intentType == requireActivity().getString(R.string.not_interested)
                                .uppercase()
                        ) {
                            listType = Constant.NOTINTEREST
                            val data1 = leadViewModel.response.value!!.data[i]
                            if (data1.userDisconnectTheCall.isNotEmpty()) {
                                interestedArrayList.addAll(data1.userDisconnectTheCall)
                                status = "userDisconnectTheCall"
                                data1.userDisconnectTheCall.forEach { userDisconnectTheCall ->
                                    val list = UpdatedList(userDisconnectTheCall, status)
                                    if (!modifiedList.contains(list)) {
                                        modifiedList.add(list)
                                    }
                                }
                            }
                            if (data1.notInterested.isNotEmpty()) {
                                interestedArrayList.addAll(data1.notInterested)
                                status = "notInterested"
                                data1.notInterested.forEach { notInterested ->
                                    val list = UpdatedList(notInterested, status)
                                    if (!modifiedList.contains(list)) {
                                        modifiedList.add(list)
                                    }
                                }
                            }

                            if (data1.invalidNumber.isNotEmpty()) {
                                interestedArrayList.addAll(data1.invalidNumber)
                                status = "invalidNumber"
                                data1.invalidNumber.forEach { invalidNumber ->
                                    val list = UpdatedList(invalidNumber, status)
                                    if (!modifiedList.contains(list)) {
                                        modifiedList.add(list)
                                    }
                                }
                            }

                            listCount = modifiedList.size.toString()

                            binding.leadList.visibility = VISIBLE
                            binding.noData.visibility = GONE
                        }

                    }

                } else if (modifiedList.size == 0) {
                    binding.leadList.visibility = GONE
                    binding.noData.visibility = VISIBLE
                } else {
                    binding.leadList.visibility = GONE
                    binding.noData.visibility = VISIBLE
                }

                (requireActivity() as MainActivity).title =
                    MyFunctions.changeToProperCase(intentType) + "(" + listCount + ")"

                leadViewModelAdapter =
                    LeadViewModelAdapter(
                        requireActivity(), intentType,
                        /*intrestedArrayList*/ modifiedList,
                        object : Listener {
                            override fun onSelect(position: Int) {
                                TODO("Not yet implemented")
                            }

                            override fun unSelect(position: Int) {
                                TODO("Not yet implemented")
                            }

                            override fun askPermission(
                                mobileNo: String,
                                _id: String,
                                listType: String,
                                name: String
                            ) {
                                phoneNo = mobileNo
                                MyFunctions.setSharedPreference(
                                    requireActivity(),
                                    Constant.LEAD_TYPE,
                                    listType
                                )
                                //   phoneNo = "9751665327"
                                leadId = _id
                                leadName = name

                                MyFunctions.setSharedPreference(
                                    requireActivity(),
                                    Constant.LEAD_ID,
                                    _id
                                )
                                MyFunctions.setSharedPreference(
                                    requireActivity(),
                                    Constant.LEAD_NUMBER,
                                    mobileNo
                                )
                                MyFunctions.setSharedPreference(
                                    requireActivity(),
                                    Constant.LEAD_NAME,
                                    name
                                )


                                requestPermission(phoneNo, "call")

                            }

                            override fun openDialogBox(_id: String, createdDate: String) {
                                reportHistoryDialog(_id, createdDate)

                            }

                            override fun viewProfile(_id: String) {
                                val profileLink =
                                    Constant.BASE_URL1 + "event/download-expo?_id=$_id"
                                val urlIntent = Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse(profileLink)
                                )
                                startActivity(urlIntent)
                            }

                        })

                binding.leadList.adapter = leadViewModelAdapter


            } catch (e: Exception) {
                MyFunctions.progressDialogDismiss()
                leadViewModel.responseError.value
                binding.leadList.visibility = GONE
                binding.noData.visibility = VISIBLE
                //   Toast.makeText(requireActivity(), responseError, Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            } catch (e: NullPointerException) {
                MyFunctions.progressDialogDismiss()
                e.message
                binding.leadList.visibility = GONE
                binding.noData.visibility = VISIBLE
            }

        } ?: run {
            // Handle the timeout case when the API response is delayed more than 10 seconds
            MyFunctions.progressDialogDismiss()
            // Handle the timeout error here
            // getLeads();
        }


    }

    private suspend fun getIndividualReport(
        _id: String,
        createdDate: String,
        reportHistory: RecyclerView
    ) {

        val token =
            MyFunctions.getSharedPreference(
                requireActivity(),
                Constant.TOKEN,
                Constant.TOKEN
            )!!

        individualReportResponseViewModel.getIndividualReport(token, _id, requireContext())

        try {
            // MyFunctions.progressDialogDismiss()
            val response = individualReportResponseViewModel.response.value?.data

            Log.d("TAG", "getIndividualReport: " + individualReportResponseViewModel.response.value)

            if (individualReportResponseViewModel.response.value != null && response!!.isNotEmpty()) {
                followUpList = response as ArrayList<IndividualReportData>
                val reportHistoryAdapter = ReportHistoryAdapter(
                    requireActivity(),
                    followUpList,
                    object : ReportHistoryAdapter.ReportItemSelectListener {
                        override fun onSelect(position: Int) {
                            TODO("Not yet implemented")
                        }

                        override fun unSelect(position: Int) {
                            TODO("Not yet implemented")
                        }

                        override fun playAudion(url: String) {
                            playAudio(url)
                        }

                        override fun pauseAudion(url: String) {
                            pauseAudio(url)
                        }
                    })

                reportHistory.adapter = reportHistoryAdapter

            }


        } catch (e: java.lang.Exception) {
            // MyFunctions.progressDialogDismiss()
            e.printStackTrace()

        }

    }

    private fun pauseAudio(url: String) {
        mp.pause()
    }


    private fun reportHistoryDialog(_id: String, createdDate: String) {

        val builder =
            AlertDialog.Builder(requireActivity())
        val reportHistoryBinding: ReportHistoryBinding =
            ReportHistoryBinding.inflate(layoutInflater)
        val view = reportHistoryBinding.root
        builder.setView(view)
        val dialog = builder.create()

        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch(Dispatchers.Main) {
            getIndividualReport(_id, createdDate, reportHistoryBinding.reportHistory)
        }

        reportHistoryBinding.createdDate.text = createdDate

        builder.setCancelable(false)
        dialog.setCancelable(true)

        dialog.show()
    }

    private fun dateFilter() {
        val builder =
            AlertDialog.Builder(requireActivity())
        val dateFilterBinding: DialogFilterBinding =
            DialogFilterBinding.inflate(layoutInflater)
        val view = dateFilterBinding.root
        builder.setView(view)

        val dialog = builder.create()

        dateFilterBinding.filterDate.setOnClickListener(View.OnClickListener {
            /*  val datePickerDialog = DatePickerDialog(
                  requireActivity(),
                  { datePicker, year, month, date ->
                      val formattedDate = String.format("%02d-%02d-%04d", date, month + 1, year)
                      dateFilterBinding.filterDate.text = formattedDate
                  }, year, month, day
              )

              Log.d("TAG", "dateFilter: " +System.currentTimeMillis() )

              datePickerDialog.datePicker.minDate = System.currentTimeMillis()
              datePickerDialog.show()
          */

            val calendar = Calendar.getInstance()
            calendar.set(2023, Calendar.APRIL, 1)

            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { datePicker, year, month, date ->
                    val formattedDate = String.format("%02d-%02d-%04d", date, month + 1, year)
                    dateFilterBinding.filterDate.text = formattedDate
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )

            datePickerDialog.datePicker.minDate = calendar.timeInMillis
            datePickerDialog.show()
        })

        dateFilterBinding.submit.setOnClickListener(View.OnClickListener {
            if (dateFilterBinding.filterDate.text.toString().trim().isEmpty()) {
                Toast.makeText(
                    requireActivity(),
                    requireActivity().getString(R.string.please_select_date_or_you_want_clear_the_data_means_click_on_close_icon),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                dialog.dismiss()
                dialog.cancel()

                dateWiseFilterList =
                    modifiedList.filter {
                        MyFunctions.dateFormat(it.modifiedList.updatedAt) == dateFilterBinding.filterDate.text.toString()
                            .trim()
                    } as ArrayList<UpdatedList>


                leadViewModelAdapter.filterList(dateWiseFilterList)
                leadViewModelAdapter.notifyDataSetChanged()
                listCount = leadViewModelAdapter.itemCount.toString()
                if (listCount != "0") {
                    binding.leadList.visibility = VISIBLE
                    binding.noData.visibility = GONE
                } else {
                    binding.leadList.visibility = GONE
                    binding.noData.visibility = VISIBLE
                }


                (requireActivity() as MainActivity).title =
                    MyFunctions.changeToProperCase(intentType) + "(" + listCount + ")"


            }
        })

        dateFilterBinding.dialogCancel.setOnClickListener(View.OnClickListener {
//            leadViewModelAdapter.filterList(modifiedList)
//            leadViewModelAdapter.notifyDataSetChanged()
//            listCount = leadViewModelAdapter.itemCount.toString()
//            (requireActivity() as MainActivity).title =
//                MyFunctions.changeToProperCase(intentType) + "(" + listCount + ")"
            dialog.dismiss()
        })


        builder.setCancelable(false)
        dialog.setCancelable(true)

        dialog.show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            // Checking whether user granted the permission or not.
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                //   Toast.makeText(this, getString(R.string.please_allow_contact_permission), Toast.LENGTH_SHORT).show();
            } else {

                Toast.makeText(
                    requireActivity(),
                    getString(R.string.please_allow_contact_permission),
                    Toast.LENGTH_SHORT
                ).show();
                //  requestPermission(phoneNo)
            }
        }
    }

//    private fun onRecord(start: Boolean) = if (start) {
//        startRecording()
//    } else {
//        stopRecording()
//    }
//
//
//    private fun startRecording() {
//        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
//        val storageDir = context?.cacheDir
//        val outputFile = File(storageDir, "Call_$timestamp.mp3")
//
//
//        recorder = MediaRecorder()
//        recorder?.apply {
//            setAudioSource(MediaRecorder.AudioSource.MIC)
//            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)  // Use MPEG_4 format
//            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)     // Use AAC encoding
//            setOutputFile(outputFile?.absolutePath)
////            prepare()
////            start()
//        }
//
//        try {
//            recorder?.prepare()
//            recorder?.start()
//        } catch (e: IOException) {
//            Log.e("LOG_TAG", "prepare() failed")
//        }
//
//
//    }

    private fun getRecordedFiles(): List<File> {
        val storageDir = context?.cacheDir
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

    fun getRecordedCallFiles(): List<File> {
        val recordedCallFiles: MutableList<File> = mutableListOf()

        // Specify the directory where call recordings are stored
        val directory = File(Environment.getExternalStorageDirectory(), "Call_timestamp")

        if (directory.exists() && directory.isDirectory) {
            val files = directory.listFiles()
            if (files != null) {
                for (file in files) {
                    if (file.isFile) {
                        // Add the file to the list of recorded call files
                        recordedCallFiles.add(file)
                    }
                }
            }
        }

        Log.d("TAG", "getRecordedFiles: " + recordedCallFiles)


        return recordedCallFiles
    }

    private fun playAudio(url: String) {
        try {

            mp.setDataSource(url) //Write your location here
            mp.setOnPreparedListener {
                mp.start() // Start playback when prepared
            }

            mp.prepareAsync()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }


//    private fun stopRecording() {
//        recorder?.apply {
//            try {
//                stop()
//                reset()
//                release()
//            } catch (e: IllegalStateException) {
//                Log.d("TAG", "stopRecording: " + e.message)
//            }
//            recorder = null
//        }
//    }


}
