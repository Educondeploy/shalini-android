package com.tt.skolarrs.view.fragment

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
import com.tt.skolarrs.databinding.FragmentDashBoardBinding
import com.tt.skolarrs.model.DashBoardList
import com.tt.skolarrs.utilz.Constant
import com.tt.skolarrs.utilz.MyFunctions
import com.tt.skolarrs.view.activity.MainActivity
import com.tt.skolarrs.view.adapter.DashBoardAdapter
import com.tt.skolarrs.viewmodel.DashBoardListViewModel
import com.tt.skolarrs.viewmodel.LeadViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import java.net.ConnectException


class DashBoardFragment : Fragment() {
    lateinit var dashBoardAdapter: DashBoardAdapter
    private lateinit var binding: FragmentDashBoardBinding
    var interested: Int = 0
    var hot: Int = 0
    var cold: Int = 0
    var warm: Int = 0
    var notInterested: Int = 0
    var disconnect_the_call: Int = 0
    var switchoff: Int = 0
    var notInterest: Int = 0
    var out_of_coverage: Int = 0
    var invalid_number: Int = 0
    var did_not_pick: Int = 0
    var busy_on_another_call: Int = 0
    var admission_closed: Int = 0
    var token: String = "0"
    var assignedOn: String = ""
    private var backPressedTime: Long = 0
    private val doubleBackToExitInterval = 2000
    private lateinit var leadViewModel: LeadViewModel
    private lateinit var dashViewModel: DashBoardListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val currentTime = System.currentTimeMillis()
                if (currentTime - backPressedTime > doubleBackToExitInterval) {
                    // If it's the first tap, show a toast indicating to double tap to exit
                    Toast.makeText(
                        requireActivity(),
                        requireActivity().getString(R.string.press_once_again_to_exit),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    backPressedTime = currentTime
                } else {
                    // If it's the second tap within the double tap interval, exit the app
                    requireActivity().finishAffinity()
                }
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallback)

        // Add the onBackPressedCallback to the Fragment's OnBackPressedDispatcher

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
        binding = FragmentDashBoardBinding.inflate(inflater, container, false)
        val view = binding.root

        (requireActivity() as MainActivity).title =
            MyFunctions.changeToProperCase(getString(R.string.dashBoard))

        assignedOn =
            MyFunctions.getSharedPreference(requireActivity(), Constant.USER_ID, Constant.USER_ID)!!

        token = MyFunctions.getSharedPreference(requireActivity(), Constant.TOKEN, Constant.TOKEN)!!
        leadViewModel = ViewModelProvider(this)[LeadViewModel::class.java]
        dashViewModel = ViewModelProvider(this)[DashBoardListViewModel::class.java]

        return view


    }

    override fun onResume() {
        super.onResume()
        if (MyFunctions.isConnected(requireActivity())) {
            val scope = CoroutineScope(Dispatchers.IO)

            scope.launch(Dispatchers.Main) {

                    getDashBoardCount()

                dashBoardAdapter = DashBoardAdapter(
                    requireActivity(), dashBoardList(), object :
                        DashBoardAdapter.DashBoardItemSelectListener {
                            override fun onSelect(position: Int) {
                            fragmentListener(position)
                        }

                        override fun unSelect(position: Int) {
                            TODO("Not yet implemented")
                        }
                    })

                binding.dashBoardList.adapter = dashBoardAdapter
            }
        } else {
            Toast.makeText(
                requireActivity(),
                getString(R.string.please_turn_on_your_internet_connection),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun fragmentListener(position: Int) {
        val menuItem = dashBoardList()[position].tittle
        val leadFragment = LeadFragment()
        val bundle = Bundle()

        bundle.putString(Constant.TYPE, menuItem.uppercase())
        leadFragment.arguments = bundle
        leadFragment
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, leadFragment).addToBackStack(null)
            .commit()

    }

    private fun dashBoardList(): List<DashBoardList> {
        var itemList: ArrayList<DashBoardList> = ArrayList()
        val interest = DashBoardList(getString(R.string.interested), interested.toString(), 0)
        itemList.add(interest)
        val hot = DashBoardList(getString(R.string.hot), hot.toString(), 1)
        itemList.add(hot)
        val cold = DashBoardList(getString(R.string.cold), cold.toString(), 2)
        itemList.add(cold)
        val warm = DashBoardList(getString(R.string.warm), warm.toString(), 3)
        itemList.add(warm)
        val notInterest =
            DashBoardList(getString(R.string.not_interested), notInterested.toString(), 4)
        itemList.add(notInterest)
        val disconnectCall =
            DashBoardList(
                getString(R.string.disconnect_the_call),
                disconnect_the_call.toString(),
                5
            )
        itemList.add(disconnectCall)
        val switchOff = DashBoardList(getString(R.string.switchoff), switchoff.toString(), 6)
        itemList.add(switchOff)
        val outOfCoverage =
            DashBoardList(getString(R.string.out_of_coverage), out_of_coverage.toString(), 7)
        itemList.add(outOfCoverage)
        val invalidNumber =
            DashBoardList(getString(R.string.invalid_number), invalid_number.toString(), 8)
        itemList.add(invalidNumber)
        val didNotPick = DashBoardList(getString(R.string.did_not_pick), did_not_pick.toString(), 9)
        itemList.add(didNotPick)
        val busy = DashBoardList(
            getString(R.string.busy_on_another_call),
            busy_on_another_call.toString(),
            10
        )
        itemList.add(busy)
        val closed = DashBoardList("Lead Closed", admission_closed.toString(), 11)
        itemList.add(closed)

        return itemList
    }

    private suspend fun getLeads() {
        hot = 0
        cold = 0
        warm = 0
        interested = 0
        notInterest = 0
        notInterested = 0
        disconnect_the_call = 0
        did_not_pick = 0
        switchoff = 0
        out_of_coverage = 0
        invalid_number = 0
        busy_on_another_call = 0
        admission_closed = 0


        //  MyFunctions.progressDialogShow(requireActivity())
        withTimeoutOrNull(10000L) {
            leadViewModel.getLeads(token, context)
            //ViewModel.getFollowUpList((token))

            try {
                //   MyFunctions.progressDialogDismiss()
                //  val data = leadViewModel.response.value!!.data
                if (leadViewModel.response.value != null) {
                    if (leadViewModel.response.value!!.data.isNotEmpty()) {
                        for (i in leadViewModel.response.value!!.data.indices) {

                            //    if (leadViewModel.response.value!!.data[0].hot != null) {
                            hot += leadViewModel.response.value!!.data[i].hot.size
                            Log.d(
                                "TAG",
                                "getLeads: " + hot
                            )
                            //   }
                            // if (leadViewModel.response.value!!.data[0].cold != null) {
                            cold += leadViewModel.response.value!!.data[i].cold.size
                            //}
                            //  if (leadViewModel.response.value!!.data[0].warm != null) {
                            warm += leadViewModel.response.value!!.data[i].warm.size
                            // }
                            //  if (leadViewModel.response.value!!.data[0].busyOnOtherCall != null) {
                            busy_on_another_call +=
                                leadViewModel.response.value!!.data[i].busyOnOtherCall.size
                            // }
                            //  if (leadViewModel.response.value!!.data[0].outofCoverage != null) {
                            out_of_coverage +=
                                leadViewModel.response.value!!.data[i].outofCoverage.size
                            //  }
                            //if (leadViewModel.response.value!!.data[0].didNotPick != null) {
                            did_not_pick += leadViewModel.response.value!!.data[i].didNotPick.size
                            //}
                            //  if (leadViewModel.response.value!!.data[0].userDisconnectTheCall != null) {
                            disconnect_the_call +=
                                leadViewModel.response.value!!.data[i].userDisconnectTheCall.size
                            // }
                            //     if (leadViewModel.response.value!!.data[0].invalidNumber != null) {
                            invalid_number +=
                                leadViewModel.response.value!!.data[i].invalidNumber.size
                            //    }
                            //    if (leadViewModel.response.value!!.data[0].switchOff != null) {
                            switchoff +=
                                leadViewModel.response.value!!.data[i].switchOff.size
                            //   }
                            //   if (leadViewModel.response.value!!.data[0].switchOff != null) {
                            notInterest +=
                                leadViewModel.response.value!!.data[i].notInterested.size
                            //   }

                            admission_closed += leadViewModel.response.value!!.data[i].complete.size

                        }
                    }
                }

                Log.d("TAG", "getCount: $disconnect_the_call,$notInterest ,$invalid_number")

                val totalInterested = hot.toInt() + cold.toInt() + warm.toInt()
                val totalNotInterested =
                    disconnect_the_call.toInt() + notInterest.toInt() + invalid_number.toInt()
                interested = totalInterested
                notInterested = totalNotInterested


            } catch (e: Exception) {
                //  MyFunctions.progressDialogDismiss()
                val responseError = leadViewModel.responseError.value ?: "Unknown error"

                //   Toast.makeText(requireActivity(), responseError, Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            } catch (e: java.lang.NullPointerException) {
                //   MyFunctions.progressDialogDismiss()
                e.message
            } catch (e: ConnectException) {
                //  MyFunctions.progressDialogDismiss()
                e.message
            }

        } ?: run {
            // Handle the timeout case when the API response is delayed more than 10 seconds
            //  MyFunctions.progressDialogDismiss()
            // Handle the timeout error here
            // getLeads();
        }


    }

    private suspend fun getDashBoardCount() {
        hot = 0
        cold = 0
        warm = 0
        interested = 0
        notInterest = 0
        notInterested = 0
        disconnect_the_call = 0
        did_not_pick = 0
        switchoff = 0
        out_of_coverage = 0
        invalid_number = 0
        busy_on_another_call = 0
        admission_closed = 0


        // MyFunctions.progressDialogShow(requireActivity())
        withTimeoutOrNull(10000L) {
            dashViewModel.getDashBoardCount(token, assignedOn, context)
            //ViewModel.getFollowUpList((token))

            try {
                // MyFunctions.progressDialogDismiss()
                //  val data = leadViewModel.response.value!!.data
                if (dashViewModel.response.value != null) {
                    //  MyFunctions.progressDialogDismiss()
                    if (dashViewModel.response.value!!.data.isNotEmpty()) {
                        //MyFunctions.progressDialogDismiss()
                        for (i in dashViewModel.response.value!!.data.indices) {

                            //    if (leadViewModel.response.value!!.data[0].hot != null) {
                            hot += dashViewModel.response.value!!.data[i].hot
                            Log.d(
                                "TAG",
                                "getLeads: " + hot
                            )
                            //   }
                            // if (leadViewModel.response.value!!.data[0].cold != null) {
                            cold += dashViewModel.response.value!!.data[i].cold
                            //}
                            //  if (leadViewModel.response.value!!.data[0].warm != null) {
                            warm += dashViewModel.response.value!!.data[i].warm
                            // }
                            //  if (leadViewModel.response.value!!.data[0].busyOnOtherCall != null) {
                            busy_on_another_call +=
                                dashViewModel.response.value!!.data[i].busyOnOtherCall
                            // }
                            //  if (leadViewModel.response.value!!.data[0].outofCoverage != null) {
                            out_of_coverage +=
                                dashViewModel.response.value!!.data[i].outofCoverage
                            //  }
                            //if (leadViewModel.response.value!!.data[0].didNotPick != null) {
                            did_not_pick += dashViewModel.response.value!!.data[i].didNotPick
                            //}
                            //  if (leadViewModel.response.value!!.data[0].userDisconnectTheCall != null) {
                            disconnect_the_call +=
                                dashViewModel.response.value!!.data[i].userDisconnectTheCall
                            // }
                            //     if (leadViewModel.response.value!!.data[0].invalidNumber != null) {
                            invalid_number +=
                                dashViewModel.response.value!!.data[i].invalidNumber
                            //    }
                            //    if (leadViewModel.response.value!!.data[0].switchOff != null) {
                            switchoff +=
                                dashViewModel.response.value!!.data[i].switchOff
                            //   }
                            //   if (leadViewModel.response.value!!.data[0].switchOff != null) {
                            notInterest +=
                                dashViewModel.response.value!!.data[i].notInterested
                            //   }

                            interested += dashViewModel.response.value!!.data[i].interested
                            notInterested += dashViewModel.response.value!!.data[i].notInterested

                            admission_closed += dashViewModel.response.value!!.data[i].completed

                        }
                    } else {
                        //   MyFunctions.progressDialogDismiss()
                    }
                } else {
                    //  MyFunctions.progressDialogDismiss()
                }

                //  Log.d("TAG", "getCount: $disconnect_the_call,$notInterest ,$invalid_number")


            } catch (e: Exception) {
                // MyFunctions.progressDialogDismiss()
                val responseError = dashViewModel.responseError.value ?: "Unknown error"

                //   Toast.makeText(requireActivity(), responseError, Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            } catch (e: java.lang.NullPointerException) {
                //  MyFunctions.progressDialogDismiss()
                e.message
            } catch (e: ConnectException) {
                // MyFunctions.progressDialogDismiss()
                e.message
            }

        } ?: run {
            // Handle the timeout case when the API response is delayed more than 10 seconds
            //     MyFunctions.progressDialogDismiss()
            // Handle the timeout error here
            // getLeads();
        }


    }


}