package com.example.notes

import NotesAdapter
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notes.data.Note
import com.google.firebase.database.*
import com.example.notes.databinding.ActivityMainBinding
import com.example.notes.databinding.DialogAddNoteBinding

class MainActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private val notesList = mutableListOf<Note>()
    private lateinit var adapter: NotesAdapter
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Always show welcome screen first
        setContentView(R.layout.activity_welcome)
        findViewById<Button>(R.id.nextButton).setOnClickListener {
            // Navigate to main notes screen
            setupNotesScreen()
        }
    }

    private fun setupNotesScreen() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance().getReference("notes")

        adapter = NotesAdapter(notesList) { note: Note -> deleteNoteFromFirebase(note) }
        binding.notesRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.notesRecyclerView.adapter = adapter

        fetchNotesFromFirebase()

        binding.addNoteButton.setOnClickListener { showAddNoteDialog() }
    }

    private fun showAddNoteDialog() {
        val dialogBinding = DialogAddNoteBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(this)
            .setTitle("Add Note")
            .setView(dialogBinding.root)
            .setPositiveButton("Save") { _, _ ->
                val title = dialogBinding.noteTitle.text.toString()
                val content = dialogBinding.noteContent.text.toString()
                saveNoteToFirebase(Note(title = title, content = content))
            }
            .setNegativeButton("Cancel", null)
            .create()
        dialog.show()
    }

    private fun saveNoteToFirebase(note: Note) {
        val noteId = database.push().key ?: return
        note.id = noteId
        database.child(noteId).setValue(note)
            .addOnSuccessListener { Toast.makeText(this, "Note added", Toast.LENGTH_SHORT).show() }
            .addOnFailureListener { Toast.makeText(this, "Error adding note", Toast.LENGTH_SHORT).show() }
    }

    private fun fetchNotesFromFirebase() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                notesList.clear()
                snapshot.children.mapNotNullTo(notesList) { it.getValue(Note::class.java) }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Error fetching notes", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun deleteNoteFromFirebase(note: Note) {
        database.child(note.id).removeValue()
            .addOnSuccessListener { Toast.makeText(this, "Note deleted", Toast.LENGTH_SHORT).show() }
            .addOnFailureListener { Toast.makeText(this, "Error deleting note", Toast.LENGTH_SHORT).show() }
    }
}