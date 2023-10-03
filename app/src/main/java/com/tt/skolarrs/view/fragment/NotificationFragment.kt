package com.tt.skolarrs.view.fragment

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.tt.skolarrs.R
import com.tt.skolarrs.databinding.FragmentNotificationBinding
import com.tt.skolarrs.model.response.NotificationData
import com.tt.skolarrs.utilz.Constant
import com.tt.skolarrs.utilz.MyFunctions
import com.tt.skolarrs.view.activity.MainActivity
import com.tt.skolarrs.view.activity.SplashScreenActivity
import com.tt.skolarrs.view.adapter.NotificationListAdapter
import com.tt.skolarrs.viewmodel.NotificationListViewModel
import com.tt.skolarrs.viewmodel.UpdateNotificationViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class NotificationFragment : Fragment() {

    lateinit var binding: FragmentNotificationBinding

    lateinit var viewModel : NotificationListViewModel
    lateinit var token : String
    private var notificationData = ArrayList<NotificationData>()
    private lateinit var notifictionListAdapter: NotificationListAdapter

    private lateinit var updateNotificationViewModel: UpdateNotificationViewModel


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
        binding = FragmentNotificationBinding.inflate(inflater,container, false)
        val view = binding.root

        (requireActivity() as MainActivity).title = MyFunctions.changeToProperCase(getString(R.string.notification))

        token = MyFunctions.getSharedPreference(requireActivity(), Constant.TOKEN, Constant.TOKEN)!!
        viewModel = ViewModelProvider(this)[NotificationListViewModel::class.java]
        updateNotificationViewModel = ViewModelProvider(this)[UpdateNotificationViewModel::class.java]


        return view
    }

    override fun onResume() {
        super.onResume()
        if (MyFunctions.isConnected(requireActivity())) {
            val scope = CoroutineScope(Dispatchers.IO)
            scope.launch(Dispatchers.Main) {

                getNotificationList()


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

    private suspend fun getNotificationList() {
        notificationData.clear()
       MyFunctions.progressDialogShow(requireActivity())
        viewModel.getNotificationList(token, context)

        try { MyFunctions.progressDialogDismiss()
            val data = viewModel.response.value?.data
            val response = viewModel.response.value?.data

            if (response!!.isNotEmpty()) {
                binding.container.visibility = View.VISIBLE
                binding.noData.visibility = View.GONE
                notificationData = response as ArrayList<NotificationData>
                    var count: Int = 0
                for (i in response.indices) {
                    if (!response[i].read) {
                        count++
                    }
                }

                MyFunctions.setSharedPreference(requireActivity(),Constant.NOTIFICATION_COUNT,count.toString())

            }
            else {
                binding.container.visibility = View.GONE
                binding.noData.visibility = View.VISIBLE
            }

            val filterData = notificationData.filter { !it.read }

            if (filterData.isEmpty()) {
                binding.container.visibility = View.GONE
                binding.noData.visibility = View.VISIBLE
            }
            else {
                binding.container.visibility = View.VISIBLE
                binding.noData.visibility = View.GONE
            }
            Log.d("TAG", "getNotificationList: " + filterData.size)

            notifictionListAdapter = NotificationListAdapter(requireActivity(),filterData, object : NotificationListAdapter.NotificationSelectListener{
                override fun onSelect(position: Int) {
                    TODO("Not yet implemented")
                }

                override fun unSelect(position: Int) {
                    TODO("Not yet implemented")
                }

                override fun updateNotification(notificationList: NotificationData) {
                    requestPermission(notificationList)

                }
            })

            binding.container.adapter = notifictionListAdapter


        }
        catch (e:java.lang.Exception) {
            MyFunctions.progressDialogDismiss()
            e.printStackTrace()
            binding.container.visibility = View.GONE
            binding.noData.visibility = View.VISIBLE
        }
    }

    private suspend fun readNotification(notificationList: NotificationData) {
        var token = MyFunctions.getSharedPreference(requireActivity(), Constant.TOKEN, Constant.TOKEN)!!
        updateNotificationViewModel.updateNotification(notificationList._id, token, context)

        try {
            if (updateNotificationViewModel.response.value != null) {
                val response = updateNotificationViewModel.response
                if (notificationList.notification?.data?.type == "followUp") {
                    Log.d("TAG", "readNotification: " + notificationList.notification.data.followUpData.previousStatus)
                    requireActivity().startActivity(
                        Intent(requireActivity(), SplashScreenActivity::class.java)
                            .putExtra(
                                Constant.LEAD_ID,
                                notificationList.notification.data.followUpData.leadId._id
                            )
                            .putExtra(
                                Constant.LEAD_NUMBER,
                                notificationList.notification.data.followUpData.leadId.mobileNo
                            )
                            .putExtra(
                                Constant.LEAD_TYPE,
                                notificationList.notification.data.followUpData.previousStatus
                            )
                            .putExtra(
                                Constant.LEAD_NAME,
                                notificationList.notification.data.followUpData.leadId.name
                            ).putExtra(
                                Constant.TYPE,
                                "followUp"
                            ).putExtra(
                                Constant.NOTIFICATION_ID,
                                1
                            )
                    )

                }
                if (notificationList.notification.data.type == "leave") {
                    requireActivity().startActivity(
                        Intent(requireActivity(), MainActivity::class.java).putExtra(
                            Constant.NOTIFICATION_TYPE,
                            "leave"
                        )
                    )
                }
                if (notificationList.notification?.data?.type == "permission") {
                    requireActivity().startActivity(
                        Intent(requireActivity(), MainActivity::class.java).putExtra(
                            Constant.NOTIFICATION_TYPE,
                            "permission"
                        )
                    )
                }
                if (notificationList.notification?.data?.type == "lead") {
                    requireActivity().startActivity(
                        Intent(requireActivity(), MainActivity::class.java).putExtra(
                            Constant.NOTIFICATION_TYPE,
                            "lead"
                        )
                    )
                }

                //    context.startActivity(Intent(context, LoginActivity::class.java))
            } else {
                if (updateNotificationViewModel.responseError.value != null) {
                    Toast.makeText(
                        requireActivity(),
                        updateNotificationViewModel.responseError.value!!,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        } catch (e: Exception) {
            //  Toast.makeText(context, updateNotificationViewModel.responseError.value!!, Toast.LENGTH_SHORT).show()
            Log.d("TAG", "getLeads: " + e.message)
            e.printStackTrace()
        } catch (e: java.lang.NullPointerException) {
            e.message
            Log.d("TAG", "getLeads: " + e.message)
        }


    }

    private fun requestPermission(notificationList: NotificationData) {
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
        }

        else {
            if (MyFunctions.isConnected(requireActivity())) {
                val scope = CoroutineScope(Dispatchers.IO)
                scope.launch(Dispatchers.Main) {
                    readNotification(notificationList)

                }
            } else {
                Toast.makeText(
                    requireActivity(),
                    requireActivity().getString(R.string.please_turn_on_your_internet_connection),
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }
    }

//    private fun requestPermission(notificationList: NotificationData) {
//        if (requireActivity().checkSelfPermission(Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED && requireActivity().checkSelfPermission(
//                Manifest.permission.WRITE_CALL_LOG
//            ) != PackageManager.PERMISSION_GRANTED && requireActivity().checkSelfPermission(
//                Manifest.permission.READ_CONTACTS
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            requestPermissions(
//                arrayOf(
//                    Manifest.permission.WRITE_CALL_LOG,
//                    Manifest.permission.READ_CALL_LOG,
//                    Manifest.permission.READ_CONTACTS,
//                ), 1
//            )
//        }
//        else {
//            if (MyFunctions.isConnected(requireActivity())) {
//                val scope = CoroutineScope(Dispatchers.IO)
//                scope.launch(Dispatchers.Main) {
//                    readNotification(notificationList)
//
//                }
//            } else {
//                Toast.makeText(
//                    requireActivity(),
//                    requireActivity().getString(R.string.please_turn_on_your_internet_connection),
//                    Toast.LENGTH_SHORT
//                )
//                    .show()
//            }
//        }
//
//    }

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
            }
        }
    }


}