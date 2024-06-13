package com.bezkoder.spring.security.postgresql.service;

import com.bezkoder.spring.security.postgresql.Dto.QuestionDto;
import com.bezkoder.spring.security.postgresql.Dto.QuestionSearchRequestDto;
import com.bezkoder.spring.security.postgresql.Exeception.ResourceNotFoundException;
import com.bezkoder.spring.security.postgresql.models.*;
import com.bezkoder.spring.security.postgresql.payload.request.AnswerRequest;
import com.bezkoder.spring.security.postgresql.payload.request.QuestionRequest;
import com.bezkoder.spring.security.postgresql.payload.response.MessageResponse;
import com.bezkoder.spring.security.postgresql.repository.*;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    QuestionRepositoryCustom questionRepositoryCustom;

    @Autowired
    private VoteRepository voteRepository;

    @Override
    public List<QuestionDto> getAllQuestions(QuestionSearchRequestDto searchRequest) {
        Pageable pageable = PageRequest.of(searchRequest.getPageIndex(), searchRequest.getPageSize());
        List<Question> questions = questionRepositoryCustom.findByCriteria(
                searchRequest.getTitle(),
                searchRequest.getContent(),
                searchRequest.getUserId(),
                searchRequest.getTags(),
                searchRequest.getUserAnonymous(),
                pageable
        );

        return questions.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }
   @Override
    public List<Question> getQuestionsByTag(String tagName) {
        return questionRepository.findByTagsName(tagName);
    }
    @Override
    public QuestionDto mapToDto(Question question) {
        QuestionDto dto = new QuestionDto();
        dto.setId(question.getId());
        dto.setTitle(question.getTitle());
        dto.setContent(question.getContent());
        Boolean isUserAnonymous = question.getUserAnonymous();
        if (isUserAnonymous == null || !isUserAnonymous) {
            dto.setUsername(question.getUser().getUsername());
        }
        dto.setCreatedAt(question.getCreatedAt());
        dto.setUpdatedAt(question.getUpdatedAt());
        dto.setTags(question.getTags().stream().map(Tag::getName).collect(Collectors.toSet()));
        dto.setViews(question.getViews());
        int voteCount = question.getVotes().size();
        dto.setVoteCount(voteCount);

        int answerCount = question.getAnswers().size();
        dto.setAnswerCount(answerCount);
        return dto;
    }
    @Override
    public Question createQuestion(QuestionRequest questionRequest, String username, MultipartFile file, List<Long> tagIds, Boolean isUserAnonymous) {
        if (questionRequest == null) {
            throw new IllegalArgumentException("QuestionRequest cannot be null");
        }

        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        List<Tag> tags = tagRepository.findAllById(tagIds);
        if (tags.size() != tagIds.size()) {
            // Check if all tags were found
            throw new RuntimeException("Some tags were not found");
        }

        Question question = new Question();
        question.setTitle(questionRequest.getTitle());
        question.setContent(questionRequest.getContent());
        question.setUser(user);
        question.setCreatedAt(new Date());
        question.getTags().addAll(tags);
        question.setUserAnonymous(isUserAnonymous);

        if (file != null) {
            String contentType = file.getContentType();

            if (!contentType.equals("image/jpeg") &&
                    !contentType.equals("image/png") &&
                    !contentType.equals("application/pdf")) {
                throw new RuntimeException("Unsupported file type");
            }

            try {
                question.setFile(file.getBytes());
                question.setContentType(file.getContentType());
            } catch (IOException e) {
                throw new RuntimeException("Error reading file", e);
            }
        }

        questionRepository.save(question);
        return question;
    }


    @Override
    public void associateTagsWithQuestion(Long questionId, List<Long> tagIds) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question", "id", questionId));

        List<Tag> tags = tagRepository.findAllById(tagIds);

        if (tags.size() != tagIds.size()) {
            // Check if all tags were found
            throw new ResourceNotFoundException("Some tags were not found");
        }

        question.getTags().addAll(tags);
        questionRepository.save(question);
    }

    @Override
    public void incrementViewCount(Long questionId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found with id: " + questionId));

        int currentViewCount = question.getViews();
        question.setViews(currentViewCount + 1);

        questionRepository.save(question);
    }

    @Override
    public Optional<Question> getQuestionById(Long id) {
        return questionRepository.findById(id);
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
    public Map<Long, Integer> getTotalVotesForAnswers(List<Long> answerIds) {
        Map<Long, Integer> votesMap = new HashMap<>();
        for (Long answerId : answerIds) {
            int totalVotes = voteRepository.sumValuesByEntityIds(answerId);
            votesMap.put(answerId, totalVotes);
        }
        return votesMap;
    }

    @Override
    public Answer createAnswer(Long questionId, AnswerRequest answerRequest, String username,byte[] imageData) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        Question question = questionRepository.findById(questionId).orElseThrow(() -> new RuntimeException("Question not found"));
        Answer answer = new Answer();
        answer.setContent(answerRequest.getContent());
        answer.setUser(user);
        answer.setQuestion(question);
        answer.setFile(imageData);
        answer.setCreatedAt(new Date());
        Answer savedAnswer = answerRepository.save(answer);

        Notification notification = new Notification();
        notification.setUser(question.getUser());
        notification.setContent("Une nouvelle réponse a été ajoutée à votre question");
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now());

        notificationRepository.save(notification);
        sendNotificationEmail(question.getUser(), "Une nouvelle réponse a été ajoutée à votre question");


        return savedAnswer;
    }

    @Override
    public AnswerResponse createResponseToAnswer(Long questionId, Long parentAnswerId, AnswerRequest answerRequest, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Answer parentAnswer = answerRepository.findById(parentAnswerId)
                .orElseThrow(() -> new RuntimeException("Parent Answer not found"));

        if (!parentAnswer.getQuestion().getId().equals(questionId)) {
            throw new RuntimeException("Parent Answer does not belong to the specified question");
        }

        AnswerResponse response = new AnswerResponse();
        response.setContent(answerRequest.getContent());
        response.setUser(user);
        response.setParentAnswer(parentAnswer);
        response.setCreatedAt(new Date());
        AnswerResponse savedResponse = answerResponseRepository.save(response);

        Notification notification = new Notification();
        notification.setUser(parentAnswer.getUser());
        notification.setContent("Une nouvelle réponse à une réponse a été ajoutée");
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now());

        notificationRepository.save(notification);
        sendNotificationEmail(parentAnswer.getUser(), "Une nouvelle réponse a été ajoutée à votre réponse de question");


        return savedResponse;
    }
    private void sendNotificationEmail(User user, String content) {
        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(user.getEmail());
        email.setSubject("Notification");
        email.setText(content);
        mailSender.send(email);
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
    public void associateTagWithQuestion(Long questionId, Tag tag) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question", "id", questionId));

        question.getTags().add(tag);
        questionRepository.save(question);
    }

    // Dissocier un tag d'une question
    public void dissociateTagFromQuestion(Long questionId, Long tagId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question", "id", questionId));

        question.getTags().removeIf(tag -> tag.getId().equals(tagId));
        questionRepository.save(question);
    }

    @Override
    public List<Question> getQuestionsWithAnswers() {
        List<Question> questions = questionRepository.findAll().stream()
                .filter(question -> !question.getAnswers().isEmpty())
                .collect(Collectors.toList());

        // Initialize the user field for each question
        for (Question question : questions) {
            Hibernate.initialize(question.getUser());
        }

        return questions;
    }



    @Override
    public List<Question> getQuestionsWithoutAnswers() {
        return questionRepository.findAll().stream()
                .filter(question -> question.getAnswers().isEmpty())
                .collect(Collectors.toList());
    }
    @Override
    public List<Question> getQuestionsSortedByVotes() {
        return questionRepository.findAll().stream()
                .sorted((q1, q2) -> Integer.compare(q2.getVotes().size(), q1.getVotes().size()))
                .collect(Collectors.toList());
    }
}
