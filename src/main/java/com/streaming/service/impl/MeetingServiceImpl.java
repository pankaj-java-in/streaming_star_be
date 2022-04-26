package com.streaming.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.streaming.dto.JoinMeetingDTO;
import com.streaming.dto.MeetingMember;
import com.streaming.dto.MeetingResponse;
import com.streaming.dto.ScheduleMeetingDTO;
import com.streaming.entities.Meeting;
import com.streaming.entities.User;
import com.streaming.exception.InvalidDataTimeRangeException;
import com.streaming.exception.UserNotFoundException;
import com.streaming.repo.MeetingRepo;
import com.streaming.service.EmailService;
import com.streaming.service.MeetingService;
import com.streaming.utils.InMeetingMembers;
import com.streaming.utils.QRCodeGenerator;
import com.streaming.utils.Response;

@Service
public class MeetingServiceImpl implements MeetingService {
	
	@Autowired MeetingRepo meetingRepo;
	@Autowired ObjectMapper mapper;
	@Autowired InMeetingMembers inMeetingMembers;
	@Autowired EmailService emailService;
	@Autowired AmazonS3 amazonS3;
	
	private static final Logger log = LoggerFactory.getLogger(MeetingServiceImpl.class);
	//private String meetingUrlPrefix="http://www.thestreamingstars.com/outside/join-event?meet_id=";
	private String meetingUrlPrefix ="http://localhost/thestreamingstar/outside/join-event?meet_id=";
	
	@Value("${cloud.aws.bucket.name}")
	private String bucketName;
	private String endpointUrl = "https://cs-image.s3.ap-south-1.amazonaws.com/streaming/";
	
	@Override
	public Object scheduleMeeting(@Valid ScheduleMeetingDTO payload) {
		verifyMeetingUseCases(payload);
		Meeting meeting = mapper.convertValue(payload, Meeting.class);
		meeting.setMeetingId(getMeetingId());
		meeting.setMeetingTitle(payload.getMeetingTitle());
		meeting.setCreateDateTime(LocalDateTime.now());
		meeting.setStartDateTime(payload.getStartDateTime());
		meeting.setEndDateTime(payload.getEndDateTime());
		meeting.setPasscode(getMeetingPasscode());
		meeting.setCreatorId(meeting.getCreatorId());
		meeting.setMembers(getMemberDetails(payload.getCreatorId(), payload.getArtistId()));
		Meeting savedMeeting = meetingRepo.save(meeting);
		new Thread(()-> sendEmailInvitation(savedMeeting)).start();
		return new MeetingResponse(meetingUrlPrefix+savedMeeting.getMeetingId(), savedMeeting.getMeetingTitle(), savedMeeting.getMembers(), 
				Timestamp.valueOf(savedMeeting.getStartDateTime()).getTime(), Timestamp.valueOf(savedMeeting.getEndDateTime()).getTime());
	}

	private void verifyMeetingUseCases(@Valid ScheduleMeetingDTO payload) {
		if (payload.getStartDateTime().isAfter(payload.getEndDateTime())) {
			throw new InvalidDataTimeRangeException("Must be end date time after start date time.");
		}else if(payload.getCreatorId()!=1234) {
			throw new UserNotFoundException("User not found with id - " + payload.getCreatorId());
		}else if(payload.getArtistId()!=12345 ) {
			throw new UserNotFoundException("User not found with id - " + payload.getArtistId());
		}else if(payload.getCreatorId()==payload.getArtistId()) {
			throw new IllegalArgumentException("Must be different creatorId and ArtistId.");
		}else if(payload.getStartDateTime().isBefore(LocalDateTime.now()) || payload.getEndDateTime().isBefore(LocalDateTime.now())) {
			throw new InvalidDataTimeRangeException("Meeting start or end date time must be after current date time.");
		}
	}

	private String getMeetingId() {
		String block1 = UUID.randomUUID().toString().substring(0, 4).replaceAll("[-+.^:,]","");
		String block2 = UUID.randomUUID().toString().substring(0, 5).replaceAll("[-+.^:,]","");
		String block3 = UUID.randomUUID().toString().substring(0, 4).replaceAll("[-+.^:,]","");
		return block1+"-"+block2+"-"+block3;
	}
	
	private String getMeetingPasscode() {
		Random r = new Random(System.currentTimeMillis());
	    return String.valueOf(1000000000 + r.nextInt(2000000000)*2);
	}
	
	private void sendEmailInvitation(Meeting meeting) {
		try {
			List<User> members = meeting.getMembers();
			String guestEmail = members.stream().filter(user->user.getUserType().equals("guest")).map(user->user.getEmail()).findFirst().get();
			User star = members.stream().filter(user->user.getUserType().equals("star")).findFirst().get();
			Map<String, Object> emailBody = new HashMap<>();
			emailBody.put("meetingLink", meetingUrlPrefix+meeting.getMeetingId());
			emailBody.put("meetingDateTime", getMeetingDateTime(meeting.getStartDateTime(), meeting.getEndDateTime()));
			emailBody.put("meetingTitle", meeting.getMeetingTitle());
			emailBody.put("qrCodeUrl", endpointUrl+meeting.getMeetingId()+".png");
			emailBody.put("guestEmail", guestEmail);
			emailBody.put("starEmail", star.getName());
			String emailSubject = "Invitation: "+meeting.getMeetingTitle()+" @ " +getMeetingDateTime(meeting.getStartDateTime(), meeting.getEndDateTime())+" (IST)";
			members.stream().forEach(user->{
				if (user.getUserType().equals("guest")) {
					emailBody.put("The Event has been scheduled for star" , emailBody);
					emailService.sendHtmlMail(guestEmail, emailSubject, emailBody, "template.html");
				}else if(user.getUserType().equals("star")) {
					emailBody.put("The Event has been scheduled for guest", emailBody);
					emailService.sendHtmlMail(star.getEmail(), emailSubject, emailBody, "template.html");
				}
			});		
			generateQRAndUpload(meeting.getMeetingId()+".png", meetingUrlPrefix+meeting.getMeetingId());
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}

	private String getMeetingDateTime(LocalDateTime startDateTime, LocalDateTime endDateTime) {
		ZonedDateTime fromDate = ZonedDateTime.of(startDateTime, ZoneId.of("UTC"));
		ZonedDateTime toDate = ZonedDateTime.of(endDateTime, ZoneId.of("UTC"));
		String fromDateInIST = fromDate.format(
				DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).withZone(ZoneId.of("Asia/Kolkata")));
		String toDateInIST = toDate.format(
				DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).withZone(ZoneId.of("Asia/Kolkata")));
		String fromTimeInIST = fromDate.format(
				DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).withZone(ZoneId.of("Asia/Kolkata")));
		String toTimeInIST = toDate.format(
				DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).withZone(ZoneId.of("Asia/Kolkata")));
		return (fromTimeInIST.contains("pm") || fromTimeInIST.contains("am") && toTimeInIST.contains("am"))
				? fromDateInIST + " at " + fromTimeInIST + " to " + toDateInIST + " - " + toTimeInIST
				: fromDateInIST + " at " + fromTimeInIST + " - " + toTimeInIST;
	}
	
	@Override
	public ResponseEntity<Object> joinMeeting(JoinMeetingDTO payload) {
		Optional<Meeting> meetingStream = meetingRepo.findByMeetingIdAndDeleted(payload.getMeetingId(), false);
		if (meetingStream.isPresent()) {
			Map<String, Object> message = new HashMap<>();
			Meeting meeting = meetingStream.get();
			if(meeting.getStartDateTime().isAfter(LocalDateTime.now())){
				message.put("string", "Event will be start on the scheduled time.");
				message.put("timestamp", Timestamp.valueOf(meeting.getStartDateTime()).getTime());
				return Response.generateResponse(HttpStatus.OK, null, message , false);
			}else if(meeting.getEndDateTime().isBefore(LocalDateTime.now())) {
				message.put("string", "Meeting has expired.");
				return Response.generateResponse(HttpStatus.OK, null, message, false);
			}else {
				Optional<User> userStream = meeting.getMembers().stream().filter(mem->mem.getEmail().equals(payload.getEmail())).findFirst();
				if (userStream.isPresent()) {
					User currentUser = userStream.get();
					inMeetingMembers.addMemberInMeeting(new MeetingMember(currentUser.getEmail(), currentUser.getUserId(),currentUser.getUserType(), 
							currentUser.getName(), payload.getStreamId()), payload.getMeetingId() );
					Set<MeetingMember> membersOfMeeting = inMeetingMembers.getMembersOfMeeting(payload.getMeetingId());
					message.put("string", "You have joined");
					return Response.generateResponse(HttpStatus.OK, membersOfMeeting, message, true);
				}else {
					message.put("string", "You are not member of this event.");
					return Response.generateResponse(HttpStatus.NOT_ACCEPTABLE, null, message, false);
				}
			}
		}
		return null;
	}
	
	private String generateQRAndUpload(String qrName, String url) {
		File file = new File(qrName);
		try (FileOutputStream iofs = new FileOutputStream(file)) {
			byte[] qrCodeImage = QRCodeGenerator.getQRCodeImage(url, 250, 250);
			iofs.write(qrCodeImage);
			amazonS3.putObject(new PutObjectRequest(bucketName, file.getName(), file));
			file.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private List<User> getMemberDetails(long creatorId, long artistId) {
		List<User> users = new ArrayList<>();
		users.add(getUser(creatorId));
		users.add(getUser(artistId));
		return users;
	}
	
	public User getUser(long userId) {
		List<User> users = new ArrayList<>();
		users.add(new User(1234, "Pankaj", "guest", "pankaj.raj@oodles.io"));
		users.add(new User(123456, "Prashant", "guest", "prashat.dave@oodles.io"));
		users.add(new User(12345, "Raj", "star", "pankaj.java.in@gmail.com"));
		users.add(new User(2, "Shubhmoy", "guest", "shub@viak.nl"));
		users.add(new User(1, "Kos", "star", "thestreamingstar@viak.nl"));
		return users.stream().filter(user->user.getUserId()==userId).findFirst().get();
	}
}
