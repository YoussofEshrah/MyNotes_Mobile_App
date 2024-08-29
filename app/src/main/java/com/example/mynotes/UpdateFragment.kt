package com.example.mynotes

import android.content.Intent
import android.content.LocusId
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.sql.Timestamp

class UpdateFragment : Fragment(R.layout.fragment_update) {
    lateinit var rgColor : RadioGroup
    lateinit var rbEditYellow : RadioButton
    lateinit var rbEditRed : RadioButton
    lateinit var rbEditBlue : RadioButton
    lateinit var rbEditGreen : RadioButton
    lateinit var etEditTitle : EditText
    lateinit var etEditContent : EditText
    lateinit var noteId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_update, container, false)

        val fabSave = view.findViewById<FloatingActionButton>(R.id.fabEditSaveButton)
        val fabBack = view.findViewById<FloatingActionButton>(R.id.fabEditBackButton)
        etEditTitle = view.findViewById<EditText>(R.id.etEditTitle)
        etEditContent = view.findViewById<EditText>(R.id.etEditContent)
        rgColor = view.findViewById<RadioGroup>(R.id.rgEditColorSelection)
        rbEditYellow= view.findViewById<RadioButton>(R.id.rbEditYellow)
        rbEditRed= view.findViewById<RadioButton>(R.id.rbEditRed)
        rbEditGreen= view.findViewById<RadioButton>(R.id.rbEditGreen)
        rbEditBlue= view.findViewById<RadioButton>(R.id.rbEditBlue)


//         Retrieve the arguments passed from the adapter
        noteId = arguments?.getString("noteId").toString()
        val noteTitle = arguments?.getString("noteTitle")
        val noteContent = arguments?.getString("noteContent")
        val noteColor = arguments?.getString("noteColor")
        etEditTitle.setText(noteTitle)
        etEditContent.setText(noteContent)

        fabSave.setOnClickListener {
            if(etEditTitle.text.toString()=="" || etEditContent.text.toString()==""){
                Toast.makeText(context, "Please fill out all fields", Toast.LENGTH_SHORT).show()
            }else {
                showCustomDialog()
            }
        }



        fabBack.setOnClickListener{
            val homeFragment = HomeFragment()

            val userName = arguments?.getString("USER_NAME") ?: ""
            val userEmail = arguments?.getString("USER_EMAIL") ?: ""
            Log.d("update recieved name, email", "name: "+userName+ " email: "+userEmail)
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

//    fun confirmUpdateDialog(){
//        val builder = AlertDialog.Builder(requireContext())
//        builder.setTitle("Save Changes")
//        builder.setMessage("Are you sure you want to save changes?")
//        builder.setPositiveButton("Yes") {_,_ ->
//
//
//        }
//
//
//        builder.setNegativeButton("No",{_,_->})
//        builder.create().show()
//    }
//
    private fun showCustomDialog() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_custom, null)
    val builder = AlertDialog.Builder(requireContext(), R.style.CustomDialog)
        .setView(dialogView)

        val alertDialog = builder.create()

        val btnPositive = dialogView.findViewById<Button>(R.id.btnPositive)
        val btnNegative = dialogView.findViewById<Button>(R.id.btnNegative)

        btnPositive.setOnClickListener {
            lateinit var color : String
            if(rgColor.checkedRadioButtonId==R.id.rbEditRed){
                color = "red"
            } else if(rgColor.checkedRadioButtonId==R.id.rbEditBlue){
                color = "blue"
            } else if(rgColor.checkedRadioButtonId==R.id.rbEditYellow){
                color = "yellow"
            }else{
                color = "green"
//                Toast.makeText(context,"id: "+radioButton.id +", yellow id: "+R.id.rbYellow, Toast.LENGTH_LONG).show()
                Log.d("message","yellow id: "+rbEditYellow.id+" selected id:" + rgColor.checkedRadioButtonId)
            }

            val updatedTitle = etEditTitle.text.toString()
            val updatedContent = etEditContent.text.toString()
            val savedTimestamp = System.currentTimeMillis()

            val db = UserDatabase(requireContext())
            db.updateNote(noteId, updatedTitle, updatedContent,color, savedTimestamp)
            parentFragmentManager.popBackStack()
            alertDialog.dismiss()
        }

        btnNegative.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }


}
