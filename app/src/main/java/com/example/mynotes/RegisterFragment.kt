package com.example.mynotes

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment

class RegisterFragment:Fragment(R.layout.fragment_register){
    lateinit var userDB : UserDatabase
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        return super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_register, container, false)
        val sharedPref = context?.getSharedPreferences("myPref", Context.MODE_PRIVATE)
        val editor = sharedPref?.edit()
        val etRegisterName = view.findViewById<EditText>(R.id.etRegisterName)
        val etRegisterEmail = view.findViewById<EditText>(R.id.etRegisterEmail)
        val etRegisterPassword = view.findViewById<EditText>(R.id.etRegisterPassword)
        val cbRegisterRememberMe = view.findViewById<CheckBox>(R.id.cbRegisterRememberMe)
        val btnRegister = view.findViewById<Button>(R.id.btnRegister)
        val btnGoTOLogin = view.findViewById<Button>(R.id.btnGoToLogin)
        userDB = UserDatabase(context)


        //getting rememberMe value from shared pref
        val rememberMe = sharedPref?.getBoolean("rememberMe", false)
        if(rememberMe==true){
//            Toast.makeText(context, "remembered, navigating to homescreen", Toast.LENGTH_SHORT).show()
            val storedName = sharedPref.getString("name","name")
            val storedEmail = sharedPref.getString("email","email")
            val intent = Intent(context, HomeActivity::class.java)
            intent.putExtra("USER_NAME",storedName)
            intent.putExtra("USER_EMAIL", storedEmail)
            startActivity(intent)
        }else{
//                Toast.makeText(context, "can't remember you", Toast.LENGTH_SHORT).show()
            Log.d("rememberMe", "rememberMe Value "+sharedPref?.getBoolean("rememberMe", false))
        }


        btnGoTOLogin.setOnClickListener{
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.flAuthFragment, LoginFragment())
                .addToBackStack(null)
                .commit()
        }


        btnRegister.setOnClickListener{
            val name = etRegisterName.text.toString()
            val email = etRegisterEmail.text.toString()
            val password = etRegisterPassword.text.toString()
            if((name.isEmpty()) or (email.isEmpty()) or (password.isEmpty())){
                Toast.makeText(requireContext(),"Please fill out all fields", Toast.LENGTH_SHORT).show()

            }else if(!(password.length>7)){
                Toast.makeText(context, "Password must be at least 8 characters long",Toast.LENGTH_SHORT).show()

            }else if (userDB.checkUserExists(email)) {
                Toast.makeText(context, "User already exists", Toast.LENGTH_SHORT).show()

            } else {
                userDB.addUser(name, email, password)
                Toast.makeText(context, "navigating to homescreen", Toast.LENGTH_SHORT).show()
                if(cbRegisterRememberMe.isChecked){
                    editor?.apply{
                        putBoolean("rememberMe", true)
                        putString("name", name)
                        putString("email", email)
                        putString("password", password)
                        apply()
                    }
                }
                val intent = Intent(context, HomeActivity::class.java)
                intent.putExtra("USER_NAME",name)
                intent.putExtra("USER_EMAIL", email)
                startActivity(intent)
            }
        }


        return view
    }

}
