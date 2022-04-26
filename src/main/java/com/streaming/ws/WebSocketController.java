package com.streaming.ws;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WebSocketController {
	
	@Autowired SimpMessagingTemplate template;
	
	@MessageMapping("/event.{eventId}")
	public void chatRoom(@DestinationVariable String eventId, Message<Map<String, Object>> message) {
		System.out.println(eventId);
		Map<String, Object> payload = message.getPayload();
		String actionType = String.valueOf(payload.get("actionType"));
		switch (actionType) {
		case "SEND_MESSAGE":
			sendMessage(payload);
			break;
		default:
			break;
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
		System.out.println(response);
	}
}
