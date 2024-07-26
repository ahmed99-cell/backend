package com.bezkoder.spring.security.postgresql.service;

import com.bezkoder.spring.security.postgresql.Dto.*;
import com.bezkoder.spring.security.postgresql.controllers.QuestionRequestWrapper;
import com.bezkoder.spring.security.postgresql.models.*;
import com.bezkoder.spring.security.postgresql.payload.request.AnswerRequest;
import com.bezkoder.spring.security.postgresql.payload.request.QuestionRequest;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface QuestionService {
    List<QuestionDto> getAllQuestions(QuestionSearchRequestDto searchRequest);
    List<Question> getQuestionsByTag(String tagName);
     QuestionDto mapToDto(Question question);
    Optional<QuestionByIdDto> getQuestionById(Long id);
    List<Question> searchQuestions(String keyword);
    public Question createQuestion(QuestionRequest questionRequest, String username, MultipartFile file, List<Long> tagIds, Boolean isUserAnonymous) ;
    public void associateTagsWithQuestion(Long questionId, List<Long> tagIds);
    public void incrementViewCount(Long questionId);
    List<QuestionDto> getAllQuestions1();

    Question updateQuestion(Long questionId, QuestionRequestWrapper questionRequestWrapper, MultipartFile file);    void deleteQuestion(Long questionId);
    Answer getAnswerById(Long questionId, Long answerId);
    Answer updateAnswer(Long questionId, Long answerId, AnswerRequest answerRequest);
    void deleteAnswer(Long questionId, Long answerId);
    List<Answer> getAnswersByQuestionId(Long questionId);
    Answer createAnswer(Long questionId, AnswerRequest answerRequest, String username,MultipartFile file);
    AnswerResponse createResponseToAnswer(Long questionId, Long parentAnswerId, AnswerRequest answerRequest, String username);
    List<AnswerResponse> getResponsesToAnswer(Long questionId, Long answerId);
    AnswerResponse updateResponseToAnswer(Long questionId, Long parentAnswerId, Long responseId, AnswerRequest answerRequest, String username);
    void deleteResponseToAnswer(Long questionId, Long parentAnswerId, Long responseId, String username);
     void associateTagWithQuestion(Long questionId, Tag tag);
    void dissociateTagFromQuestion(Long questionId, Long tagId);
    List<Question> getQuestionsWithAnswers();
    List<Question> getQuestionsWithoutAnswers();
    List<Question> getQuestionsSortedByVotes();
    List<QuestionDto> findQuestionsByUserIdAndDateRange(Long userId, Date startDate, Date endDate);
    List<AnswerDto> findAnswersByUserIdAndDateRange(Long userId, Date startDate, Date endDate);    Map<Long, Integer> getTotalVotesForAnswers(List<Long> answerIds) ;

    public AnswerDto mapAnswerToDto(Answer answer);
    public AnswerResponseDto mapToAnswerResponseDto(AnswerResponse answerResponse);
    Answer acceptAnswer(Long answerId);



}
