package com.streaming.dto;

import java.time.LocalDateTime;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleMeetingDTO {
	@NotNull
	private long creatorId;
	@NotNull
	private long artistId;
	@NotBlank @NotEmpty
	private String meetingTitle;
	@NotNull
	private LocalDateTime startDateTime;
	@NotNull
	private LocalDateTime endDateTime;
	
}
