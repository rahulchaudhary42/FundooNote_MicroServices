package com.bridgelabz.fundoo.notes.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.bridgelabz.fundoo.elasticsearch.IElasticsearch;
import com.bridgelabz.fundoo.elasticsearch.NoteContainer;
import com.bridgelabz.fundoo.elasticsearch.NoteOperation;
import com.bridgelabz.fundoo.exception.NotesException;
import com.bridgelabz.fundoo.notes.dto.NotesDto;
import com.bridgelabz.fundoo.notes.model.Note;
import com.bridgelabz.fundoo.notes.repository.INotesRepository;
import com.bridgelabz.fundoo.response.Response;

import com.bridgelabz.fundoo.util.JWTToken;
import com.bridgelabz.fundoo.util.RabbitMqElasticSearch;
import com.bridgelabz.fundoo.util.StatusHelper;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service("notesService")
@PropertySource("classpath:message.properties")
public class NotesServiceImpl implements INotesService {

	Logger logger = LoggerFactory.getLogger(NotesServiceImpl.class);

//	@Autowired
//	private IUserRepository userRepository;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private INotesRepository notesRepository;

	@Autowired
	private JWTToken jWTToken;

	@Autowired
	private Environment environment;

	@SuppressWarnings("unused")
	@Autowired
	private ObjectMapper objectMapper;

	@Override
	public Response createNote(String token, NotesDto notesDto) {
		String userId = jWTToken.verifyToken(token);
		System.out.println(notesDto.getTitle() + "\t" + notesDto.getDescription());
		logger.info(notesDto.toString());
		if (notesDto.getTitle().isEmpty() && notesDto.getDescription().isEmpty()) {
			throw new NotesException("Title and description are empty", -5);
		}
		Note note = modelMapper.map(notesDto, Note.class);
		note.setUserId(userId);
		note.setCreated(LocalDateTime.now());
		note.setModified(LocalDateTime.now());
		notesRepository.save(note);

		Response response = StatusHelper.statusInfo(environment.getProperty("status.notes.createdSuccessfull"),
				Integer.parseInt(environment.getProperty("status.success.code")));
		return response;
	}

	@Override
	public Response updateNote(NotesDto notesDto, String token, String noteId) {
		String userId = jWTToken.verifyToken(token);
		if (notesDto.getTitle().isEmpty() && notesDto.getDescription().isEmpty()) {
			throw new NotesException("Title and description are empty", -5);
		}

		Note notes = notesRepository.findByIdAndUserId(noteId, userId);
		notes.setTitle(notesDto.getTitle());
		notes.setDescription(notesDto.getDescription());
		notes.setModified(LocalDateTime.now());
		notesRepository.save(notes);
		Response response = StatusHelper.statusInfo(environment.getProperty("status.notes.updated"),
				Integer.parseInt(environment.getProperty("status.success.code")));
		return response;
	}

	@Override
	public Response deleteNote(String token, String noteId) {
		String userId=jWTToken.verifyToken(token);
		Note notes = notesRepository.findByIdAndUserId(noteId, userId);
		if (notes == null) {
			throw new NotesException("Invalid input", -5);
		}
		if (notes.isTrash() == false) {
			notes.setTrash(true);
			notes.setModified(LocalDateTime.now());
			notesRepository.save(notes);
			Response response = StatusHelper.statusInfo(environment.getProperty("status.note.trashed"),
					Integer.parseInt(environment.getProperty("status.success.code")));
			return response;
		}
		Response response = StatusHelper.statusInfo(environment.getProperty("status.note.trashError"),
				Integer.parseInt(environment.getProperty("status.note.errorCode")));
		return response;
	}

	public List<Note> getAllNote(String token, boolean isPin, boolean isTrash, boolean isArchive) {
		String userId=jWTToken.verifyToken(token);
		List<Note> listNote = notesRepository.findByUserId(userId);
		List<Note> notes = new ArrayList<Note>();
		for (Note note : listNote) {
			if (note.isPin() == isPin && note.isTrash() == isTrash && note.isArchive() == isArchive) {
				notes.add(note);
			}
		}
		 
		return notes;
	}

	@Override
	public Response pinAndUnPin(String token, String noteId) {
		String userId=jWTToken.verifyToken(token);
		Note notes = notesRepository.findByIdAndUserId(noteId, userId);
		if (notes == null) {
			throw new NotesException("Invalid input", -5);
		}
		if (notes.isPin() == false) {
			notes.setPin(true);
			notesRepository.save(notes);
			Response response = StatusHelper.statusInfo(environment.getProperty("status.note.pinned"),
					Integer.parseInt(environment.getProperty("status.success.code")));
			return response;
		} else {
			notes.setPin(false);
			notesRepository.save(notes);
			Response response = StatusHelper.statusInfo(environment.getProperty("status.note.unpinned"),
					Integer.parseInt(environment.getProperty("status.success.code")));
			return response;
		}
	}

	@Override
	public Response trashAndUnTrash(String token, String noteId) {
		String userId=jWTToken.verifyToken(token);
		Note notes = notesRepository.findByIdAndUserId(noteId, userId);
		if (notes.isTrash() == false) {
			notes.setTrash(true);
			notesRepository.save(notes);
			Response response = StatusHelper.statusInfo(environment.getProperty("status.note.trashed"),
					Integer.parseInt(environment.getProperty("status.success.code")));
			return response;
		} else {
			notes.setTrash(false);
			notesRepository.save(notes);
			Response response = StatusHelper.statusInfo(environment.getProperty("status.note.untrashed"),
					Integer.parseInt(environment.getProperty("status.success.code")));
			return response;
		}
	}

	@Override
	public Response archiveAndUnArchive(String token, String noteId) {
		String userId=jWTToken.verifyToken(token);
		Note notes = notesRepository.findByIdAndUserId(noteId, userId);
		if (notes == null) {
			throw new NotesException("Invalid input", -5);
		}
		if (notes.isArchive() == false) {
			notes.setArchive(true);
			notesRepository.save(notes);
			Response response = StatusHelper.statusInfo(environment.getProperty("status.note.archieved"),
					Integer.parseInt(environment.getProperty("status.success.code")));
			return response;
		} else {
			notes.setArchive(false);
			notesRepository.save(notes);
			Response response = StatusHelper.statusInfo(environment.getProperty("status.note.unarchieved"),
					Integer.parseInt(environment.getProperty("status.success.code")));
			return response;
		}
	}

 
 
 
 

 

}
