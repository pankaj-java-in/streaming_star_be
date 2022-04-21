package com.streaming.utils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.streaming.dto.MeetingMember;

public class InMeetingMembers {
	
	private ConcurrentHashMap<String, Set<MeetingMember>> members = new ConcurrentHashMap<>();
	
	public void addMemberInMeeting(MeetingMember member, String meetingId) {
		if (members.containsKey(meetingId)) {
			Set<MeetingMember> listOfMember = members.get(meetingId);
			if(!listOfMember.stream().anyMatch(mem->mem.getUserId()==member.getUserId())) {	listOfMember.add(member);}
		}else {
			Set<MeetingMember> listOfMember = new HashSet<>();
			listOfMember.add(member);
			members.put(meetingId, listOfMember);
		}
	}
	
	public void removeMemberInMeeting(long userId, String meetingId) {
		if (members.containsKey(meetingId)) {
			Set<MeetingMember> listOfMember = members.get(meetingId);
			listOfMember.removeIf(member-> member.getUserId()==userId);
			members.replace(meetingId, listOfMember);
		}
	}
	
	public Set<MeetingMember> getMembersOfMeeting(String meetingId) {
		if (members.containsKey(meetingId)) {
			return members.get(meetingId);
		}
		return Collections.emptySet();
	}
	
}
