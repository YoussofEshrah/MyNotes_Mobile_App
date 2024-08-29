package com.example.mynotes

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class HomeFragment : Fragment(R.layout.fragment_home) {

    lateinit var noteIds: ArrayList<String>
    lateinit var noteTitles: ArrayList<String>
    lateinit var noteContent: ArrayList<String>
    lateinit var noteColors: ArrayList<String>
    lateinit var noteTimestamps: ArrayList<Long>
    lateinit var db: UserDatabase
    lateinit var userName: String
    lateinit var userEmail: String
    lateinit var tvEmpty: TextView
    lateinit var ivEmpty: ImageView
    lateinit var customAdapter: CustomNotesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        noteIds = ArrayList()
        noteTitles = ArrayList()
        noteContent = ArrayList()
        noteColors = ArrayList()
        noteTimestamps = ArrayList()
        tvEmpty = view.findViewById(R.id.tvEmpty)
        ivEmpty = view.findViewById(R.id.ivEmpty)
        val tvHomeTitle = view.findViewById<TextView>(R.id.tvHomeTitle)
        val fabAddButton = view.findViewById<FloatingActionButton>(R.id.fabAddButton)
        val fabSearchButton = view.findViewById<FloatingActionButton>(R.id.fabSearchButton)
        val rvRecyclerView = view.findViewById<RecyclerView>(R.id.rvRecyclerView)
        val btnLogOut = view.findViewById<Button>(R.id.btnLogOut)
        db = UserDatabase(context)
        val addFragment = AddFragment()
        val searchFragment = SearchFragment()
        val sharedPref = context?.getSharedPreferences("myPref", Context.MODE_PRIVATE)
        val editor = sharedPref?.edit()

        userName = arguments?.getString("USER_NAME") ?: ""
        userEmail = arguments?.getString("USER_EMAIL") ?: ""
        val bundle = Bundle().apply {
            putString("USER_NAME", userName)
            putString("USER_EMAIL", userEmail)
        }
        addFragment.arguments = bundle
        searchFragment.arguments = bundle


        val cursor = db.readUserData(userEmail)
        if(cursor!=null){
            while (cursor.moveToNext()){
                userName = cursor.getString(1)
            }
        }


        tvHomeTitle.text = "$userName's Notes"


        storeDataInArrays()
        customAdapter = CustomNotesAdapter(requireActivity(), requireContext(),db, this, noteIds, noteTitles, noteContent, noteColors, noteTimestamps, userName, userEmail)
        rvRecyclerView.adapter = customAdapter
        rvRecyclerView.layoutManager = LinearLayoutManager(context)


        val itemTouchHelper = ItemTouchHelper(SwipeToDeleteCallback(customAdapter, db, requireContext(),this))
        itemTouchHelper.attachToRecyclerView(rvRecyclerView)

        fabAddButton.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.flHomeFragment, addFragment)
                .commit()
        }

        btnLogOut.setOnClickListener {
            val intent = Intent(context, AuthenticationActivity::class.java)
            editor?.apply {
                putBoolean("rememberMe", false)
                apply()
            }
            startActivity(intent)
        }




        fabSearchButton.setOnClickListener{
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.flHomeFragment, searchFragment)
                .commit()
        }

        return view
    }

    private fun storeDataInArrays() {
        val cursor = db.getNotesForUser(userEmail)
        if (cursor != null) {
            while (cursor.moveToNext()) {
                noteIds.add(cursor.getString(0))
                noteTitles.add(cursor.getString(1))
                noteContent.add(cursor.getString(2))
                noteColors.add(cursor.getString(3))
                noteTimestamps.add(cursor.getLong(4))
            }
        }
        refreshEmptyPicture()
    }

    public fun refreshEmptyPicture(){
        if(noteIds.isEmpty()){
            Log.d("visibility","(visible): "+ ivEmpty.visibility)
            tvEmpty.visibility = View.VISIBLE
            ivEmpty.visibility = View.VISIBLE
        }else{
            Log.d("visibility","(invisible): "+ ivEmpty.visibility)
            tvEmpty.visibility = View.INVISIBLE
            ivEmpty.visibility = View.INVISIBLE
        }
    }


    override fun onResume() {
        val tvHomeTitle = view?.findViewById<TextView>(R.id.tvHomeTitle)
        userEmail = arguments?.getString("USER_EMAIL") ?: ""


        super.onResume()
    }
}
