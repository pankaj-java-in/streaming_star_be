package com.streaming.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MeetingMember {
	
	private long userId;
	private String name;
	private String email;
	private String userType;
	private String streamId;
	private boolean audioStatus;
	private boolean videoStatus;
	private boolean recordingStatus;
	
	public MeetingMember(String email, long userId, String userType, String name, String streamId) {
		this.email=email;
		this.userId = userId;
		this.userType = userType;
		this.name=name;
		this.streamId=streamId;
	}
	
	
}
