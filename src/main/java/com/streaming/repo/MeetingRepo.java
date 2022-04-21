package com.streaming.repo;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.streaming.entities.Meeting;
@Repository
public interface MeetingRepo extends MongoRepository<Meeting, String> {

	Optional<Meeting> findByMeetingIdAndDeleted(String meetingId, boolean deleted);

}
