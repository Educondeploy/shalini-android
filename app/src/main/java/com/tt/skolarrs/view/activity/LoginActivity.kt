package com.tt.skolarrs.view.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.*
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import com.tt.skolarrs.R
import com.tt.skolarrs.databinding.ActivityLoginBinding
import com.tt.skolarrs.utilz.Constant
import com.tt.skolarrs.utilz.MyFunctions
import com.tt.skolarrs.viewmodel.LoginViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*


class LoginActivity : AppCompatActivity(), LocationListener {

    lateinit var binding: ActivityLoginBinding

    private lateinit var viewModel: LoginViewModel

    lateinit var token: String

    lateinit var lat: String

    lateinit var long: String
    var address: String = ""
    var deviceId: String = ""
    val coords: MutableList<String> = mutableListOf()


    private lateinit var mFusedLocationClient: FusedLocationProviderClient


    @SuppressLint("HardwareIds")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        deviceId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)

        FirebaseApp.initializeApp(this@LoginActivity)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getLocation("")
//        if(checkPermissions()) {
//
//        }
//        else {
//            Toast.makeText(this, "Please turn on location ", Toast.LENGTH_LONG).show()
////            val intent = Intent(Settings.ACTION_APPLICATION_SETTINGS)
////            val uri: Uri = Uri.parse("package:"+ "com.tt.educon" )
////            intent.data = uri
////            startActivity(intent)
//
////            val i = Intent(
////                Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse(
////                    "package:com.tt.educon"
////                )
////            )
////            finish()
////            startActivity(i)
//        }


        viewModel = ViewModelProvider(this)[LoginViewModel::class.java]

        binding.submit.setOnClickListener {
            MyFunctions.progressDialogShow(this)
            Log.d("TAG", "onCreate: " +checkPermissions() )
            if(checkPermissions()) {
                MyFunctions.progressDialogDismiss()
                if (isValid()) {

                    getLocation("")
                    if (MyFunctions.isConnected(this@LoginActivity)) {
                        val scope = CoroutineScope(Dispatchers.IO)
                        scope.launch(Dispatchers.Main) {
                          //  MyFunctions.progressDialogShow(this@LoginActivity)
                            if (isLocationEnabled()) {
                                getLocation("login")
                                // delay(2000)
                               // login()
                            } else {
                                MyFunctions.progressDialogDismiss()
                                Toast.makeText(
                                    this@LoginActivity,
                                    "Please turn on location then try login",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        }
                    } else {
                        Toast.makeText(
                            this@LoginActivity,
                            getString(R.string.please_turn_on_your_internet_connection),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            else {
                MyFunctions.progressDialogDismiss()
                Toast.makeText(this,"Please allow all permission then try", Toast.LENGTH_SHORT).show()
            }


        }

        FirebaseMessaging.getInstance().token.addOnSuccessListener { s ->
            Log.d("TAG", "onSuccess: $s")
            MyFunctions.setSharedPreference(applicationContext, Constant.FIREBASE_TOKEN, s)
        }


    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun checkPermissions(): Boolean {
        val requiredPermissions = arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.WRITE_CALL_LOG,
        Manifest.permission.READ_CALL_LOG,
        Manifest.permission.READ_CONTACTS,
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.CALL_PHONE,
        )
        val anyPermissionNotGranted = requiredPermissions.all { permission ->
            ActivityCompat.checkSelfPermission(this,permission) == PackageManager.PERMISSION_GRANTED
        }
        return anyPermissionNotGranted
//        if (ActivityCompat.checkSelfPermission(
//                this,
//                Manifest.permission.ACCESS_COARSE_LOCATION
//            ) == PackageManager.PERMISSION_GRANTED &&
//            ActivityCompat.checkSelfPermission(
//                this,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
//                this,
//                Manifest.permission.READ_CALL_LOG
//            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
//                this, Manifest.permission.WRITE_CALL_LOG
//            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
//                this, Manifest.permission.READ_CONTACTS
//            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
//                this,
//                Manifest.permission.RECORD_AUDIO
//            ) == PackageManager.PERMISSION_GRANTED
//            && ActivityCompat.checkSelfPermission(
//                this,
//                Manifest.permission.CALL_PHONE
//            ) == PackageManager.PERMISSION_GRANTED
//
//        ) {
//            return true
//        }
//        return false
    }

    private fun requestPermissions() {
        val requiredPermissions = arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_CALL_LOG,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CALL_PHONE,
        )
        val anyPermissionNotGranted = requiredPermissions.all { permission ->
            ActivityCompat.checkSelfPermission(this,permission) == PackageManager.PERMISSION_GRANTED
        }



        Log.d("TAG", "requestPermission: " + anyPermissionNotGranted)

        if (!anyPermissionNotGranted) {
            requestPermissions(requiredPermissions, 1)
        }
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.WRITE_CALL_LOG,
                Manifest.permission.READ_CALL_LOG,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.CALL_PHONE,
                Manifest.permission.PROCESS_OUTGOING_CALLS
            ),
            1
        )
    }

    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == 1) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLocation("")
            }
        } else {
            Toast.makeText(this, "Please turn on location", Toast.LENGTH_LONG).show()

            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
        }

    }

    @SuppressLint("MissingPermission", "SetTextI18n")
    private fun getLocation(type: String ) {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    val location: Location? = task.result
                    if (location != null) {
                        val geocoder = Geocoder(this, Locale.getDefault())

//                        val list: List<Address>? =
//                            geocoder.getFromLocation(location.latitude, location.longitude, 1)
//                        lat = list!![0].latitude.toString()
//                        long = list!![0].longitude.toString()
//                        address = list!![0].getAddressLine(0).toString()
//                        coords?.add(lat)
//                        coords?.add(long)

                        val list: List<Address>? =
                            geocoder.getFromLocation(location.latitude, location.longitude, 1)
                        if (list != null && list.isNotEmpty()) {
                            lat = list[0].latitude.toString()
                            long = list[0].longitude.toString()
                            address = list[0].getAddressLine(0).toString()
                            coords?.add(lat)
                            coords?.add(long)
                            val scope = CoroutineScope(Dispatchers.IO)
                            scope.launch(Dispatchers.Main) {
                                if(type.equals("login")) {


                                login() } else {}
                            }

                        } else {
                            Toast.makeText(this, "Your location not found", Toast.LENGTH_LONG)
                                .show()
                        }


                        Log.d("TAG", "getLocation: " + lat + long + address)

                    }
                }
            } else {
                Toast.makeText(this, "Please turn on location", Toast.LENGTH_LONG).show()
//                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
//                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }


    private suspend fun login() {
        MyFunctions.progressDialogShow(this)
        val email = binding.email.editText?.text.toString().trim()
        val password = binding.password.editText?.text.toString().trim()

        viewModel.login(email, password, deviceId, coords!!, address)

        Log.d("TAG", "login: " + email + coords + address)

        val response = viewModel.response
        val failes = viewModel.failedResponse
        try {
            MyFunctions.progressDialogDismiss()
            if (response.value != null) {
                Toast.makeText(this@LoginActivity, response.value!!.message, Toast.LENGTH_SHORT)
                    .show()
                token = response.value!!.data.token

                /*   val intent = Intent(this, TimerService::class.java)
                   ContextCompat.startForegroundService(this, intent)*/

                MyFunctions.setSharedPreference(this@LoginActivity, Constant.TOKEN, token)
                MyFunctions.setSharedPreference(
                    this@LoginActivity,
                    Constant.ID,
                    response.value!!.data.user._id
                )
                MyFunctions.setSharedPreference(
                    this@LoginActivity,
                    Constant.isloggedIn,
                    true
                )
                MyFunctions.setSharedPreference(
                    this@LoginActivity,
                    Constant.USER_ID,
                    response.value!!.data.user.userID
                )
                MyFunctions.setSharedPreference(
                    this@LoginActivity,
                    Constant.USER_NAME,
                    response.value!!.data.user.empFirstName
                )
                MyFunctions.setSharedPreference(
                    this@LoginActivity,
                    Constant.USER_NUMBER,
                    response.value!!.data.user.userID
                )

                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                finish()
            } else {
                MyFunctions.progressDialogDismiss()
                val responseError = viewModel.failedResponse.value!!
                Toast.makeText(this@LoginActivity, responseError, Toast.LENGTH_SHORT).show()
            }

        } catch (e: Exception) {
            MyFunctions.progressDialogDismiss()
            //   val responseError = viewModel.failedResponse.value!!
            //  Toast.makeText(this@LoginActivity, responseError, Toast.LENGTH_SHORT).show()
            Log.d("TAG", "getLeads: " + e.message)
            e.printStackTrace()
        }

    }

    private fun isValid(): Boolean {
        Log.d("TAG", "isValid: " + address)
        if (MyFunctions.isEmpty(binding.email.editText!!, this@LoginActivity)) {
            return false
        }
        /*  if (MyFunctions.emailPattern(binding.email.editText!!, this@LoginActivity)) {
              return false
          }*/
        if (MyFunctions.isEmpty(binding.password.editText!!, this@LoginActivity)) {
            return false
        }
//        if (address.isEmpty()) {
//            Toast.makeText(this@LoginActivity, "Please allow location permission go to the app permission and allow location permission", Toast.LENGTH_SHORT).show()
//           // requestPermissions()
//            return false
//        }
        /*if (MyFunctions.passwordLength(binding.password.editText!!, this@LoginActivity)) {
            return false
        }*/

        return true
    }

    override fun onLocationChanged(location: Location) {
        Log.d("TAG", "onLocationChanged: " + location)
    }

}

