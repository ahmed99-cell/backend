package com.bezkoder.spring.security.postgresql.service;

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
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
public class QuestionServiceImp implements QuestionService{
    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AnswerResponseRepository answerResponseRepository;

    @Override
    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }

    @Override
    public Question createQuestion(QuestionRequest questionRequest, String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        Question question = new Question();
        question.setTitle(questionRequest.getTitle());
        question.setContent(questionRequest.getContent());
        question.setUser(user);
        question.setCreatedAt(new Date());
        return questionRepository.save(question);
    }

    @Override
    public Question getQuestionById(Long questionId) {
        return questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found"));
    }

    @Override
    public Question updateQuestion(Long questionId, QuestionRequest questionRequest) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found"));
        question.setTitle(questionRequest.getTitle());
        question.setContent(questionRequest.getContent());
        question.setUpdatedAt(new Date());
        return questionRepository.save(question);
    }

    @Override
    public void deleteQuestion(Long questionId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found"));
        questionRepository.delete(question);
    }

    @Override
    public Answer getAnswerById(Long questionId, Long answerId) {
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new RuntimeException("Answer not found"));
        if (!answer.getQuestion().getId().equals(questionId)) {
            throw new RuntimeException("Answer does not belong to the specified question");
        }
        return answer;
    }

    @Override
    public Answer updateAnswer(Long questionId, Long answerId, AnswerRequest answerRequest) {
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new RuntimeException("Answer not found"));
        if (!answer.getQuestion().getId().equals(questionId)) {
            throw new RuntimeException("Answer does not belong to the specified question");
        }
        answer.setContent(answerRequest.getContent());
        answer.setUpdatedAt(new Date());
        return answerRepository.save(answer);
    }

    @Override
    public void deleteAnswer(Long questionId, Long answerId) {
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new RuntimeException("Answer not found"));
        if (!answer.getQuestion().getId().equals(questionId)) {
            throw new RuntimeException("Answer does not belong to the specified question");
        }
        answerRepository.delete(answer);
    }

    @Override
    public List<Answer> getAnswersByQuestionId(Long questionId) {
        return answerRepository.findByQuestionId(questionId);
    }

    @Override
    public Answer createAnswer(Long questionId, AnswerRequest answerRequest, String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        Question question = questionRepository.findById(questionId).orElseThrow(() -> new RuntimeException("Question not found"));
        Answer answer = new Answer();
        answer.setContent(answerRequest.getContent());
        answer.setUser(user);
        answer.setQuestion(question);
        answer.setCreatedAt(new Date());
        return answerRepository.save(answer);
    }

    @Override
    public AnswerResponse createResponseToAnswer(Long questionId, Long parentAnswerId, AnswerRequest answerRequest, String username) {
        // Trouver l'utilisateur authentifié
        User user = userRepository.findByUsername(username)
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

        // Sauvegarder la réponse à la réponse et la retourner
        return answerResponseRepository.save(response);
    }




    @Override
    public List<AnswerResponse> getResponsesToAnswer(Long questionId, Long answerId) {
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new RuntimeException("Answer not found"));

        if (!answer.getQuestion().getId().equals(questionId)) {
            throw new RuntimeException("Answer does not belong to the specified question");
        }

        Set<AnswerResponse> responses = answer.getResponses();
        return new ArrayList<>(responses);
    }

    @Override
    public AnswerResponse updateResponseToAnswer(Long questionId, Long parentAnswerId, Long responseId, AnswerRequest answerRequest, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Answer parentAnswer = answerRepository.findById(parentAnswerId)
                .orElseThrow(() -> new RuntimeException("Parent Answer not found"));

        if (!parentAnswer.getQuestion().getId().equals(questionId)) {
            throw new RuntimeException("Parent Answer does not belong to the specified question");
        }

        AnswerResponse response = answerResponseRepository.findById(responseId)
                .orElseThrow(() -> new RuntimeException("Response to Answer not found"));

        if (!response.getUser().getUsername().equals(username)) {
            throw new RuntimeException("User is not authorized to update this response to answer");
        }

        response.setContent(answerRequest.getContent());

        return answerResponseRepository.save(response);
    }

    @Override
    public void deleteResponseToAnswer(Long questionId, Long parentAnswerId, Long responseId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Answer parentAnswer = answerRepository.findById(parentAnswerId)
                .orElseThrow(() -> new RuntimeException("Parent Answer not found"));

        if (!parentAnswer.getQuestion().getId().equals(questionId)) {
            throw new RuntimeException("Parent Answer does not belong to the specified question");
        }

        AnswerResponse response = answerResponseRepository.findById(responseId)
                .orElseThrow(() -> new RuntimeException("Response to Answer not found"));

        if (!response.getUser().getUsername().equals(username)) {
            throw new RuntimeException("User is not authorized to delete this response to answer");
        }

        answerResponseRepository.delete(response);
    }
}
