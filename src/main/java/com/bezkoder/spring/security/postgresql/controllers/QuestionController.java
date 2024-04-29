package com.bezkoder.spring.security.postgresql.controllers;

import com.bezkoder.spring.security.postgresql.models.*;
import com.bezkoder.spring.security.postgresql.payload.request.AnswerRequest;
import com.bezkoder.spring.security.postgresql.payload.request.QuestionRequest;
import com.bezkoder.spring.security.postgresql.payload.response.MessageResponse;
import com.bezkoder.spring.security.postgresql.service.QuestionService;
import com.bezkoder.spring.security.postgresql.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {

    @Autowired
    private QuestionService questionService;
    @Autowired
    private TagService tagService;






    @GetMapping("/all")
    public List<Question> getAllQuestions() {
        return questionService.getAllQuestions();
    }
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createQuestion(@Valid @ModelAttribute QuestionRequestWrapper questionRequestWrapper, @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        Question question = questionService.createQuestion(questionRequestWrapper.getQuestionRequest(), username, questionRequestWrapper.getFile());

        // Créer le tag
        Tag tag = questionRequestWrapper.getTag();
        Tag createdTag = tagService.createTag(tag);

        // Associer le tag à la question
        questionService.associateTagWithQuestion(question.getId(), createdTag);

        return ResponseEntity.ok(new MessageResponse("Question and tag created successfully!"));
    }
    @GetMapping("/files/{id}")
    public ResponseEntity<byte[]> getFile(@PathVariable Long id) {
        Question question = questionService.getQuestionById(id)
                .orElseThrow(() -> new RuntimeException("Question not found"));

        String fileName = question.getTitle();
        byte[] fileContent = question.getFile();

        MediaType mediaType = getMediaTypeForFileName(fileName);
        if (mediaType == null) {
            throw new RuntimeException("Unsupported file type");
        }

        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
                .body(fileContent);
    }

    private MediaType getMediaTypeForFileName(String fileName) {
        if (fileName.endsWith(".pdf")) {
            return MediaType.APPLICATION_PDF;
        } else if (fileName.endsWith(".jpeg") || fileName.endsWith(".jpg")) {
            return MediaType.IMAGE_JPEG;
        } else if (fileName.endsWith(".csv")) {
            return new MediaType("text", "csv");
        } else {
            return null;
        }
    }

    @GetMapping("/{questionId}")
    public ResponseEntity<Question> getQuestionById(@PathVariable Long questionId) {
        Question question = questionService.getQuestionById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found"));
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
    @PostMapping("/{questionId}/tags")
    public ResponseEntity<?> associateTagWithQuestion(@PathVariable(value = "questionId") Long questionId, @RequestBody Tag tag) {
        questionService.associateTagWithQuestion(questionId, tag);
        return ResponseEntity.ok().build();
    }


    @DeleteMapping("/{questionId}/tags/{tagId}")
    public ResponseEntity<?> dissociateTagFromQuestion(@PathVariable(value = "questionId") Long questionId, @PathVariable(value = "tagId") Long tagId) {
        questionService.dissociateTagFromQuestion(questionId, tagId);
        return ResponseEntity.ok().build();
    }


}
