package com.tt.skolarrs.view.activity

import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import com.tt.skolarrs.R
import com.tt.skolarrs.databinding.ActivitySettingsBinding
import com.tt.skolarrs.utilz.Constant
import com.tt.skolarrs.utilz.MyFunctions
import java.util.*

class SettingsActivity : AppCompatActivity() {

    lateinit var binding: ActivitySettingsBinding
    var simSlot: String? = "0"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        simSlot = MyFunctions.getSharedPreference(
            this@SettingsActivity,
            Constant.SIM_SLOT,
            Constant.SIM1
        )

        if (simSlot == Constant.SIM1) {
            binding.sim1.isChecked = true
        }
        if (simSlot ==    Constant.SIM2) {
            binding.sim2.isChecked = true
        }

        binding.simType.setOnCheckedChangeListener { group, checkedId ->
            val rb = group.findViewById<View>(checkedId) as RadioButton
            if (rb != null) {

                if (checkedId == R.id.sim1) {

                    MyFunctions.setSharedPreference(
                        this@SettingsActivity,
                        Constant.SIM_SLOT,Constant.SIM1
                    )

                }
                if (checkedId == R.id.sim2) {

                    MyFunctions.setSharedPreference(
                        this@SettingsActivity,
                        Constant.SIM_SLOT,
                        Constant.SIM2
                    )

                    //finish();
                }
            }
        }

    }
}