package com.example.mynotes

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import java.util.Locale

class SearchFragment : Fragment(R.layout.fragment_search) {

    private lateinit var searchResultsAdapter: CustomNotesAdapter
    private lateinit var rvSearchRecyclerView: RecyclerView
    private lateinit var fabSearchBackButton: FloatingActionButton
    private lateinit var etSearchBar: EditText
    private lateinit var noteIds: ArrayList<String>
    private lateinit var noteTitles: ArrayList<String>
    private lateinit var noteContents: ArrayList<String>
    private lateinit var noteColors: ArrayList<String>
    private lateinit var noteTimestamps: ArrayList<Long>
    private lateinit var db: UserDatabase
    private lateinit var userEmail: String
    private lateinit var userName: String
    private lateinit var tvNoResults : TextView
    private lateinit var ivNoResults : ImageView
    private lateinit var filteredNoteIds : ArrayList<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)
        rvSearchRecyclerView = view.findViewById(R.id.rvSearchRecyclerView)
        etSearchBar = view.findViewById(R.id.etSearchBar)
        fabSearchBackButton = view.findViewById<FloatingActionButton>(R.id.fabSearchBackButton)
        tvNoResults = view.findViewById(R.id.tvNoResults)
        ivNoResults = view.findViewById(R.id.ivNoResults)


        noteIds = ArrayList()
        noteTitles = ArrayList()
        noteContents = ArrayList()
        noteColors = ArrayList()
        noteTimestamps = ArrayList()
        db = UserDatabase(requireContext())
        userEmail = arguments?.getString("USER_EMAIL") ?: ""
        userName = arguments?.getString("USER_NAME") ?: ""

        loadNotes()

        searchResultsAdapter = CustomNotesAdapter(
            requireActivity(),
            requireContext(),
            db,
            this,
            noteIds,
            noteTitles,
            noteContents,
            noteColors,
            noteTimestamps,
            "", // userName (not used in search fragment)
            userEmail
        )
        rvSearchRecyclerView.adapter = searchResultsAdapter
        rvSearchRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        filteredNoteIds = ArrayList<String>()
        etSearchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterNotes(s.toString())
                refreshNoResultsPicture()
            }

            override fun afterTextChanged(s: Editable?) {}
        })



        fabSearchBackButton.setOnClickListener{
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

    private fun loadNotes() {
        val cursor = db.getNotesForUser(userEmail)
        if (cursor != null) {
            while (cursor.moveToNext()) {
                noteIds.add(cursor.getString(0))
                noteTitles.add(cursor.getString(1))
                noteContents.add(cursor.getString(2))
                noteColors.add(cursor.getString(3))
                noteTimestamps.add(cursor.getLong(4))
            }
        }
    }


    private fun filterNotes(query: String) {
        filteredNoteIds = ArrayList<String>()
        val filteredNoteTitles = ArrayList<String>()
        val filteredNoteContents = ArrayList<String>()
        val filteredNoteColors = ArrayList<String>()
        val filteredNoteTimestamps = ArrayList<Long>()

        val queryWords = query.trim().split("\\s+".toRegex()).map { it.lowercase(Locale.ROOT) }

        for (i in noteTitles.indices) {
            val titleWords = noteTitles[i].lowercase(Locale.ROOT).split("\\s+".toRegex())
            val contentWords = noteContents[i].lowercase(Locale.ROOT).split("\\s+".toRegex())

            val titleMatches = titleWords.any { word -> queryWords.contains(word) }
            val contentMatches = contentWords.any { word -> queryWords.contains(word) }

            if (titleMatches || contentMatches) {
                filteredNoteIds.add(noteIds[i])
                filteredNoteTitles.add(noteTitles[i])
                filteredNoteContents.add(noteContents[i])
                filteredNoteColors.add(noteColors[i])
                filteredNoteTimestamps.add(noteTimestamps[i])
            }
        }

        searchResultsAdapter = CustomNotesAdapter(
            requireActivity(),
            requireContext(),
            db,
            this,
            filteredNoteIds,
            filteredNoteTitles,
            filteredNoteContents,
            filteredNoteColors,
            filteredNoteTimestamps,
            "",
            userEmail
        )
        rvSearchRecyclerView.adapter = searchResultsAdapter
    }

    public fun refreshNoResultsPicture(){
        if(filteredNoteIds.isEmpty()){
//            Log.d("visibility","(visible): "+ ivNoResults.visibility)
            tvNoResults.visibility = View.VISIBLE
            ivNoResults.visibility = View.VISIBLE
        }else{
//            Log.d("visibility","(invisible): "+ ivNoResults.visibility)
            tvNoResults.visibility = View.INVISIBLE
            ivNoResults.visibility = View.INVISIBLE
        }
    }

}
