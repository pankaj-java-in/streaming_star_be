package com.streaming.controller;

import java.util.Objects;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.streaming.dto.JoinMeetingDTO;
import com.streaming.dto.ScheduleMeetingDTO;
import com.streaming.service.MeetingService;
import com.streaming.utils.Response;

@RestController
public class MeetingController {
	
	@Autowired MeetingService meetingService;
	
	@PostMapping("/api/v1/meeting")
	public ResponseEntity<Object> ScheduleMeeting(@Valid @RequestBody ScheduleMeetingDTO payload){
		Object response = meetingService.scheduleMeeting(payload);
		if (Objects.nonNull(response)) {
			return Response.generateResponse(HttpStatus.OK, response, "success", true);
		}
		return Response.generateResponse(HttpStatus.EXPECTATION_FAILED, null, "failed", false);
	}
	
//	@GetMapping("/api/v1/meeting")
//	public ResponseEntity<Object> getUserMeetings(@RequestParam long userId){
//		Object response = meetingService.getUserMeeting(userId);
//		if (Objects.nonNull(response)) {
//			return Response.generateResponse(HttpStatus.OK, response, "success", true);
//		}
//		return Response.generateResponse(HttpStatus.EXPECTATION_FAILED, null, "failed", false);
//	}
	
	@PostMapping("/api/v1/meeting/join")
	public ResponseEntity<Object> joinMeeting(@RequestBody JoinMeetingDTO payload){
		ResponseEntity<Object> response = meetingService.joinMeeting(payload);
		if (Objects.nonNull(response)) {
			return response;
		}
		return Response.generateResponse(HttpStatus.NOT_ACCEPTABLE, null, "Meeting Not Found.", false);
	}
}
