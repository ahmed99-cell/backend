package com.bezkoder.spring.security.postgresql.service;

import com.bezkoder.spring.security.postgresql.models.*;
import com.bezkoder.spring.security.postgresql.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class VoteServiceImp implements VoteService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private AnswerResponseRepository answerResponseRepository;

    @Autowired
    private VoteRepository voteRepository;

    public ResponseEntity<String> vote(Long userId, Long entityId, String entityType, int value) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Error: User is not found."));

        Vote vote = voteRepository.findByUserAndEntityIdAndEntityType(user, entityId, entityType);
System.out.println(vote)   ;
        if (vote != null) {
            if (value == 0 || value == 1) {
                vote.setValue(value);
            } else {
                throw new RuntimeException("Error: Vote value is not valid.");
            }
        } else {
            if (value >= -1 && value <= 1) {
                vote = new Vote();
                vote.setUser(user);
                vote.setEntityId(entityId);
                vote.setEntityType(entityType);
                vote.setValue(value);

                switch (entityType) {
                    case "Question":
                        Question question = questionRepository.findById(entityId)
                                .orElseThrow(() -> new RuntimeException("Error: Question is not found."));
                        vote.setQuestion(question);
                        break;
                    case "Answer":
                        Answer answer = answerRepository.findById(entityId)
                                .orElseThrow(() -> new RuntimeException("Error: Answer is not found."));
                        vote.setAnswer(answer);
                        break;
                    case "AnswerResponse":
                        AnswerResponse answerResponse = answerResponseRepository.findById(entityId)
                                .orElseThrow(() -> new RuntimeException("Error: AnswerResponse is not found."));
                        vote.setAnswerResponse(answerResponse);
                        break;
                    default:
                        throw new RuntimeException("Error: Invalid entityType.");
                }
            } else {
                throw new RuntimeException("Error: Vote value is not valid2.");
            }
        }

        voteRepository.save(vote);
        return ResponseEntity.ok("Vote successful");
    }



    public int getVoteValue(Long userId, Long entityId, String entityType) {


        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Error: User is not found."));
        Vote vote = voteRepository.findByUserAndEntityIdAndEntityType(user, entityId, entityType);
        return vote != null ? vote.getValue() : -1; // Assuming -1 means no vote
    }
}

