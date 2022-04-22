package com.streaming.ws.event;

import java.util.Objects;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;

public class ConnectDisconnectEvent implements ChannelInterceptor {
	
	@Override
	public Message<?> preSend(Message<?> message, MessageChannel channel) {
		StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
		if (Objects.nonNull(accessor) && Objects.nonNull(accessor.getCommand())) {
			StompCommand command = accessor.getCommand();
			if (command.equals(StompCommand.CONNECT)) {
				handleSessionConnected(accessor);
			} else if (command.equals(StompCommand.DISCONNECT)) {
				handleSessionDisconnect(accessor);
			}
		}
		return message;
	}

	private void handleSessionConnected(StompHeaderAccessor accessor) {
		// TODO Auto-generated method stub
		
	}

	private void handleSessionDisconnect(StompHeaderAccessor accessor) {
		// TODO Auto-generated method stub
		
	}
}
