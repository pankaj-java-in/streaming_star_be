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
	private String userType;
	private boolean audioStatus;
	private boolean videoStatus;
	private boolean recordingStatus;
	
	public MeetingMember(long userId, String userType) {
		this.userId = userId;
		this.userType = userType;
	}
	
	
}
