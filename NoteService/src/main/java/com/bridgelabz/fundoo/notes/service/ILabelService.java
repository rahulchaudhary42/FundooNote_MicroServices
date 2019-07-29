package com.bridgelabz.fundoo.notes.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.bridgelabz.fundoo.notes.dto.LabelDto;
import com.bridgelabz.fundoo.notes.model.Label;
import com.bridgelabz.fundoo.response.Response;

@Service
public interface ILabelService {
	
	// Method to create label
	public Response createLabel(LabelDto labelDto , String userId);
//	
 	 // Method to delete label
 	public Response deleteLabel(String labelId, String userId);
//
//	// Method to update label
    public Response updateLabel(String labelId , String userId ,LabelDto labelDto);
    
    public List<Label> getLabel(String userId);
 	
//	public List<Label> getAllLabel(String token);
 	
 	public Response addLabelToNote(String labelId, String userId , String noteId);
//	
    public Response removeLabelFromNote(String labelId ,String userId , String noteId);
//  
//	public List<Label> getLebelsOfNote(String token, String noteId);
//	public Response addNoteToLabel(String labelId, String token, String noteId);
//	
//	public Response removeNoteFromLabel(String labelId, String token, String noteId);
//	
}
