package com.bezkoder.spring.security.postgresql.controllers;

import com.bezkoder.spring.security.postgresql.models.*;
import com.bezkoder.spring.security.postgresql.payload.request.AnswerRequest;
import com.bezkoder.spring.security.postgresql.payload.request.QuestionRequest;
import com.bezkoder.spring.security.postgresql.payload.response.MessageResponse;
import com.bezkoder.spring.security.postgresql.repository.AnswerRepository;
import com.bezkoder.spring.security.postgresql.repository.AnswerResponseRepository;
import com.bezkoder.spring.security.postgresql.repository.QuestionRepository;
import com.bezkoder.spring.security.postgresql.repository.UserRepository;
import com.bezkoder.spring.security.postgresql.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {

    @Autowired
    private QuestionService questionService;
    @GetMapping("/all")
    public List<Question> getAllQuestions() {
        return questionService.getAllQuestions();
    }
    @PostMapping("/create")
    public ResponseEntity<?> createQuestion(@Valid @RequestBody QuestionRequest questionRequest, @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        Question question = questionService.createQuestion(questionRequest, username);
        return ResponseEntity.ok(new MessageResponse("Question created successfully!"));
    }
    @GetMapping("/{questionId}")
    public ResponseEntity<Question> getQuestionById(@PathVariable Long questionId) {
        Question question = questionService.getQuestionById(questionId);
        return ResponseEntity.ok().body(question);
    }
    @PutMapping("/{questionId}")
    public ResponseEntity<?> updateQuestion(@PathVariable Long questionId, @Valid @RequestBody QuestionRequest questionRequest) {
        Question question = questionService.updateQuestion(questionId, questionRequest);
        return ResponseEntity.ok(new MessageResponse("Question updated successfully!"));
    }
    @DeleteMapping("/{questionId}")
    public ResponseEntity<?> deleteQuestion(@PathVariable Long questionId) {
        questionService.deleteQuestion(questionId);
        return ResponseEntity.ok(new MessageResponse("Question deleted successfully!"));
    }
    @GetMapping("/{questionId}/answers/{answerId}")
    public ResponseEntity<Answer> getAnswerById(@PathVariable Long questionId, @PathVariable Long answerId) {
        Answer answer = questionService.getAnswerById(questionId, answerId);
        return ResponseEntity.ok().body(answer);
    }
    @PutMapping("/{questionId}/answers/{answerId}")
    public ResponseEntity<?> updateAnswer(@PathVariable Long questionId, @PathVariable Long answerId, @Valid @RequestBody AnswerRequest answerRequest) {
        Answer answer = questionService.updateAnswer(questionId, answerId, answerRequest);
        return ResponseEntity.ok(new MessageResponse("Answer updated successfully!"));
    }
    @DeleteMapping("/{questionId}/answers/{answerId}")
    public ResponseEntity<?> deleteAnswer(@PathVariable Long questionId, @PathVariable Long answerId) {
        questionService.deleteAnswer(questionId, answerId);
        return ResponseEntity.ok(new MessageResponse("Answer deleted successfully!"));
    }



    @GetMapping("/{questionId}/answers")
    public List<Answer> getAnswersByQuestionId(@PathVariable Long questionId) {
        return questionService.getAnswersByQuestionId(questionId);
    }
    @PostMapping("/{questionId}/answers")
    public ResponseEntity<?> createAnswer(@PathVariable Long questionId, @Valid @RequestBody AnswerRequest answerRequest, @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        Answer answer = questionService.createAnswer(questionId, answerRequest, username);
        return ResponseEntity.ok(new MessageResponse("Answer created successfully!"));
    }
    @PostMapping("/{questionId}/answers/{parentAnswerId}/responses")
    public ResponseEntity<?> createResponseToAnswer(@PathVariable Long questionId, @PathVariable Long parentAnswerId, @Valid @RequestBody AnswerRequest answerRequest, @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        AnswerResponse answer = questionService.createResponseToAnswer(questionId, parentAnswerId, answerRequest, username);
        return ResponseEntity.ok(new MessageResponse("Response to Answer created successfully!"));
    }

    @GetMapping("/{questionId}/answers/{answerId}/responses")
    public ResponseEntity<List<AnswerResponse>> getResponsesToAnswer(@PathVariable Long questionId, @PathVariable Long answerId) {
        List<AnswerResponse> responses = questionService.getResponsesToAnswer(questionId, answerId);
        return ResponseEntity.ok().body(responses);
    }
    @PutMapping("/{questionId}/answers/{parentAnswerId}/responses/{responseId}")
    public ResponseEntity<?> updateResponseToAnswer(@PathVariable Long questionId, @PathVariable Long parentAnswerId, @PathVariable Long responseId, @Valid @RequestBody AnswerRequest answerRequest, @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        AnswerResponse response = questionService.updateResponseToAnswer(questionId, parentAnswerId, responseId, answerRequest, username);
        return ResponseEntity.ok(new MessageResponse("Response to Answer updated successfully!"));
    }
    @DeleteMapping("/{questionId}/answers/{parentAnswerId}/responses/{responseId}")
    public ResponseEntity<?> deleteResponseToAnswer(@PathVariable Long questionId, @PathVariable Long parentAnswerId, @PathVariable Long responseId, @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        questionService.deleteResponseToAnswer(questionId, parentAnswerId, responseId, username);
        return ResponseEntity.ok(new MessageResponse("Response to Answer deleted successfully!"));
    }


}
