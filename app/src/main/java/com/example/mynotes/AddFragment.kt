package com.example.mynotes

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.sql.Timestamp


class AddFragment : Fragment(R.layout.fragment_add) {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add, container, false)

        val etAddTitle = view.findViewById<EditText>(R.id.etAddTitle)
        val etAddContent = view.findViewById<EditText>(R.id.etAddContent)
        val fabBackButton = view.findViewById<FloatingActionButton>(R.id.fabBackButton)
        val rgColorSelection = view.findViewById<RadioGroup>(R.id.rgColorSelection)
        val radioButton = view.findViewById<RadioButton>(rgColorSelection.checkedRadioButtonId)
        val fabAddSaveButton = view.findViewById<FloatingActionButton>(R.id.fabAddSaveButton)

        val userName = arguments?.getString("USER_NAME") ?: ""
        val userEmail = arguments?.getString("USER_EMAIL") ?: ""

        fabAddSaveButton.setOnClickListener{
            if(etAddTitle.text.toString()=="" || etAddContent.text.toString()==""){
                Toast.makeText(context, "Please fill out all fields", Toast.LENGTH_SHORT).show()
            }else{
                val savedTimestamp = System.currentTimeMillis()
                val timeStamp = Timestamp(savedTimestamp)
                Log.d("time", ""+timeStamp)
                lateinit var color : String
                if(rgColorSelection.checkedRadioButtonId==R.id.rbRed){
                    color = "red"
                } else if(rgColorSelection.checkedRadioButtonId==R.id.rbBlue){
                    color = "blue"
                } else if(rgColorSelection.checkedRadioButtonId==R.id.rbYellow){
                    color = "yellow"
                }else{
                    color = "green"
    //                Toast.makeText(context,"id: "+radioButton.id +", yellow id: "+R.id.rbYellow, Toast.LENGTH_LONG).show()
    //                Log.d("message", "id: "+radioButton.id +", other id:" + rgColorSelection.checkedRadioButtonId)
                }
                val title = etAddTitle.text.toString()
                val content = etAddContent.text.toString()

                val db = UserDatabase(context)
                db.addNoteForUser(title, content, userEmail, color,savedTimestamp)

                val homeFragment = HomeFragment()
                val bundle = Bundle()
                bundle.putString("USER_NAME", userName)
                bundle.putString("USER_EMAIL", userEmail)
                homeFragment.arguments = bundle

                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.flHomeFragment, homeFragment)
                    .commit()

            }

        }


        fabBackButton.setOnClickListener{
            val homeFragment = HomeFragment()
            val bundle = Bundle()
            bundle.putString("USER_NAME", userName)
            bundle.putString("USER_EMAIL", userEmail)
            homeFragment.arguments = bundle

            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.flHomeFragment, homeFragment)
                .commit()
        }






        return view
    }

}