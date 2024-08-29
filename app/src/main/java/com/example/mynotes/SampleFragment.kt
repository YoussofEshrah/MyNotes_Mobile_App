package com.example.mynotes

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class SampleFragment : Fragment(R.layout.fragment_sample) {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_sample, container, false)
        val fabEdit = view.findViewById<FloatingActionButton>(R.id.fabSampleEditButton)
        val fabBack = view.findViewById<FloatingActionButton>(R.id.fabSampleBackButton)
        val tvSampleTitle = view.findViewById<TextView>(R.id.tvSampleTitle)
        val tvSampleContent = view.findViewById<TextView>(R.id.tvSampleContent)


        val noteId = arguments?.getString("noteId")
        val noteTitle = arguments?.getString("noteTitle")
        val noteContent = arguments?.getString("noteContent")
        val noteColor = arguments?.getString("noteColor")

        tvSampleTitle.text = noteTitle
        tvSampleContent.text = noteContent

        val userName = arguments?.getString("USER_NAME") ?: ""
        val userEmail = arguments?.getString("USER_EMAIL") ?: ""
        Log.d("msg1", "sample name: "+ userName)
        Log.d("msg2", "sample email: "+ userEmail)


        fabBack.setOnClickListener{
            val homeFragment = HomeFragment()

            val bundle = Bundle()
            bundle.putString("USER_NAME", userName)
            bundle.putString("USER_EMAIL", userEmail)
            homeFragment.arguments = bundle
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.flHomeFragment, homeFragment)
                .commit()
        }

        fabEdit.setOnClickListener{
            val updateFragment = UpdateFragment().apply {
                arguments = Bundle().apply {
                    putString("USER_NAME", userName)
                    putString("USER_EMAIL", userEmail)
                    putString("noteId", noteId)
                    putString("noteTitle", noteTitle)
                    putString("noteContent", noteContent)
                    putString("noteColor", noteColor)
                }
            }
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.flHomeFragment, updateFragment)
                .commit()
        }


        return view
    }


}