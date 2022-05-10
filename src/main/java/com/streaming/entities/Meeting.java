package com.streaming.entities;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(value = "stars_meeting")
public class Meeting {
	@Id
	private String id;
	@Indexed(unique = true)
	private String meetingId;
	private String meetingTitle;
	private String qrcode;
	private List<User> members;
	private LocalDateTime createDateTime;
	private LocalDateTime startDateTime;
	private LocalDateTime endDateTime;
	private boolean expired=false;
	private boolean deleted=false;
	private long creatorId;
}
