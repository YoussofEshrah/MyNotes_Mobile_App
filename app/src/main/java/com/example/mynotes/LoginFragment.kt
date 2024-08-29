package com.example.mynotes

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment

class LoginFragment : Fragment(R.layout.fragment_login){
    lateinit var userDB : UserDatabase
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        lateinit var name :String
        val view = inflater.inflate(R.layout.fragment_login, container, false)
        val sharedPref = context?.getSharedPreferences("myPref", Context.MODE_PRIVATE)
        val editor = sharedPref?.edit()
        val etLoginEmail = view.findViewById<EditText>(R.id.etLoginEmail)
        val etLoginPassword = view.findViewById<EditText>(R.id.etLoginPassword)
        val cbLoginRememberMe = view.findViewById<CheckBox>(R.id.cbLoginRememberMe)
        val btnLogin = view.findViewById<Button>(R.id.btnLogin)
        val btnGoToRegister = view.findViewById<Button>(R.id.btnGoToRegister)
        userDB = UserDatabase(context)



        btnGoToRegister.setOnClickListener{
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.flAuthFragment, RegisterFragment())
                .addToBackStack(null)
                .commit()
        }



        btnLogin.setOnClickListener{
            val email = etLoginEmail.text.toString()
            val password = etLoginPassword.text.toString()
            if((email.isEmpty()) or (password.isEmpty())){
                Toast.makeText(requireContext(),"Please fill out all fields", Toast.LENGTH_SHORT).show()

            }else if (!userDB.checkUserExists(email)) {
                Toast.makeText(context, "User doesn't exist", Toast.LENGTH_SHORT).show()

            } else {
                val cursor = userDB.readUserData(email)
                val exists = cursor?.count ?: 0 > 0
                if(!exists){
                    Toast.makeText(requireContext(),"Incorrect Email or Password",Toast.LENGTH_SHORT).show()
                }else{
                    if(cursor!=null){
                        while (cursor.moveToNext()){
                            name = cursor.getString(1)
                        }
                    }
//                    Toast.makeText(context,"navigating to homescreen, "+ name,Toast.LENGTH_SHORT).show()
                    if(cbLoginRememberMe.isChecked){
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
        }
        return view
    }
}