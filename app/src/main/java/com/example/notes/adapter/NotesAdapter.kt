import com.example.notes.data.Note
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.notes.R


class NotesAdapter(
    private val notes: List<Note>,            // List of notes
    private val onDeleteClick: (Note) -> Unit // Callback for deleting a note
) : RecyclerView.Adapter<NotesAdapter.NoteViewHolder>() {

    // ViewHolder to hold item views
    class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val noteTitle: TextView = itemView.findViewById(R.id.noteTitle)
        val noteContent: TextView = itemView.findViewById(R.id.noteContent)
        val deleteButton: Button = itemView.findViewById(R.id.deleteNoteButton)
    }

    // Inflate item layout and create ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_note, parent, false)
        return NoteViewHolder(view)
    }

    // Bind data to ViewHolder
    // Bind data to ViewHolder
    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = notes[position]
        holder.noteTitle.text = note.title
        holder.noteContent.text = note.content

        // Handle delete button click
        holder.deleteButton.setOnClickListener {
            onDeleteClick(note) // Pass the Note object to the callback
        }
    }

    // Return the size of the notes list
    override fun getItemCount(): Int = notes.size
}
