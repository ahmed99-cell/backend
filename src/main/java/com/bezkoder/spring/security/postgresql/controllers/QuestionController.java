package com.bezkoder.spring.security.postgresql.controllers;

import com.bezkoder.spring.security.postgresql.models.Answer;
import com.bezkoder.spring.security.postgresql.models.AnswerResponse;
import com.bezkoder.spring.security.postgresql.models.Question;
import com.bezkoder.spring.security.postgresql.models.User;
import com.bezkoder.spring.security.postgresql.payload.request.AnswerRequest;
import com.bezkoder.spring.security.postgresql.payload.request.QuestionRequest;
import com.bezkoder.spring.security.postgresql.payload.response.MessageResponse;
import com.bezkoder.spring.security.postgresql.repository.AnswerRepository;
import com.bezkoder.spring.security.postgresql.repository.AnswerResponseRepository;
import com.bezkoder.spring.security.postgresql.repository.QuestionRepository;
import com.bezkoder.spring.security.postgresql.repository.UserRepository;
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
    private QuestionRepository questionRepository;

    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AnswerResponseRepository answerResponseRepository;
    @GetMapping("/questions")
    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }
    @PostMapping("/qst")
    public ResponseEntity<?> createQuestion(@Valid @RequestBody QuestionRequest questionRequest, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse("Unauthorized access"));
        }

        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow(() -> new RuntimeException("User not found"));
        Question question = new Question();
        question.setTitle(questionRequest.getTitle());
        question.setContent(questionRequest.getContent());
        question.setUser(user);
        question.setCreatedAt(new Date());
        questionRepository.save(question);
        return ResponseEntity.ok(new MessageResponse("Question created successfully!"));
    }
    @GetMapping("/{questionId}")
    public ResponseEntity<Question> getQuestionById(@PathVariable Long questionId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found"));
        return ResponseEntity.ok().body(question);
    }
    @PutMapping("/{questionId}")
    public ResponseEntity<?> updateQuestion(@PathVariable Long questionId, @Valid @RequestBody QuestionRequest questionRequest) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found"));
        question.setTitle(questionRequest.getTitle());
        question.setContent(questionRequest.getContent());
        question.setUpdatedAt(new Date());
        questionRepository.save(question);
        return ResponseEntity.ok(new MessageResponse("Question updated successfully!"));
    }
    @DeleteMapping("/{questionId}")
    public ResponseEntity<?> deleteQuestion(@PathVariable Long questionId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found"));
        questionRepository.delete(question);
        return ResponseEntity.ok(new MessageResponse("Question deleted successfully!"));
    }
    @GetMapping("/{questionId}/answers/{answerId}")
    public ResponseEntity<Answer> getAnswerById(@PathVariable Long questionId, @PathVariable Long answerId) {
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new RuntimeException("Answer not found"));
        if (!answer.getQuestion().getId().equals(questionId)) {
            throw new RuntimeException("Answer does not belong to the specified question");
        }
        return ResponseEntity.ok().body(answer);
    }
    @PutMapping("/{questionId}/answers/{answerId}")
    public ResponseEntity<?> updateAnswer(@PathVariable Long questionId, @PathVariable Long answerId, @Valid @RequestBody AnswerRequest answerRequest) {
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new RuntimeException("Answer not found"));
        // Vérifiez si la réponse appartient à la question spécifiée
        if (!answer.getQuestion().getId().equals(questionId)) {
            throw new RuntimeException("Answer does not belong to the specified question");
        }
        answer.setContent(answerRequest.getContent());
        answer.setUpdatedAt(new Date());
        answerRepository.save(answer);
        return ResponseEntity.ok(new MessageResponse("Answer updated successfully!"));
    }
    @DeleteMapping("/{questionId}/answers/{answerId}")
    public ResponseEntity<?> deleteAnswer(@PathVariable Long questionId, @PathVariable Long answerId) {
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new RuntimeException("Answer not found"));
        if (!answer.getQuestion().getId().equals(questionId)) {
            throw new RuntimeException("Answer does not belong to the specified question");
        }
        answerRepository.delete(answer);
        return ResponseEntity.ok(new MessageResponse("Answer deleted successfully!"));
    }



    @GetMapping("/questions/{questionId}/answers")
    public List<Answer> getAnswersByQuestionId(@PathVariable Long questionId) {
        return answerRepository.findByQuestionId(questionId);
    }
    @PostMapping("/{questionId}/answers")
    public ResponseEntity<?> createAnswer(@PathVariable Long questionId, @Valid @RequestBody AnswerRequest answerRequest, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow(() -> new RuntimeException("User not found"));
        Question question = questionRepository.findById(questionId).orElseThrow(() -> new RuntimeException("Question not found"));
        Answer answer = new Answer();
        answer.setContent(answerRequest.getContent());
        answer.setUser(user);
        answer.setQuestion(question);
        answer.setCreatedAt(new Date());
        answerRepository.save(answer);
        return ResponseEntity.ok(new MessageResponse("Answer created successfully!"));
    }
    @PostMapping("/{questionId}/answers/{parentAnswerId}/responses")
    public ResponseEntity<?> createResponseToAnswer(
            @PathVariable Long questionId,
            @PathVariable Long parentAnswerId,
            @Valid @RequestBody AnswerRequest answerRequest,
            @AuthenticationPrincipal UserDetails userDetails) {

        // Vérifier si l'utilisateur est authentifié
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse("Unauthorized access"));
        }

        // Trouver l'utilisateur authentifié
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Trouver la réponse parente
        Answer parentAnswer = answerRepository.findById(parentAnswerId)
                .orElseThrow(() -> new RuntimeException("Parent Answer not found"));

        // Assurer que la réponse parente appartient à la question spécifiée
        if (!parentAnswer.getQuestion().getId().equals(questionId)) {
            throw new RuntimeException("Parent Answer does not belong to the specified question");
        }

        // Créer la réponse à la réponse
        AnswerResponse response = new AnswerResponse();
        response.setContent(answerRequest.getContent());
        response.setUser(user);
        response.setParentAnswer(parentAnswer);
        response.setCreatedAt(new Date());
        answerResponseRepository.save(response);

        return ResponseEntity.ok(new MessageResponse("Response to Answer created successfully!"));
    }

    @GetMapping("/{questionId}/answers/{answerId}/responses")
    public ResponseEntity<List<AnswerResponse>> getResponsesToAnswer(
            @PathVariable Long questionId,
            @PathVariable Long answerId) {

        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new RuntimeException("Answer not found"));

        // Vérifier que la réponse appartient à la question spécifiée
        if (!answer.getQuestion().getId().equals(questionId)) {
            throw new RuntimeException("Answer does not belong to the specified question");
        }

        Set<AnswerResponse> responses = answer.getResponses();
        return ResponseEntity.ok().body(new ArrayList<>(responses));
    }
    @PutMapping("/{questionId}/answers/{parentAnswerId}/responses/{responseId}")
    public ResponseEntity<?> updateResponseToAnswer(
            @PathVariable Long questionId,
            @PathVariable Long parentAnswerId,
            @PathVariable Long responseId,
            @Valid @RequestBody AnswerRequest answerRequest,
            @AuthenticationPrincipal UserDetails userDetails) {

        // Vérifier si l'utilisateur est authentifié
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse("Unauthorized access"));
        }

        // Trouver la réponse parente
        Answer parentAnswer = answerRepository.findById(parentAnswerId)
                .orElseThrow(() -> new RuntimeException("Parent Answer not found"));

        // Assurer que la réponse parente appartient à la question spécifiée
        if (!parentAnswer.getQuestion().getId().equals(questionId)) {
            throw new RuntimeException("Parent Answer does not belong to the specified question");
        }

        // Trouver la réponse à la réponse
        AnswerResponse response = answerResponseRepository.findById(responseId)
                .orElseThrow(() -> new RuntimeException("Response to Answer not found"));

        // Vérifier si l'utilisateur authentifié est le créateur de la réponse à la réponse
        if (!response.getUser().getUsername().equals(userDetails.getUsername())) {
            throw new RuntimeException("User is not authorized to update this response to answer");
        }

        // Mettre à jour le contenu de la réponse à la réponse
        response.setContent(answerRequest.getContent());

        // Enregistrer les modifications
        answerResponseRepository.save(response);

        return ResponseEntity.ok(new MessageResponse("Response to Answer updated successfully!"));
    }
    @DeleteMapping("/{questionId}/answers/{parentAnswerId}/responses/{responseId}")
    public ResponseEntity<?> deleteResponseToAnswer(
            @PathVariable Long questionId,
            @PathVariable Long parentAnswerId,
            @PathVariable Long responseId,
            @AuthenticationPrincipal UserDetails userDetails) {

        // Vérifier si l'utilisateur est authentifié
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse("Unauthorized access"));
        }

        // Trouver la réponse parente
        Answer parentAnswer = answerRepository.findById(parentAnswerId)
                .orElseThrow(() -> new RuntimeException("Parent Answer not found"));

        // Assurer que la réponse parente appartient à la question spécifiée
        if (!parentAnswer.getQuestion().getId().equals(questionId)) {
            throw new RuntimeException("Parent Answer does not belong to the specified question");
        }

        // Trouver la réponse à la réponse
        AnswerResponse response = answerResponseRepository.findById(responseId)
                .orElseThrow(() -> new RuntimeException("Response to Answer not found"));

        // Vérifier si l'utilisateur authentifié est le créateur de la réponse à la réponse
        if (!response.getUser().getUsername().equals(userDetails.getUsername())) {
            throw new RuntimeException("User is not authorized to delete this response to answer");
        }

        // Supprimer la réponse à la réponse
        answerResponseRepository.delete(response);

        return ResponseEntity.ok(new MessageResponse("Response to Answer deleted successfully!"));
    }


}
