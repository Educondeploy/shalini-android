package com.tt.skolarrs.view.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.View.OnClickListener
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.messaging.FirebaseMessaging
import com.tt.skolarrs.R
import com.tt.skolarrs.com.github.axet.callrecorder.services.TileService
import com.tt.skolarrs.databinding.ActivityMainBinding
import com.tt.skolarrs.model.Menu
import com.tt.skolarrs.model.SubMenu
import com.tt.skolarrs.service.LogoutService
import com.tt.skolarrs.utilz.Constant
import com.tt.skolarrs.utilz.MyFunctions
import com.tt.skolarrs.view.adapter.ItemSelectListener
import com.tt.skolarrs.view.adapter.MenuAdapter
import com.tt.skolarrs.view.fragment.*
import com.tt.skolarrs.viewmodel.LogoutViewModel
import com.tt.skolarrs.viewmodel.MainViewModel
import com.tt.skolarrs.viewmodel.NotificationListViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : BaseActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var menuAdapter: MenuAdapter
    lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    private var backPressedTime: Long = 0
    private val doubleBackToExitInterval = 2000
    private lateinit var viewModel: MainViewModel
    private lateinit var logoutViewModel: LogoutViewModel
    lateinit var notificationViewModel: NotificationListViewModel
    private var selectedPosition: Int = 0
    var currentFragment: Fragment? = DashBoardFragment()
    private var listType: String = Constant.MENU_LIST
    private var intentType: String = ""

    var lead_id: String = ""
    var mobileNumber: String = ""
    var type: String = ""
    var lead_type: String = ""
    var lead_name: String = ""
    lateinit var MOBILE_NO: String

    companion object {
        @JvmStatic
        fun startActivity(tileService: TileService, b: Boolean) {

        }

        @kotlin.jvm.JvmField
        var MUST: Array<String> = arrayOf(
            Manifest.permission.RECORD_AUDIO
        )

    }





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        startService(Intent(this, LogoutService::class.java))

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        logoutViewModel = ViewModelProvider(this)[LogoutViewModel::class.java]

        notificationViewModel = ViewModelProvider(this)[NotificationListViewModel::class.java]

        val empName = MyFunctions.getSharedPreference(
            this@MainActivity,
            Constant.USER_NAME,
            Constant.USER_NAME
        )

        binding.empName.text = MyFunctions.changeToProperCase(empName!!)

        //Idle time logout

        val customActionBar = layoutInflater.inflate(R.layout.custom_actionbar, null)

        supportActionBar?.setDisplayShowCustomEnabled(true)
        supportActionBar?.customView = customActionBar

        val filter = customActionBar.findViewById<ImageView>(R.id.filterIcon)
        val notificationCount = customActionBar.findViewById<TextView>(R.id.notificationCount)
        val titleTextView = customActionBar.findViewById<TextView>(R.id.custom_title)
        val iconImageView = customActionBar.findViewById<ImageView>(R.id.custom_icon)

        val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy")
        val currentDate: String = simpleDateFormat.format(Date())

        titleTextView.text = currentDate

        actionBarDrawerToggle =
            ActionBarDrawerToggle(this, binding.drawer, R.string.nav_open, R.string.nav_close)
        binding.drawer.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        iconImageView.setOnClickListener(OnClickListener {
            binding.drawer.closeDrawer(GravityCompat.START)
            showFragment(NotificationFragment())
        })

          Log.d("TAG", "onCreate: intentType" + intent.getStringExtra(Constant.NOTIFICATION_TYPE))

        if (intent.getStringExtra(Constant.NOTIFICATION_TYPE) != null) {
            intentType = intent.getStringExtra(Constant.NOTIFICATION_TYPE)!!
            Log.d("TAG", "onCreate: Main Intent Tye $intentType")
            when (intentType) {
                "permission" -> {
                    currentFragment = PermissionListFragment()
                }
                "leave" -> {
                    currentFragment = LeaveListFragment()
                }
                "lead" -> {
                    currentFragment = LeadFragment()
                }
                "followUp" -> {
                    currentFragment = NotificationFragment()
                }

            }

        }

        setAdapter()
        askNotificationPermission()
        requestPermission()

        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch(Dispatchers.Main) {
            saveFcm()
            getNotificationList(notificationCount)
        }


    }

    private fun requestPermission() {
        if (this@MainActivity.checkSelfPermission(Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED && this@MainActivity.checkSelfPermission(
                Manifest.permission.WRITE_CALL_LOG
            ) != PackageManager.PERMISSION_GRANTED && this@MainActivity.checkSelfPermission(
                Manifest.permission.READ_CONTACTS
            ) != PackageManager.PERMISSION_GRANTED &&  this@MainActivity.checkSelfPermission(
                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.WRITE_CALL_LOG,
                    Manifest.permission.READ_CALL_LOG,
                    Manifest.permission.READ_CONTACTS,

                    Manifest.permission.RECORD_AUDIO,
                ), 1
            )
        }

    }

    override fun onResume() {
        super.onResume()
        //  autoLogoutTimer.onUserAction()
        /* MyFunctions.setSharedPreference(this, Constant.LEAD_ID, "")
         MyFunctions.setSharedPreference(this, Constant.LEAD_TYPE, "")
         MyFunctions.setSharedPreference(this, Constant.LEAD_NAME, "")
         MyFunctions.setSharedPreference(this, Constant.LEAD_NUMBER, "")*/
        binding.drawer.closeDrawer(GravityCompat.START)
    }

    private suspend fun saveFcm() {
        //   MyFunctions.progressDialogShow(this@MainActivity)
        val fcmId =
            MyFunctions.getSharedPreference(this, Constant.FIREBASE_TOKEN, Constant.FIREBASE_TOKEN)
        val token = MyFunctions.getSharedPreference(this, Constant.TOKEN, Constant.TOKEN)

        viewModel.saveFcm(token!!, fcmId!!, applicationContext)

        val response = viewModel.response
        val failes = viewModel.responseError

        try {
            //       MyFunctions.progressDialogDismiss()
            if (response.value != null) {
                Log.d("TAG", "saveFcm: ${response.value!!.message}")
            } else {
                val responseError = viewModel.responseError.value!!
                Log.d("TAG", "saveFcm: $responseError")
            }

        } catch (e: Exception) {
            //    MyFunctions.progressDialogDismiss()
            e.printStackTrace()
        }

    }

    private fun fragmentListener(position: Int) {
        val menuItem = menuList()[position].tittle
        if (menuItem != getString(R.string.follow_up) && menuItem != getString(R.string.permission) && menuItem != getString(
                R.string.leave
            )
        ) {
            binding.drawer.closeDrawer(GravityCompat.START)
            menuFragment(menuItem)
        }
    }

    private fun subFragmentListener(position: Int, subMenuSelectedTitle: String) {
        val menuItem = menuList()[position].tittle
        var followUpItem: String = ""
        var followUpId: Int = 0
        if (subMenuSelectedTitle == getString(R.string.follow_up)) {
            followUpItem = followUpList()?.get(position)?.tittle.toString()
            followUpId = followUpList()?.get(position)?.id!!
        }
        if (subMenuSelectedTitle == getString(R.string.permission)) {
            followUpItem = permissionList()?.get(position)?.tittle.toString()
            followUpId = permissionList()?.get(position)?.id!!
        }
        if (subMenuSelectedTitle == getString(R.string.leave)) {
            followUpItem = leaveList()?.get(position)?.tittle.toString()
            followUpId = leaveList()?.get(position)?.id!!
        }

        subMenuFragment(followUpItem, followUpId)

    }

    private fun subMenuFragment(followUpItem: String?, followUpId: Int) {
        binding.drawer.closeDrawer(GravityCompat.START)

        val fragment = when (followUpId) {
            0 -> LeadFragment()
            1 -> LeadFragment()
            2 -> LeadFragment()
            3 -> LeadFragment()
            4 -> LeadFragment()
            5 -> PermissionListFragment()
            6 -> LeaveListFragment()
            7 -> CalenderViewFragment()

            else -> {
                LeadFragment()
            }
        }

        val bundle = Bundle()
        bundle.putString(Constant.TYPE, followUpItem?.uppercase())
        fragment.arguments = bundle

        showFragment(fragment)

    }

    private fun menuFragment(menuItem: String) {
        var fragment: Fragment = currentFragment!!

        Log.d("TAG", "menuFragment: " + menuItem)
        fragment = when (menuItem) {

            getString(R.string.lead) -> {
                val leadFragment = LeadFragment()
                val bundle = Bundle()
                bundle.putString(Constant.TYPE, Constant.LEAD)
                leadFragment.arguments = bundle
                leadFragment
            }
            "Settings" -> {
                val leadFragment = SettingsFragment();
//                val bundle = Bundle()
//                bundle.putString(Constant.TYPE, Constant.LEAD)
//                leadFragment.arguments = bundle
                leadFragment
            }

            getString(R.string.dashBoard) -> DashBoardFragment()

            getString(R.string.report) -> ReportFragment()

            getString(R.string.notification) -> NotificationFragment()

                getString(R.string.admission_closed) -> {
                val leadFragment = LeadFragment()
                val bundle = Bundle()
                bundle.putString(Constant.TYPE, getString(R.string.admission_closed).uppercase())
                leadFragment.arguments = bundle
                leadFragment
            }

            "LEAD CLOSED"-> {
                val leadFragment = LeadFragment()
                val bundle = Bundle()
                bundle.putString(Constant.TYPE, getString(R.string.admission_closed).uppercase())
                leadFragment.arguments = bundle
                leadFragment
            }

            else -> /*throw IllegalArgumentException("Invalid menu item name") */  fragment
        }

        showFragment(fragment)


    }

    private fun showFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment).addToBackStack(null)
            .commit()

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            true
        } else super.onOptionsItemSelected(item)
    }

    private fun setAdapter() {

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, currentFragment!!)
            .commit()


        menuAdapter = MenuAdapter(
            this@MainActivity,
            menuList(),
            Constant.MENU_LIST,
            object : ItemSelectListener {
                override fun onSelect(position: Int) {
                    Log.d("TAG", "onSelect: " + position)
                    menuList()[position].isSelected = true
                    selectedPosition = position
                    listType = Constant.MENU_LIST
                    fragmentListener(selectedPosition)

                }

                override fun unSelect(position: Int) {
                    menuList()[position].isSelected = false
                }

                override fun subMenuSelect(position: Int, subMenuSelectedTitle: String) {
                    selectedPosition = position
                    subFragmentListener(selectedPosition, subMenuSelectedTitle)
                }

                override fun logout() {
                    if (MyFunctions.isConnected(this@MainActivity)) {
                        val scope = CoroutineScope(Dispatchers.IO)
                        scope.launch(Dispatchers.Main) {
                            logOut1()

                        }
                    } else {
                        /* Toast.makeText(
                             this@MainActivity,
                             getString(R.string.please_turn_on_your_internet_connection),
                             Toast.LENGTH_SHORT
                         )
                             .show()*/
                    }
                }

            })


        binding.menuList.adapter = menuAdapter


        binding.cancel.setOnClickListener(OnClickListener {
            binding.drawer.closeDrawer(GravityCompat.START)
        })
        /* binding.navOpen.setOnClickListener(OnClickListener {
             binding.drawer.openDrawer(GravityCompat.START)
         })*/


    }

    private fun menuList(): ArrayList<Menu> {
        val itemList: ArrayList<Menu> = ArrayList()
        val subMenuList: ArrayList<SubMenu> = ArrayList()
        val section = Menu(
            R.drawable.ic_calendar,
            getString(R.string.dashBoard), false, 0,
            subMenuList
        )
        itemList.add(section)
        val section1 = Menu(
            R.drawable.ic_calendar,
            getString(R.string.lead), false, 1,
            subMenuList
        )
        itemList.add(section1)
        val followUp = Menu(
            R.drawable.ic_calendar,
            getString(R.string.follow_up), false, 2,
            followUpList() as ArrayList<SubMenu>
        )
        itemList.add(followUp)
        val admission = Menu(
            R.drawable.ic_calendar,
           "LEAD CLOSED", false, 3,
            subMenuList

        )
        itemList.add(admission)
        val permission = Menu(
            R.drawable.ic_permission,
            getString(R.string.permission), false, 4,
            permissionList() as ArrayList<SubMenu>
        )
        itemList.add(permission)
        val leave = Menu(
            R.drawable.ic_calendar,
            getString(R.string.leave), false, 5,
            leaveList() as ArrayList<SubMenu>
        )
        itemList.add(leave)
        /*  val report = Menu(
              R.drawable.ic_permission,
              getString(R.string.report), false, 7,
              subMenuList
          )
          itemList.add(report)

          val notification = Menu(
              R.drawable.notification,
              getString(R.string.notification), false, 8,
              subMenuList
          )
          itemList.add(notification)*/

        val settings = Menu(
            R.drawable.ic_logout,
            "Settings", false, 10,
            subMenuList
        )
        itemList.add(settings)

        val logOut = Menu(
            R.drawable.ic_logout,
            getString(R.string.logout), false, 6,
            subMenuList
        )
        itemList.add(logOut)

        return itemList
    }

    private fun followUpList(): ArrayList<SubMenu>? {
        var itemList: ArrayList<SubMenu> = ArrayList()
        val section = SubMenu(0, getString(R.string.interest))
        itemList.add(section)
        val section1 = SubMenu(1, getString(R.string.switchOff))
        itemList.add(section1)
        val section2 =
            SubMenu(2, getString(R.string.out_of_coverage))
        itemList.add(section2)
        val section3 = SubMenu(3, getString(R.string.did_not_pick))
        itemList.add(section3)
        val section4 =
            SubMenu(4, getString(R.string.busy_on_another_call))
        itemList.add(section4)
        return itemList
    }

    private fun permissionList(): ArrayList<SubMenu>? {
        var itemList: ArrayList<SubMenu> = ArrayList()
        val section = SubMenu(5, getString(R.string.apply))
        itemList.add(section)
        return itemList
    }

    private fun leaveList(): ArrayList<SubMenu>? {
        var itemList: ArrayList<SubMenu> = ArrayList()
        val section = SubMenu(6, getString(R.string.apply))
        itemList.add(section)
        val section1 = SubMenu(7, getString(R.string.view_calender))
        itemList.add(section1)
        return itemList
    }

    // Declare the launcher at the top of your Activity/Fragment:
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // FCM SDK (and your app) can post notifications.
        } else {
            // TODO: Inform user that that your app will not show notifications.
        }
    }

    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                // TODO: display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private suspend fun logOut1() {
        val id = MyFunctions.getSharedPreference(this, Constant.USER_ID, Constant.USER_ID)!!
        logoutViewModel.logout(id, applicationContext)

        try {
            if (logoutViewModel.response.value != null) {
                val response = logoutViewModel.response
                FirebaseMessaging.getInstance().deleteToken()
                Toast.makeText(
                    this,
                    logoutViewModel.response.value!!.message,
                    Toast.LENGTH_SHORT
                ).show()
                MyFunctions.setSharedPreference(this@MainActivity, Constant.TOKEN, "")
                MyFunctions.setSharedPreference(this@MainActivity, Constant.ID, "")
                MyFunctions.setSharedPreference(this@MainActivity, Constant.isloggedIn, false)
                MyFunctions.setSharedPreference(this@MainActivity, Constant.USER_ID, "")
                this.startActivity(Intent(this, SplashScreenActivity::class.java))
                finish()
            } else {
                if (logoutViewModel.responseError.value != null) {
                    Toast.makeText(
                        this,
                        "success",
                        Toast.LENGTH_SHORT
                    ).show()
                    MyFunctions.setSharedPreference(this@MainActivity, Constant.TOKEN, "")
                    MyFunctions.setSharedPreference(this@MainActivity, Constant.ID, "")
                    MyFunctions.setSharedPreference(this@MainActivity, Constant.isloggedIn, false)
                    MyFunctions.setSharedPreference(this@MainActivity, Constant.USER_ID, "")
                    this.startActivity(Intent(this, SplashScreenActivity::class.java))
                    finish()
                }
            }

        } catch (e: Exception) {
//            Toast.makeText(this, logoutViewModel.responseError.value!!, Toast.LENGTH_SHORT)
//                .show()
            MyFunctions.setSharedPreference(this@MainActivity, Constant.TOKEN, "")
            MyFunctions.setSharedPreference(this@MainActivity, Constant.ID, "")
            MyFunctions.setSharedPreference(this@MainActivity, Constant.isloggedIn, false)
            MyFunctions.setSharedPreference(this@MainActivity, Constant.USER_ID, "")
            this.startActivity(Intent(this, SplashScreenActivity::class.java))
            finish()
            e.printStackTrace()
        } catch (e: java.lang.NullPointerException) {
            e.message
        }
    }

    private fun saveAppClosed(isClosed: Boolean) {
        val prefs = getSharedPreferences("AutoLogoutTimerPrefs", Context.MODE_PRIVATE)
        prefs.edit().putBoolean("app_closed", isClosed).apply()
    }

    private suspend fun getNotificationList(notificationCount: TextView) {
       // MyFunctions.progressDialogShow(this)

        val token = MyFunctions.getSharedPreference(this, Constant.TOKEN, Constant.TOKEN)

        notificationViewModel.getNotificationList(token!!, applicationContext)

        try {
          //  MyFunctions.progressDialogDismiss()
            val data = notificationViewModel.response.value?.data
            val response = notificationViewModel.response.value?.data
            val message = notificationViewModel.response.value!!.message;



            if (response!!.isNotEmpty()) {

                var count: Int = 0
                for (i in response.indices) {
                    if (!response[i].read) {
                        count++
                    }
                }

                MyFunctions.setSharedPreference(
                    this@MainActivity,
                    Constant.NOTIFICATION_COUNT,
                    count.toString()
                )

                val unReadNotificationCount = MyFunctions.getSharedPreference(
                    this@MainActivity,
                    Constant.NOTIFICATION_COUNT,
                    "0"
                )

                if (unReadNotificationCount!!.isNotEmpty() && unReadNotificationCount != "0") {
                    notificationCount.visibility = View.VISIBLE
                    notificationCount.text = unReadNotificationCount
                } else {
                    notificationCount.visibility = View.GONE
                }

            } else {
                Log.d("TAG", "getNotificationList: ")
            }


        } catch (e: java.lang.Exception) {
            logOut1();
          //  MyFunctions.progressDialogDismiss()
            e.printStackTrace()
            Log.d("TAG", "getNotificationList:2323 ")

        }
    }

    /*  override fun logout() {
          val scope = CoroutineScope(Dispatchers.IO)
          scope.launch(Dispatchers.Main) {
              logOut()
          }
      }*/



}