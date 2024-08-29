package com.example.mynotes

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast

class UserDatabase constructor(val context: Context?) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "userdatabase.db"
        private const val DATABASE_VERSION = 1

        // Users table
        private const val TABLE_USERS = "users"
        private const val COLUMN_USER_ID = "_id"
        private const val COLUMN_USER_NAME = "user_name"
        private const val COLUMN_USER_EMAIL = "user_email"
        private const val COLUMN_USER_PASSWORD = "user_password"

        // Notes table
        private const val TABLE_NOTES = "notes"
        private const val COLUMN_NOTE_ID = "_id"
        private const val COLUMN_NOTE_TITLE = "note_title"
        private const val COLUMN_NOTE_CONTENT = "note_content"
        private const val COLUMN_COLOR = "note_color"
        private const val COLUMN_TIMESTAMP = "note_timestamp"
        private const val COLUMN_USER_EMAIL_FK = "user_email_fk"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createUserTableQuery = """
            CREATE TABLE $TABLE_USERS (
            $COLUMN_USER_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $COLUMN_USER_NAME TEXT,
            $COLUMN_USER_EMAIL TEXT UNIQUE,
            $COLUMN_USER_PASSWORD TEXT);
        """.trimIndent()
        db.execSQL(createUserTableQuery)

        val createNotesTableQuery = """
            CREATE TABLE $TABLE_NOTES (
            $COLUMN_NOTE_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $COLUMN_NOTE_TITLE TEXT,
            $COLUMN_NOTE_CONTENT TEXT,
            $COLUMN_COLOR TEXT,
            $COLUMN_TIMESTAMP LONG,
            $COLUMN_USER_EMAIL_FK TEXT,
            FOREIGN KEY($COLUMN_USER_EMAIL_FK) REFERENCES $TABLE_USERS($COLUMN_USER_EMAIL) ON DELETE CASCADE);
        """.trimIndent()
        db.execSQL(createNotesTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NOTES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }

    fun addUser(name: String, email: String, password: String) {
        val db = this.writableDatabase
        val cv = ContentValues()

        cv.put(COLUMN_USER_NAME, name)
        cv.put(COLUMN_USER_EMAIL, email)
        cv.put(COLUMN_USER_PASSWORD, password)
        val result = db.insert(TABLE_USERS, null, cv)
        if (result == -1L) {
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Added Successfully!", Toast.LENGTH_SHORT).show()
        }
    }

    fun readUserData(email: String): Cursor? {
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_USERS WHERE $COLUMN_USER_EMAIL = ?"
        return db.rawQuery(query, arrayOf(email))
    }

    fun checkUserExists(email: String): Boolean {
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_USERS WHERE $COLUMN_USER_EMAIL = ?"
        val cursor: Cursor? = db.rawQuery(query, arrayOf(email))
        val exists = cursor?.count ?: 0 > 0
        cursor?.close()
        return exists
    }

    fun addNoteForUser(title: String, content: String, email: String, color: String, timestamp: Long) {
        val db = this.writableDatabase
        val cv = ContentValues()

        cv.put(COLUMN_NOTE_TITLE, title)
        cv.put(COLUMN_NOTE_CONTENT, content)
        cv.put(COLUMN_COLOR, color)
        cv.put(COLUMN_TIMESTAMP,timestamp)
        cv.put(COLUMN_USER_EMAIL_FK, email)
        val result = db.insert(TABLE_NOTES, null, cv)
        if (result == -1L) {
            Toast.makeText(context, "Failed to add note", Toast.LENGTH_SHORT).show()
        } else {
//            Toast.makeText(context, "Note added successfully!", Toast.LENGTH_SHORT).show()
        }
    }

    fun getNotesForUser(email: String): Cursor? {
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_NOTES WHERE $COLUMN_USER_EMAIL_FK = ?"
        return db.rawQuery(query, arrayOf(email))
    }

    fun updateNote(noteId: String?, title: String, content: String, color: String, timestamp: Long) {
        val db = this.writableDatabase
        val cv = ContentValues()
//        cv.put(COLUMN_NOTE_ID, noteId)
        cv.put(COLUMN_NOTE_TITLE, title)
        cv.put(COLUMN_NOTE_CONTENT, content)
        cv.put(COLUMN_COLOR, color)
        cv.put(COLUMN_TIMESTAMP,timestamp)
//        Log.d("db", "color to update with: "+color)
        db.update(TABLE_NOTES, cv, "$COLUMN_NOTE_ID = ?", arrayOf(noteId))
    }

    fun deleteNote(noteId :  String){
        val db = this.writableDatabase
        val result = db.delete(TABLE_NOTES,"_id=?", arrayOf(noteId))
        if (result == -1) {
            Toast.makeText(context, "Failed to Delete", Toast.LENGTH_SHORT).show()
        } else {
//            Toast.makeText(context, "Deleted Successfully!", Toast.LENGTH_SHORT).show()
        }
    }
}
