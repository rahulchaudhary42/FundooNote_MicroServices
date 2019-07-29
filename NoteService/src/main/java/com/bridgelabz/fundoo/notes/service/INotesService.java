package com.bridgelabz.fundoo.notes.service;

import java.util.List;

import org.springframework.stereotype.Service;

 
import com.bridgelabz.fundoo.notes.dto.NotesDto;
import com.bridgelabz.fundoo.notes.model.Note;
import com.bridgelabz.fundoo.response.Response;
@Service
public interface INotesService {

	// Method to create note
	Response createNote(String userId, NotesDto notesDto);

	// Method to delete note
	public Response deleteNote(String noteId , String userId);

	// Method to update note
	public Response updateNote(NotesDto notesDto, String userId, String noteId);
	
	 
	public List<Note> getAllNote(String userId, boolean isPin, boolean isTrash, boolean isArchive);
	
	// Method to archive and unarchive
	public Response archiveAndUnArchive(String userId, String noteId);
	
	// Method to pin and unpin
	public Response pinAndUnPin(String userId, String noteId);
	
	// Method to trash and untrash
	public Response trashAndUnTrash(String userId, String noteId);
	
	public List<Note> getArchiveNotes(String token);
	
	public List<Note> getTrashNotes(String token); 
	
	public Response deletePermanently(String token, String noteId);
	
	//public List<NotesDto> getPinnedNotes(String token);
	public List<Note> getUnPinnedNotes(String token);
	
	public List<Note> getPinnedNotes(String token);
	 
 

	
}
