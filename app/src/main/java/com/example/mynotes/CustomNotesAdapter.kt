package com.example.mynotes

import android.content.Context
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Email
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale

class CustomNotesAdapter(
    private val activity: FragmentActivity,
    private val context: Context,
    private val db: UserDatabase,
    private val fragment: Fragment,
    val noteIds: ArrayList<String>,
    val noteTitles: ArrayList<String>,
    val noteContents: ArrayList<String>,
    val noteColors: ArrayList<String>,
    val noteTimestamps : ArrayList<Long>,
    private val userName: String,
    private val userEmail: String,
) : RecyclerView.Adapter<CustomNotesAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.row_note, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.tvNoteId.text = noteIds[position]
        holder.tvNoteTitle.text = noteTitles[position]
        holder.tvNoteContent.text = noteContents[position]
//        val timeStamp = noteTimestamps[position]
//        Log.d("msg","Timestamps: "+noteTimestamps)
//        Log.d("noteColors", "colors: "+noteColors)
        holder.tvDate.text = SimpleDateFormat("dd-MM-yyyy, HH:mm", Locale.ENGLISH).format(noteTimestamps[position])



        val color = when (noteColors[position]) {
            "red" -> R.color.noteRed
            "blue" -> R.color.noteBlue
            "green" -> R.color.noteGreen
            "yellow" -> R.color.noteYellow
            else -> android.R.color.white
        }
//        Log.d("back color", "custom Adapter set color as: "+color)
        holder.llMainLayout.setBackgroundResource(color)


        holder.ivDelete.setOnClickListener{
            showCustomDialog(holder)
        }

        holder.llMainLayout.setOnClickListener {
            val sampleFragment = SampleFragment().apply {
                arguments = Bundle().apply {
                    putString("noteId", noteIds[position])
                    putString("noteTitle", noteTitles[position])
                    putString("noteContent", noteContents[position])
                    putString("noteColor", noteColors[position])
                    putLong("noteTimestamp", noteTimestamps[position])
                    putString("USER_NAME", userName)
                    putString("USER_EMAIL", userEmail)
                    Log.d("adapter", "email: "+userEmail)
                }
            }
            activity.supportFragmentManager.beginTransaction()
                .addToBackStack(null)
                .replace(R.id.flHomeFragment, sampleFragment)
                .commit()
        }
    }


    override fun getItemCount(): Int {
        return noteIds.size
    }


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNoteId: TextView = itemView.findViewById(R.id.tvNoteId)
        val tvNoteTitle: TextView = itemView.findViewById(R.id.tvNoteTitle)
        val tvNoteContent: TextView = itemView.findViewById(R.id.tvNoteContent)
        val ivDelete : ImageView = itemView.findViewById(R.id.ivDelete)
        val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        val llMainLayout: LinearLayout = itemView.findViewById(R.id.llMainLayout)
    }

    private fun showCustomDialog(holder : MyViewHolder) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_custom, null)
        val builder = AlertDialog.Builder(context, R.style.CustomDialog)
            .setView(dialogView)

        val tvDialogMessage = dialogView.findViewById<TextView>(R.id.tvDialogMessage)
        val alertDialog = builder.create()

        val btnPositive = dialogView.findViewById<Button>(R.id.btnPositive)
        val btnNegative = dialogView.findViewById<Button>(R.id.btnNegative)

        tvDialogMessage.text = "Are you sure wish to delete this note?"

        btnPositive.text = "Confirm"
        btnNegative.text = "Cancel"

        btnPositive.setOnClickListener {

            val position = holder.adapterPosition
            val noteId = noteIds[position]
            db.deleteNote(noteId)
            //remove frm adapter
            noteIds.removeAt(position)
            noteTitles.removeAt(position)
            noteContents.removeAt(position)
            noteColors.removeAt(position)
            notifyItemRemoved(position)
            if(fragment is HomeFragment){
                fragment.refreshEmptyPicture()
            }else if(fragment is SearchFragment){
                fragment.refreshNoResultsPicture()
            }

            alertDialog.dismiss()
        }

        btnNegative.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }
}
