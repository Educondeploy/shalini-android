package com.tt.skolarrs.view.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.RadioButton
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.tt.skolarrs.R
import com.tt.skolarrs.databinding.FragmentSettingsBinding
import com.tt.skolarrs.utilz.Constant
import com.tt.skolarrs.utilz.MyFunctions
import com.tt.skolarrs.view.activity.MainActivity


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SettingsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SettingsFragment : Fragment() {
    lateinit var binding: FragmentSettingsBinding
    var simSlot: String? = "0"
    var isRecording: String? = "1"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                startActivity(Intent(requireActivity(), MainActivity::class.java))
                requireActivity().finish()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val view = binding.root

        simSlot = MyFunctions.getSharedPreference(
            requireContext(),
            Constant.SIM_SLOT,
            Constant.SIM1
        )

        isRecording =
            MyFunctions.getSharedPreference(requireContext(), Constant.IS_RECORDING, "1")
                .toString()

        if (simSlot == Constant.SIM1) {
            binding.sim1.isChecked = true
        }
        if (simSlot ==    Constant.SIM2) {
            binding.sim2.isChecked = true
        }

        if(isRecording.equals("0")) {
            binding.switchcompat.isChecked = false
        }
        else {
            if(isRecording.equals("1")) {
                binding.switchcompat.isChecked = true
            }
        }

        (requireActivity() as MainActivity).title =  MyFunctions.changeToProperCase("Settings")

        binding.simType.setOnCheckedChangeListener { group, checkedId ->
            val rb = group.findViewById<View>(checkedId) as RadioButton
            if (rb != null) {

                if (checkedId == R.id.sim1) {

                    MyFunctions.setSharedPreference(
                        requireContext(),
                        Constant.SIM_SLOT, Constant.SIM1
                    )

                }
                if (checkedId == R.id.sim2) {

                    MyFunctions.setSharedPreference(
                        requireContext(),
                        Constant.SIM_SLOT,
                        Constant.SIM2
                    )

                    //finish();
                }
            }
        }

        binding.switchcompat.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->


                if (isChecked) {
                    MyFunctions.setSharedPreference(
                        requireContext(),
                        Constant.IS_RECORDING, "1"
                    )
                } else {
                    MyFunctions.setSharedPreference(
                        requireContext(),
                        Constant.IS_RECORDING, "0"
                    )
                }

        })

        return view;
    }



}