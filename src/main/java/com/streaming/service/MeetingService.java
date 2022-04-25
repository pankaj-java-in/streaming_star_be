package com.streaming.service;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.streaming.dto.JoinMeetingDTO;
import com.streaming.dto.ScheduleMeetingDTO;

@Service
public interface MeetingService {

	Object scheduleMeeting(@Valid ScheduleMeetingDTO payload);
	ResponseEntity<Object> joinMeeting(JoinMeetingDTO payload);
	
}
