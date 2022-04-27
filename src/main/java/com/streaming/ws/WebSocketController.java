package com.streaming.ws;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

import com.streaming.entities.Meeting;
import com.streaming.entities.User;
import com.streaming.repo.MeetingRepo;

@RestController
public class WebSocketController {
	
	@Autowired SimpMessagingTemplate template;
	@Autowired MeetingRepo meetingRepo;
	
	@MessageMapping("/event.{eventId}")
	public void chatRoom(@DestinationVariable String eventId, Message<Map<String, Object>> message) {
		Map<String, Object> payload = message.getPayload();
		String actionType = String.valueOf(payload.get("actionType"));
		switch (actionType) {
		case "SEND_MESSAGE":
			sendMessage(payload);
			break;
		case "SEND_STREAM":
			sendStream(payload);
			break;
		case "CALL_DISCONNECTED":
			callDisconnect(payload);
			break;
		default:
			break;
		}
	}

	private void sendStream(Map<String, Object> payload) {
		String eventId = String.valueOf(payload.get("eventId"));
		String streamId = String.valueOf(payload.get("streamId"));
		String senderId = String.valueOf(payload.get("senderId"));
		Optional<Meeting> meetingStream = meetingRepo.findByMeetingIdAndDeleted(eventId, false);
		if (meetingStream.isPresent()) {
			Meeting meeting = meetingStream.get();
			User user = meeting.getMembers().stream().filter(mem-> mem.getUserId()!=Long.parseLong(senderId)).findFirst().get();
			Map<String, Object> response = new HashMap<>();
			response.put("streamId", streamId);
			response.put("eventId", eventId);
			response.put("senderId", senderId);
			response.put("actionType", "RECEIVED_STREAM");
			String destination = "/topic/event."+user.getUserId();
			template.convertAndSend(destination, response);
		}
	}

	private void callDisconnect(Map<String, Object> payload) {
		String eventId = String.valueOf(payload.get("eventId"));
		String senderId = String.valueOf(payload.get("senderId"));
		Optional<Meeting> meetingStream = meetingRepo.findByMeetingIdAndDeleted(eventId, false);
		if (meetingStream.isPresent()) {
			Meeting meeting = meetingStream.get();
			User user = meeting.getMembers().stream().filter(mem-> mem.getUserId()!=Long.parseLong(senderId)).findFirst().get();
			Map<String, Object> response = new HashMap<>();
			response.put("actionType", "CALL_DISCONNECTED");
			response.put("eventId", eventId);
			response.put("senderId", senderId);
			String destination = "/topic/event."+user.getUserId();
			template.convertAndSend(destination, response);
		}
	}

	private void sendMessage(Map<String, Object> payload) {
		String eventId = String.valueOf(payload.get("eventId"));
		String streamId = String.valueOf(payload.get("streamId"));
		String senderId = String.valueOf(payload.get("senderId"));
		Map<String, Object> response = new HashMap<>();
		response.put("streamId", streamId);
		response.put("eventId", eventId);
		response.put("senderId", senderId);
		response.put("actionType", "PLAY_STREAM");
		String destination = "/topic/event."+eventId;
		template.convertAndSend(destination, response);
	}
}
