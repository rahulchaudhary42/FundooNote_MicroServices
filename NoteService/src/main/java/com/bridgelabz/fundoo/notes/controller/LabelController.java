package com.bridgelabz.fundoo.notes.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.bridgelabz.fundoo.notes.dto.LabelDto;
import com.bridgelabz.fundoo.notes.model.Label;
import com.bridgelabz.fundoo.notes.service.ILabelService;
import com.bridgelabz.fundoo.response.Response;
 
@RestController
@RequestMapping("/user/label")
@CrossOrigin(allowedHeaders = "*" ,origins = "*",exposedHeaders= {"jwtToken"} )
public class LabelController {
 
	@Autowired
	private ILabelService labelService;
 
 
 
	@PostMapping("/create")
	public ResponseEntity<Response> createLabel(HttpServletRequest request, @RequestHeader String token,
			@RequestBody LabelDto labelDto) {
		String userId =(String)request.getAttribute("userId");
		Response statusResponse = labelService.createLabel(labelDto, userId);
		return new ResponseEntity<Response>(statusResponse, HttpStatus.OK);
	}
	
	@PutMapping("/update")
	public ResponseEntity<Response> updateLabel(HttpServletRequest request, @RequestHeader String token,
			@RequestParam String labelId, @RequestBody LabelDto labelDto) {
		String userId =(String)request.getAttribute("userId");
		System.out.println(labelId);
		Response statusResponse = labelService.updateLabel(labelId,userId, labelDto);
		return new ResponseEntity<Response>(statusResponse, HttpStatus.OK);
	}
	
	@DeleteMapping("/delete")
	public ResponseEntity<Response> deleteLabel(HttpServletRequest request, @RequestHeader String token,
			@RequestParam String labelId) {
		String userId =(String)request.getAttribute("userId");
		Response statusResponse = labelService.deleteLabel(userId, labelId);
		return new ResponseEntity<Response>(statusResponse, HttpStatus.OK);
	}
	
	@GetMapping("/getAlllabels")
	public List<Label> getLabel(HttpServletRequest request, @RequestHeader String token) {
		String userId =(String)request.getAttribute("userId");
		List<Label> listLabels = labelService.getLabel(userId);
		return listLabels;
	}
	
	@PutMapping("/addlabeltonote")
	public ResponseEntity<Response> addLabelToNote(HttpServletRequest request, @RequestHeader String token,
			@RequestParam String noteId, @RequestParam String labelId) {
		String userId =(String)request.getAttribute("userId");
		Response statusResponse = labelService.addLabelToNote(userId, labelId, noteId);
		return new ResponseEntity<Response>(statusResponse, HttpStatus.OK);
	}
 
	@PutMapping("/removelabelfronnote")
	public ResponseEntity<Response> removeLabelFromNote(HttpServletRequest request, @RequestHeader String token,
			@RequestParam String noteId, @RequestParam String labelId) {
		String userId =(String)request.getAttribute("userId");
		Response statusResponse = labelService.removeLabelFromNote(userId, labelId, noteId);
		return new ResponseEntity<Response>(statusResponse, HttpStatus.OK);
	}
}
