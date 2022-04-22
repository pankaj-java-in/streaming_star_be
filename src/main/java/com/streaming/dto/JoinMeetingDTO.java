package com.streaming.dto;

import lombok.Data;

@Data
public class JoinMeetingDTO {
	private String email;
	private String meetingId;
	private String streamId;
}
