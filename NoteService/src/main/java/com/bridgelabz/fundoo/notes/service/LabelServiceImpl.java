package com.bridgelabz.fundoo.notes.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.bridgelabz.fundoo.exception.LabelException;
import com.bridgelabz.fundoo.exception.NotesException;
//import com.bridgelabz.fundoo.exception.NotesException;
//import com.bridgelabz.fundoo.exception.TokenException;
import com.bridgelabz.fundoo.notes.dto.LabelDto;
//import com.bridgelabz.fundoo.notes.dto.NotesDto;
import com.bridgelabz.fundoo.notes.model.Label;
import com.bridgelabz.fundoo.notes.model.Note;
import com.bridgelabz.fundoo.notes.repository.INotesRepository;
import com.bridgelabz.fundoo.notes.repository.LabelRepository;
import com.bridgelabz.fundoo.response.Response;

import com.bridgelabz.fundoo.util.JWTToken;
import com.bridgelabz.fundoo.util.StatusHelper;

@Service("labelService")
@PropertySource("classpath:message.properties")
public class LabelServiceImpl implements ILabelService {

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private LabelRepository labelRepository;

	@Autowired
	private INotesRepository notesRepository;

	@Autowired
	JWTToken jWTToken;

	@Autowired
	private Environment environment;

	@Override
	public Response createLabel(LabelDto labelDto, String token) {

		String userId = jWTToken.verifyToken(token);

		if (labelDto.getLabelName().isEmpty()) {
			throw new LabelException("Label has no name", -6);
		}
		Optional<Label> labelAvailability = labelRepository.findByUserIdAndLabelName(userId, labelDto.getLabelName());
		if (labelAvailability.isPresent()) {
			throw new LabelException("Label already exist", -6);
		}

		Label label = modelMapper.map(labelDto, Label.class);

		label.setLabelName(labelDto.getLabelName());
		label.setUserId(userId);
		label.setCreatedDate(LocalDateTime.now());
		label.setModifiedDate(LocalDateTime.now());
		label = labelRepository.save(label);
		Response response = StatusHelper.statusInfo(environment.getProperty("status.label.created"),
				Integer.parseInt(environment.getProperty("status.success.code")));
		return response;
	}

	@Override
	public Response updateLabel(String labelId, String token, LabelDto labelDto) {
		String userId=jWTToken.verifyToken(token);
		Label label = labelRepository.findByLabelIdAndUserId(labelId, userId);
		if (label == null) {
			throw new LabelException("No label exist", -6);
		}
		if (labelDto.getLabelName().isEmpty()) {
			throw new LabelException("Label has no name", -6);
		}
		Optional<Label> labelAvailability = labelRepository.findByUserIdAndLabelName(userId, labelDto.getLabelName());
		if (labelAvailability.isPresent()) {
			throw new LabelException("Label already exist", -6);
		}
		label.setLabelName(labelDto.getLabelName());
		label.setModifiedDate(LocalDateTime.now());
		labelRepository.save(label);
		Response response = StatusHelper.statusInfo(environment.getProperty("status.label.updated"),
				Integer.parseInt(environment.getProperty("status.success.code")));
		return response;
	}

	@Override
	public Response deleteLabel(String token, String labelId) {
		String userId=jWTToken.verifyToken(token);
		Label label = labelRepository.findByLabelIdAndUserId(labelId, userId);
		if (label == null) {
			throw new LabelException("Invalid input", -6);
		}
		labelRepository.delete(label);
		Response response = StatusHelper.statusInfo(environment.getProperty("status.label.deleted"),
				Integer.parseInt(environment.getProperty("status.success.code")));
		return response;

	}

	@Override
	public List<Label> getLabel(String token) {
		String userId=jWTToken.verifyToken(token);
		List<Label> listLabels = labelRepository.findByUserId(userId);
		// logger.info("Getting label of user {}", listLabels);
		return listLabels;
	}

 
	@Override
	public Response addLabelToNote(String token, String labelId, String noteId) {
		String userId=jWTToken.verifyToken(token);
		Optional<Note> note = notesRepository.findById(noteId);
		Optional<Label> label = labelRepository.findById(labelId);

		if (note.isPresent()) {
			List<Label> labelList = note.get().getListLabel();
			if (labelList != null && !labelList.contains(label.get())) {
				labelList.add(label.get());
				note.get().setListLabel(labelList);
				notesRepository.save(note.get());
				System.out.println("Label == :" + label.get());
				System.out.println("if" + note.get());
			} else {
				List<Label> newLabelList = new ArrayList<Label>();
				newLabelList.add(label.get());
				note.get().setListLabel(newLabelList);
				notesRepository.save(note.get());
				System.out.println("Label == :" + label.get());
				System.out.println("LabelList == :" + newLabelList);
				System.out.println("else" + note.get());
			}
			Response response = StatusHelper.statusInfo(environment.getProperty("status.label.addedtonote"),
					Integer.parseInt(environment.getProperty("status.success.code")));
			return response;

		} else {
			Response response = StatusHelper.statusInfo(environment.getProperty("status.label.note.added"),
					Integer.parseInt(environment.getProperty("status.error.code")));
			return response;
		}
	}

	@Override
	public Response removeLabelFromNote(String token, String labelId, String noteId) {
		String userId=jWTToken.verifyToken(token);
		Optional<Note> note = notesRepository.findById(noteId);
		if (note.isPresent()) {
			List<Label> labelList = note.get().getListLabel();
			if (labelList != null) {
				Iterator<Label> it = labelList.iterator();
				while (it.hasNext()) {
					Label label = it.next();
					if (label.getLabelId().equals(labelId)) {
						labelList.remove(label);
						note.get().setListLabel(labelList);
						notesRepository.save(note.get());
						Response response = StatusHelper.statusInfo(
								environment.getProperty("status.label.removedfromnote"),
								Integer.parseInt(environment.getProperty("status.success.code")));
						return response;
					}
				}
			}

		}
		Response response = StatusHelper.statusInfo(environment.getProperty("status.label.note.added"),
				Integer.parseInt(environment.getProperty("status.error.code")));
		return response;

	}
 

}
