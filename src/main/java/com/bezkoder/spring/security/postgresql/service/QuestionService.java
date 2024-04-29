package com.bezkoder.spring.security.postgresql.service;

import com.bezkoder.spring.security.postgresql.models.Answer;
import com.bezkoder.spring.security.postgresql.models.AnswerResponse;
import com.bezkoder.spring.security.postgresql.models.Question;
import com.bezkoder.spring.security.postgresql.models.Tag;
import com.bezkoder.spring.security.postgresql.payload.request.AnswerRequest;
import com.bezkoder.spring.security.postgresql.payload.request.QuestionRequest;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface QuestionService {
    List<Question> getAllQuestions();
    Question createQuestion(QuestionRequest questionRequest, String username, @RequestParam("file") MultipartFile file)   ;
    Question getQuestionById(Long questionId);
    Question updateQuestion(Long questionId, QuestionRequest questionRequest);
    void deleteQuestion(Long questionId);
    Answer getAnswerById(Long questionId, Long answerId);
    Answer updateAnswer(Long questionId, Long answerId, AnswerRequest answerRequest);
    void deleteAnswer(Long questionId, Long answerId);
    List<Answer> getAnswersByQuestionId(Long questionId);
    Answer createAnswer(Long questionId, AnswerRequest answerRequest, String username);
    AnswerResponse createResponseToAnswer(Long questionId, Long parentAnswerId, AnswerRequest answerRequest, String username);
    List<AnswerResponse> getResponsesToAnswer(Long questionId, Long answerId);
    AnswerResponse updateResponseToAnswer(Long questionId, Long parentAnswerId, Long responseId, AnswerRequest answerRequest, String username);
    void deleteResponseToAnswer(Long questionId, Long parentAnswerId, Long responseId, String username);
     void associateTagWithQuestion(Long questionId, Tag tag);
    void dissociateTagFromQuestion(Long questionId, Long tagId);

}
