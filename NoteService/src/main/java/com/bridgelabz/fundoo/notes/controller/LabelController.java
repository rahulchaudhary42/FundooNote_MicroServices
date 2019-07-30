package com.bridgelabz.fundoo.notes.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.web.client.RestTemplate;

import com.bridgelabz.fundoo.notes.dto.LabelDto;
import com.bridgelabz.fundoo.notes.model.Label;
import com.bridgelabz.fundoo.notes.service.ILabelService;
import com.bridgelabz.fundoo.response.Response;
 
@RestController
@RequestMapping("/user/label")
@CrossOrigin(allowedHeaders = "*" ,origins = "*",exposedHeaders= {"jwtToken"} )
public class LabelController {
 
	Logger logger = LoggerFactory.getLogger(LabelController.class); 
	
	@Autowired
	private ILabelService labelService;
 
	RestTemplate template = new RestTemplate();
 
	@PostMapping("/create")
	public ResponseEntity<Response> createLabel(HttpServletRequest request, @RequestHeader String token,
			@RequestBody LabelDto labelDto) {
		Boolean isPresent = template.getForObject("http://localhost:8081/user/userPresent/" + token, Boolean.class);
		logger.info("rest call-->" + isPresent);
		Response statusResponse = labelService.createLabel(labelDto, token);
		return new ResponseEntity<Response>(statusResponse, HttpStatus.OK);
	}
	
	@PutMapping("/update")
	public ResponseEntity<Response> updateLabel(HttpServletRequest request, @RequestHeader String token,
			@RequestParam String labelId, @RequestBody LabelDto labelDto) {
		Boolean isPresent = template.getForObject("http://localhost:8081/user/userPresent/" + token, Boolean.class);
		logger.info("rest call-->" + isPresent);
		Response statusResponse = labelService.updateLabel(labelId,token, labelDto);
		return new ResponseEntity<Response>(statusResponse, HttpStatus.OK);
	}
	
	@DeleteMapping("/delete")
	public ResponseEntity<Response> deleteLabel(HttpServletRequest request, @RequestHeader String token,
			@RequestParam String labelId) {
		Boolean isPresent = template.getForObject("http://localhost:8081/user/userPresent/" + token, Boolean.class);
		logger.info("rest call-->" + isPresent);
		Response statusResponse = labelService.deleteLabel(token, labelId);
		return new ResponseEntity<Response>(statusResponse, HttpStatus.OK);
	}
	
	@GetMapping("/getAlllabels")
	public List<Label> getLabel(HttpServletRequest request, @RequestHeader String token) {
		Boolean isPresent = template.getForObject("http://localhost:8081/user/userPresent/" + token, Boolean.class);
		logger.info("rest call-->" + isPresent);
		List<Label> listLabels = labelService.getLabel(token);
		return listLabels;
	}
	
	@PutMapping("/addlabeltonote")
	public ResponseEntity<Response> addLabelToNote(HttpServletRequest request, @RequestHeader String token,
			@RequestParam String noteId, @RequestParam String labelId) {
		Boolean isPresent = template.getForObject("http://localhost:8081/user/userPresent/" + token, Boolean.class);
		logger.info("rest call-->" + isPresent);
		Response statusResponse = labelService.addLabelToNote(token, labelId, noteId);
		return new ResponseEntity<Response>(statusResponse, HttpStatus.OK);
	}
 
	@PutMapping("/removelabelfronnote")
	public ResponseEntity<Response> removeLabelFromNote(HttpServletRequest request, @RequestHeader String token,
			@RequestParam String noteId, @RequestParam String labelId) {
		Boolean isPresent = template.getForObject("http://localhost:8081/user/userPresent/" + token, Boolean.class);
		logger.info("rest call-->" + isPresent);
		Response statusResponse = labelService.removeLabelFromNote(token, labelId, noteId);
		return new ResponseEntity<Response>(statusResponse, HttpStatus.OK);
	}
}
