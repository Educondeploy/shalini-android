package com.tt.skolarrs.view.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.tt.skolarrs.R
import com.tt.skolarrs.databinding.ActivityShareBinding
import com.tt.skolarrs.utilz.Constant
import com.tt.skolarrs.utilz.MyFunctions

class ShareActivity : AppCompatActivity() {

    lateinit var binding: ActivityShareBinding
    var number: String = ""
    var name: String = ""
    var status: String = ""
    var duration: String = ""
    var leadId: String = ""
    var type: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShareBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.include.title.text = getString(R.string.share)

        binding.include.backArrow.visibility = View.GONE

        binding.include.backArrow.setOnClickListener(View.OnClickListener {
            onBackPressed()
        })

        number = MyFunctions.getSharedPreference(
            this@ShareActivity,
            Constant.LEAD_NUMBER,
            Constant.LEAD_NUMBER
        )!!
        name = MyFunctions.getSharedPreference(
            this@ShareActivity,
            Constant.LEAD_NAME,
            Constant.LEAD_NAME
        )!!
        leadId = MyFunctions.getSharedPreference(
            this@ShareActivity,
            Constant.LEAD_ID,
            Constant.LEAD_ID
        )!!

        if (intent.getStringExtra(Constant.DURATION) != null) {
            duration = intent.getStringExtra(Constant.DURATION)!!
        }
        if (intent.getStringExtra(Constant.LEAD_STATUS) != null) {
            status = intent.getStringExtra(Constant.LEAD_STATUS)!!
        }

        binding.name.text = name
        binding.callTime.text = duration
        binding.status.text = status


        binding.share.setOnClickListener(View.OnClickListener {
            sendMessageOnWhatsApp(number, getString(R.string.whatsapp_share_content))
        })

    }

    @SuppressLint("SuspiciousIndentation")
    private fun sendMessageOnWhatsApp(contactNumber: String, message: String) {
        type = Constant.SHARE
        val intent = Intent(Intent.ACTION_VIEW)
        //intent.data = Uri.parse("https://api.whatsapp.com/send?phone=+91$9629796215&text=$message")
     intent.data = Uri.parse("https://api.whatsapp.com/send?phone=+91$contactNumber&text=$message")
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        if (type.equals(Constant.SHARE)) {
            startActivity(
                Intent(
                    this@ShareActivity,
                    FollowUpActivity::class.java
                ).putExtra(Constant.LEAD_ID, leadId).putExtra(Constant.LEAD_STATUS, status)
            )
            finish()
        }
    }

}