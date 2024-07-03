package com.bezkoder.spring.security.postgresql.controllers;

import com.bezkoder.spring.security.postgresql.Dto.AnswerDto;
import com.bezkoder.spring.security.postgresql.Dto.QuestionByIdDto;
import com.bezkoder.spring.security.postgresql.Dto.QuestionDto;
import com.bezkoder.spring.security.postgresql.Dto.QuestionSearchRequestDto;
import com.bezkoder.spring.security.postgresql.models.*;
import com.bezkoder.spring.security.postgresql.payload.request.AnswerRequest;
import com.bezkoder.spring.security.postgresql.payload.request.QuestionRequest;
import com.bezkoder.spring.security.postgresql.payload.response.MessageResponse;
import com.bezkoder.spring.security.postgresql.repository.QuestionRepository;
import com.bezkoder.spring.security.postgresql.service.QuestionService;
import com.bezkoder.spring.security.postgresql.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {

    @Autowired
    private QuestionService questionService;
    @Autowired
    private TagService tagService;


    @GetMapping("/search")
    public List<Question> searchQuestions(@RequestParam String keyword) {
        return questionService.searchQuestions(keyword);
    }

    @PostMapping("/all")
    public List<QuestionDto> getAllQuestions(@RequestBody QuestionSearchRequestDto searchRequest) {

        return questionService.getAllQuestions(searchRequest);
    }
    @GetMapping("/by-user-and-date")
    public List<Question> getQuestionsByUserIdAndDateRange(
            @RequestParam Long userId,
            @RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") Date startDate,
            @RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") Date endDate) {
        return questionService.findQuestionsByUserIdAndDateRange(userId, startDate, endDate);
    }

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createQuestion(@Valid @ModelAttribute QuestionRequestWrapper questionRequestWrapper, @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        Boolean isUserAnonymous = questionRequestWrapper.getUserAnonymous();
        List<Long> tagIds = questionRequestWrapper.getTagIds(); // Change to receive a list of tag IDs
        if (tagIds == null || tagIds.isEmpty()) {
            return ResponseEntity.badRequest().body("Tag IDs must not be null or empty");
        }

        questionService.createQuestion(questionRequestWrapper.getQuestionRequest(), username, questionRequestWrapper.getFile(), tagIds, isUserAnonymous);

        return ResponseEntity.ok(new MessageResponse("Question created and associated with tag(s) successfully!"));
    }

    @GetMapping("/byTag/{tagName}")
    public ResponseEntity<List<Question>> getQuestionsByTag(@PathVariable String tagName) {
        List<Question> questions = questionService.getQuestionsByTag(tagName);
        return ResponseEntity.ok(questions);
    }



    @GetMapping("/files/{id}")
    public ResponseEntity<byte[]> getFile(@PathVariable Long id) {
        QuestionByIdDto question = questionService.getQuestionById(id)
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
    public ResponseEntity<QuestionByIdDto> getQuestionById(@PathVariable Long questionId) {
        QuestionByIdDto questionDto = questionService.getQuestionById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found"));
        return ResponseEntity.ok().body(questionDto);
    }


    @PutMapping(value = "/{questionId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateQuestion(
            @PathVariable Long questionId,
            @RequestPart("questionRequest") QuestionRequest questionRequest,
            @RequestPart(value = "file", required = false) MultipartFile file) {

        Question question = questionService.updateQuestion(questionId, questionRequest, file);
        return ResponseEntity.ok(new MessageResponse("Question updated successfully!"));
    }


    @DeleteMapping("/{questionId}")
    public ResponseEntity<?> deleteQuestion(@PathVariable Long questionId) {
        questionService.deleteQuestion(questionId);
        return ResponseEntity.ok(new MessageResponse("Question deleted successfully!"));
    }

    @PutMapping("/{questionId}/increment-view")
    public ResponseEntity<?> incrementViewCount(@PathVariable Long questionId) {
        questionService.incrementViewCount(questionId);
        return ResponseEntity.ok().build();
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

    @GetMapping("/byuseranddate")
    public List<Answer> getAnswersByUserIdAndDateRange(
            @RequestParam Long userId,
            @RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") Date startDate,
            @RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") Date endDate) {
        return questionService.findAnswersByUserIdAndDateRange(userId, startDate, endDate);
    }



    @GetMapping("/{questionId}/answers")
    public ResponseEntity<List<Map<String, Object>>> getAnswersByQuestionId(@PathVariable Long questionId) {
        List<Answer> answers = questionService.getAnswersByQuestionId(questionId);
        List<Long> answerIds = answers.stream().map(Answer::getId).collect(Collectors.toList());
        Map<Long, Integer> votesMap = questionService.getTotalVotesForAnswers(answerIds);

        List<Map<String, Object>> response = answers.stream().map(answer -> {
            Map<String, Object> answerWithVotes = new HashMap<>();
            answerWithVotes.put("answer", answer);
            answerWithVotes.put("totalVotes", votesMap.get(answer.getId()));
            return answerWithVotes;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }


    @PostMapping("/{questionId}/answers")
    public ResponseEntity<?> createAnswer(@PathVariable Long questionId, @Valid @RequestBody AnswerRequest answerRequest, @AuthenticationPrincipal UserDetails userDetails, @RequestParam(value = "file", required = false) MultipartFile image) {
        try {
            // Récupérer le nom d'utilisateur de l'utilisateur authentifié
            String username = userDetails.getUsername();

            // Vérifier si une image est présente dans la requête
            if (image != null && !image.isEmpty()) {
                // Convertir l'image en tableau d'octets
                byte[] imageData = image.getBytes();

                // Appeler le service pour créer la réponse avec les données de l'image
                Answer answer = questionService.createAnswer(questionId, answerRequest, username, imageData);

                // Répondre avec un message de succès
                return ResponseEntity.ok(new MessageResponse("Answer created successfully!"));
            } else {
                // Si aucune image n'est présente, appeler le service pour créer la réponse sans données d'image
                Answer answer = questionService.createAnswer(questionId, answerRequest, username, null);

                // Répondre avec un message de succès
                return ResponseEntity.ok(new MessageResponse("Answer created successfully!"));
            }
        } catch (IOException e) {
            // En cas d'erreur lors du traitement de l'image, répondre avec un message d'erreur
            return ResponseEntity.badRequest().body(new MessageResponse("Failed to upload image: " + e.getMessage()));
        }
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
    @GetMapping("/questionsWithAnswers")
    public ResponseEntity<List<Question>> getQuestionsWithAnswers() {
        return ResponseEntity.ok(questionService.getQuestionsWithAnswers());
    }

    @GetMapping("/questionsWithoutAnswers")
    public ResponseEntity<List<Question>> getQuestionsWithoutAnswers() {
        return ResponseEntity.ok(questionService.getQuestionsWithoutAnswers());
    }
    @GetMapping("/sorted-by-votes")
    public ResponseEntity<List<Question>> getQuestionsSortedByVotes() {
        List<Question> questions = questionService.getQuestionsSortedByVotes();
        return ResponseEntity.ok(questions);
    }
}
