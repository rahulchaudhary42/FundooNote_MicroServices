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
import org.springframework.web.client.RestTemplate;

import com.bridgelabz.fundoo.elasticsearch.IElasticsearch;
import com.bridgelabz.fundoo.notes.dto.NotesDto;
import com.bridgelabz.fundoo.notes.model.Note;
import com.bridgelabz.fundoo.notes.service.INotesService;
import com.bridgelabz.fundoo.response.Response;

import io.swagger.annotations.ApiOperation;

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

	RestTemplate template = new RestTemplate();

	@PostMapping("/create")
	public ResponseEntity<Response> creatingNote(HttpServletRequest request, @RequestBody NotesDto notesDto,
			@RequestHeader("token") String token) {
		Boolean isPresent = template.getForObject("http://localhost:8081/user/userPresent/" + token, Boolean.class);
		logger.info("rest call-->" + isPresent);
		Response responseStatus = noteService.createNote(token, notesDto);
		return new ResponseEntity<Response>(responseStatus, HttpStatus.OK);
	}

	@PutMapping("/update")
	@ApiOperation(value = "Edit Note groups", notes = "Edit Notes ", response = Note.class)
	public ResponseEntity<Response> updatingNote(HttpServletRequest request, @RequestHeader String token,
			@RequestBody NotesDto notesDto, @RequestParam String noteId) {
		Boolean isPresent = template.getForObject("http://localhost:8081/user/userPresent/" + token, Boolean.class);
		logger.info("rest call-->" + isPresent);
		Response statusResponse = noteService.updateNote(notesDto, token, noteId);
		return new ResponseEntity<Response>(statusResponse, HttpStatus.OK);
	}

	@DeleteMapping("/delete")
	public ResponseEntity<Response> deleteNote(HttpServletRequest request, @RequestHeader String token,
			@RequestParam String noteId) {
		Boolean isPresent = template.getForObject("http://localhost:8081/user/userPresent/" + token, Boolean.class);
		logger.info("rest call-->" + isPresent);
		Response statusResponse = noteService.deleteNote(token, noteId);
		return new ResponseEntity<Response>(statusResponse, HttpStatus.OK);
	}

	@GetMapping("/getallnotes")
	public List<Note> getAllNotes(HttpServletRequest request, @RequestHeader String token, @RequestParam boolean isPin,
			@RequestParam boolean isTrash, @RequestParam boolean isArchive) {
		Boolean isPresent = template.getForObject("http://localhost:8081/user/userPresent/" + token, Boolean.class);
		logger.info("rest call-->" + isPresent);
		List<Note> listNotes = noteService.getAllNote(token, isPin, isTrash, isArchive);
		return listNotes;
	}

	@PutMapping("/pinandunpin")
	public ResponseEntity<Response> pinAndUnpin(HttpServletRequest request, @RequestHeader String token,
			@RequestParam String noteId) {
		Boolean isPresent = template.getForObject("http://localhost:8081/user/userPresent/" + token, Boolean.class);
		logger.info("rest call-->" + isPresent);
		Response statusResponse = noteService.pinAndUnPin(token, noteId);
		return new ResponseEntity<Response>(statusResponse, HttpStatus.OK);
	}

	@PutMapping("/trashanduntrash")
	public ResponseEntity<Response> trashAndUntrash(HttpServletRequest request, @RequestHeader String token,
			@RequestParam String noteId) {
		Boolean isPresent = template.getForObject("http://localhost:8081/user/userPresent/" + token, Boolean.class);
		logger.info("rest call-->" + isPresent);
		Response statusResponse = noteService.trashAndUnTrash(token, noteId);
		return new ResponseEntity<Response>(statusResponse, HttpStatus.OK);
	}

	@PutMapping("/archiveandunarchive")
	public ResponseEntity<Response> archiveAndUnarchive(HttpServletRequest request, @RequestHeader String token,
			@RequestParam String noteId) {
		Boolean isPresent = template.getForObject("http://localhost:8081/user/userPresent/" + token, Boolean.class);
		logger.info("rest call-->" + isPresent);
		Response statusResponse = noteService.archiveAndUnArchive(token, noteId);
		return new ResponseEntity<Response>(statusResponse, HttpStatus.OK);
	}

}
