package com.bridgelabz.fundoo.notes.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bridgelabz.fundoo.elasticsearch.IElasticsearch;
import com.bridgelabz.fundoo.notes.dto.NotesDto;
import com.bridgelabz.fundoo.notes.model.Note;
import com.bridgelabz.fundoo.notes.service.INotesService;
import com.bridgelabz.fundoo.response.Response;

@RestController
@RequestMapping("/user/note")
@PropertySource("classpath:message.properties")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class NotesController {

	Logger logger = LoggerFactory.getLogger(NotesController.class);

	@Autowired
	private INotesService noteService;

	@Autowired
	IElasticsearch esService;

	@PostMapping("/create")
	public ResponseEntity<Response> createNote(HttpServletRequest request, @RequestHeader String token,
			@RequestBody NotesDto notesDto) {
		String userId = (String) request.getAttribute("userId");
		Response statusResponse = noteService.createNote(userId, notesDto);
		return new ResponseEntity<Response>(statusResponse, HttpStatus.OK);
	}

	@PutMapping("/update")
	public ResponseEntity<Response> updatingNote(HttpServletRequest request, @RequestHeader String token,
			@RequestBody NotesDto notesDto, @RequestParam String noteId) {
		String userId = (String) request.getAttribute("userId");
		Response statusResponse = noteService.updateNote(notesDto, userId, noteId);
		return new ResponseEntity<Response>(statusResponse, HttpStatus.OK);
	}

	@DeleteMapping("/delete")
	public ResponseEntity<Response> deleteNote(HttpServletRequest request, @RequestHeader String token,
			@RequestParam String noteId) {
		String userId = (String) request.getAttribute("userId");
		Response statusResponse = noteService.deleteNote(userId, noteId);
		return new ResponseEntity<Response>(statusResponse, HttpStatus.OK);
	}

	@GetMapping("/getallnotes")
	public List<Note> getAllNotes(HttpServletRequest request, @RequestHeader String token, @RequestParam boolean isPin,
			@RequestParam boolean isTrash, @RequestParam boolean isArchive) {
		String userId = (String) request.getAttribute("userId");
		List<Note> listNotes = noteService.getAllNote(userId, isPin, isTrash, isArchive);
		return listNotes;
	}

	@PutMapping("/pinandunpin")
	public ResponseEntity<Response> pinAndUnpin(HttpServletRequest request, @RequestHeader String token,
			@RequestParam String noteId) {
		String userId = (String) request.getAttribute("userId");
		Response statusResponse = noteService.pinAndUnPin(userId, noteId);
		return new ResponseEntity<Response>(statusResponse, HttpStatus.OK);
	}
	
	@PutMapping("/trashanduntrash")
	public ResponseEntity<Response> trashAndUntrash(HttpServletRequest request, @RequestHeader String token,
			@RequestParam String noteId) {
		String userId = (String) request.getAttribute("userId");
		Response statusResponse = noteService.trashAndUnTrash(userId, noteId);
		return new ResponseEntity<Response>(statusResponse, HttpStatus.OK);
	}
	
	@PutMapping("/archiveandunarchive")
	public ResponseEntity<Response> archiveAndUnarchive(HttpServletRequest request, @RequestHeader String token,
			@RequestParam String noteId) {
		String userId = (String) request.getAttribute("userId");
		Response statusResponse = noteService.archiveAndUnArchive(userId, noteId);
		return new ResponseEntity<Response>(statusResponse, HttpStatus.OK);
	}
	
	
	
	
	
 	
 
//	
//	@GetMapping("/getarchivenotes")
//	public List<Note>  getArchiveNotes(@RequestHeader String token) {
//		List<Note> listnotes = noteService.getArchiveNotes(token);
//		return listnotes;
//	}
//	
	
//	@GetMapping("/gettrashnotes")
//	public List<Note>  getTrashNotes(@RequestHeader String token) {
//		List<Note> listnotes = noteService.getTrashNotes(token);
//		return listnotes;
//	}
//	
//	@DeleteMapping("/deletepermanently")
//	public ResponseEntity<Response> deleteNote(@RequestHeader String token, @RequestParam String id){
//		Response responseStatus = noteService.deletePermanently(token, id);
//		return new ResponseEntity<Response> (responseStatus,HttpStatus.OK);
//	}
//	
//	@PutMapping("/color")
//	public ResponseEntity<Response> changeColor(@RequestHeader String token, @RequestParam String noteId,@RequestParam String colorCode) {
//		Response responseStatus = noteService.setColor(token, colorCode, noteId);
//		return new  ResponseEntity<Response> (responseStatus,HttpStatus.OK);
//	}
//	
//	@GetMapping("/getunpinnednotes")
//	public List<Note> getUnPinnedNotes(@RequestHeader String token){
//		List<Note> listnotes = noteService.getUnPinnedNotes(token);
//		return listnotes;
//	}
//	
//	@GetMapping("/getpinnednotes")
//	public List<Note> getPinnedNotes(@RequestHeader String token){
//		List<Note> listnotes = noteService.getPinnedNotes(token);
//		return listnotes;
//	}
//	@GetMapping("/searchTitle")
//	public List<Note> searchTitle(@RequestParam String title , @RequestParam String token) throws IOException {
//	return esService.searchByTitle(title,token);
//	}
//	
//	@PutMapping("/addreminder")
//	public ResponseEntity<Response> addingReminder(@RequestHeader String token , @RequestParam String noteId , @RequestParam String reminder) {
//		Response responseStatus = noteService.addReminder(token, noteId, reminder);
//		return new  ResponseEntity<Response> (responseStatus,HttpStatus.OK);
//	}
//	
//	@GetMapping("/getremainders")
//	public ResponseEntity<String> getRemainder(@RequestHeader String token , @RequestParam String noteId) {
//		String responseStatus = noteService.getRemainders(token, noteId);
//		return new  ResponseEntity<String> (responseStatus,HttpStatus.OK);
//	}
}
