package com.example.greatingcard.screen.main.order.checkout

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import com.example.greatingcard.MainActivity
import com.example.greatingcard.R
import com.example.greatingcard.screen.main.home.HomeFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class SuccessPaymentFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_success_payment, container, false)

        val reStart = view.findViewById<AppCompatButton>(R.id.returnHome)
        reStart.setOnClickListener {
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
        }

        return view
    }

}