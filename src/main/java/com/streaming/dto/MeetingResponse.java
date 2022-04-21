package com.streaming.dto;

import java.util.List;

import com.streaming.entities.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MeetingResponse {
	private String meetingLink;
	private String meetingTitle;
	private List<User> members;
	private long startDateTime;
	private long endDateTime;
}
