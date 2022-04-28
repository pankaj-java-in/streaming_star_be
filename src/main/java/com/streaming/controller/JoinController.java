package com.streaming.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class JoinController {
	
	@GetMapping("/join")
	public String join() {
		return "conference-enter";
	}
	
	@GetMapping("/room")
	public String room() {
		return "conference-room";
	}
	
	@GetMapping("/left-room")
	public String left() {
		return "left-room";
	}
}
