package com.bezkoder.spring.security.postgresql.service;

import com.bezkoder.spring.security.postgresql.Dto.*;
import com.bezkoder.spring.security.postgresql.Exeception.AnswerNotFoundException;
import com.bezkoder.spring.security.postgresql.Exeception.ResourceNotFoundException;
import com.bezkoder.spring.security.postgresql.controllers.QuestionRequestWrapper;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
    private FavoriteRepository favoriteRepository;

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
    @Autowired
    private UserServiceImp userServiceImp;

    @Override
    @Transactional
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
    public List<Question> searchQuestions(String keyword) {
        return questionRepository.searchQuestions(keyword);
    }

    public List<QuestionDto> getAllQuestions1() {
        return questionRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private QuestionDto convertToDto(Question question) {
        QuestionDto dto = new QuestionDto();
        dto.setId(question.getId());
        dto.setTitle(question.getTitle());
        dto.setContent(question.getContent());
        dto.setCreatedAt(question.getCreatedAt());
        dto.setUpdatedAt(question.getUpdatedAt());
        dto.setViews(question.getViews());
        dto.setUserAnonymous(question.getUserAnonymous());

        // Set username (if user is not null)
        dto.setUsername(question.getUser() != null ? question.getUser().getUsername() : "Anonymous");

        // Convert and set answers
        dto.setAnswers(question.getAnswers().stream().map(this::mapAnswerToDto).collect(Collectors.toList()));


        // Set tags
        dto.setTags(question.getTags().stream()
                .map(Tag::getName) // Assuming Tag has a getName() method
                .collect(Collectors.toSet()));

        // Calculate and set vote count and answer count
        dto.setVoteCount(question.getVotes().size());
        dto.setAnswerCount(question.getAnswers().size());

        return dto;
    }


    @Transactional
    @Override
    public List<QuestionDto> findQuestionsByUserIdAndDateRange(Long userId, Date startDate, Date endDate) {
        List<Question> questions = questionRepository.findByUser_MatriculeAndCreatedAtBetween(userId, startDate, endDate);
        return questions.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }
    @Transactional
    @Override
    public List<AnswerDto> findAnswersByUserIdAndDateRange(Long userId, Date startDate, Date endDate) {
        List<Answer> answers = answerRepository.findByUser_MatriculeAndCreatedAtBetween(userId, startDate, endDate);
        return answers.stream()
                .map(this::mapAnswerToDto)
                .collect(Collectors.toList());
    }
    @Transactional
   @Override
    public List<Question> getQuestionsByTag(String tagName) {
        return questionRepository.findByTagsName(tagName);
    }


    @Override
    @Transactional

    public QuestionDto mapToDto(Question question) {
        QuestionDto dto = new QuestionDto();
        dto.setId(question.getId());
        dto.setTitle(question.getTitle());
        dto.setContent(question.getContent());
        Boolean isUserAnonymous = question.getUserAnonymous();
        if (isUserAnonymous == null || !isUserAnonymous) {
            dto.setUsername(question.getUser().getUsername());
        }
        dto.setUserAnonymous(isUserAnonymous);
        dto.setCreatedAt(question.getCreatedAt());
        dto.setUpdatedAt(question.getUpdatedAt());
        dto.setTags(question.getTags().stream().map(Tag::getName).collect(Collectors.toSet()));
        dto.setViews(question.getViews());
        //dto.setFavorites(question.getFavorites().stream().map(Favorite::isMarkedAsFavorite).collect(Collectors.toList()));
        dto.setFavorites(question.getFavorites().stream()
                .map(favorite -> {
                    Map<String, Object> favoriteMap = new HashMap<>();
                    favoriteMap.put("id", favorite.getId());
                    favoriteMap.put("markedAsFavorite", favorite.isMarkedAsFavorite());
                    return favoriteMap;
                })
                .collect(Collectors.toList()));

        int voteCount = question.getVotes().size();


        dto.setVoteCount(voteCount);

        int answerCount = question.getAnswers().size();
        dto.setAnswerCount(answerCount);
        return dto;
    }
    @Transactional
    @Override
    public Question createQuestion(QuestionRequest questionRequest, String username, MultipartFile file, List<Long> tagIds, Boolean isUserAnonymous) {
        if (questionRequest == null) {
            throw new IllegalArgumentException("QuestionRequest cannot be null");
        }

        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        List<Tag> tags = tagRepository.findAllById(tagIds);
        if (tags.size() != tagIds.size()) {
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
        userServiceImp.increaseReputation(user.getMatricule());

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
    @Transactional
    public void incrementViewCount(Long questionId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found with id: " + questionId));

        int currentViewCount = question.getViews();
        question.setViews(currentViewCount + 1);

        questionRepository.save(question);
    }


    @Override
    @Transactional
    public Optional<QuestionByIdDto> getQuestionById(Long id) {
        return questionRepository.findById(id).map(this::maptoDto);
    }
    private QuestionByIdDto maptoDto(Question question) {
        QuestionByIdDto dto = new QuestionByIdDto();
        dto.setId(question.getId());
        dto.setUserId(question.getUser().getMatricule());
        dto.setTitle(question.getTitle());
        dto.setContent(question.getContent());
        dto.setUsername(question.getUser().getUsername());
        dto.setCreatedAt(question.getCreatedAt());
        dto.setUpdatedAt(question.getUpdatedAt());
        dto.setTags(question.getTags().stream().map(Tag::getName).collect(Collectors.toSet()));
        dto.setFile(question.getFile());
        dto.setContentType(question.getContentType());
        dto.setTags(question.getTags().stream().map(Tag::getName).collect(Collectors.toSet()));
        dto.setAnswers(question.getAnswers().stream().map(this::mapAnswerToDto).collect(Collectors.toList()));
        dto.setFavorites(question.getFavorites().stream().map(this::mapFavoriteToDto).collect(Collectors.toList()));

        return dto;


    }
    @Override
    public AnswerDto mapAnswerToDto(Answer answer) {
        AnswerDto dto = new AnswerDto();
        dto.setId(answer.getId());
        dto.setContent(answer.getContent());
        dto.setUserId(answer.getUser().getMatricule());


        dto.setQuestionId(answer.getQuestion().getId());
        dto.setAccepted(answer.isAccepted());
        dto.setFile(answer.getFile());
        dto.setContentType(answer.getContentType());

        dto.setUsername(answer.getUser().getUsername());
        dto.setCreatedAt(answer.getCreatedAt().toString());
        dto.setUpdatedAt(answer.getUpdatedAt() != null ? answer.getUpdatedAt().toString() : null);
        dto.setResponses(answer.getResponses().stream().map(this::mapToAnswerResponseDto).collect(Collectors.toList()));


        dto.setVotes(answer.getVotes().stream().map(Vote::toString).collect(Collectors.toList()));
        dto.setFavorites(answer.getFavorites().stream().map(Favorite::toString).collect(Collectors.toList()));
        return dto;
    }
    @Override
    public AnswerResponseDto mapToAnswerResponseDto(AnswerResponse answerResponse) {
        AnswerResponseDto dto = new AnswerResponseDto();
        dto.setId(answerResponse.getId());
        dto.setContent(answerResponse.getContent());
        dto.setUserId(answerResponse.getUser().getMatricule());
        dto.setUsername(answerResponse.getUser().getUsername());
        dto.setCreatedAt(answerResponse.getCreatedAt().toString());
        dto.setUpdatedAt(answerResponse.getUpdatedAt() != null ? answerResponse.getUpdatedAt().toString() : null);
        dto.setVotes(answerResponse.getVotes().stream().map(Vote::toString).collect(Collectors.toList()));
        dto.setFavorites(answerResponse.getFavorites().stream().map(Favorite::toString).collect(Collectors.toList()));
        return dto;
    }

    @Transactional
    public Answer acceptAnswer(Long answerId) {
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new AnswerNotFoundException(answerId));

        // Set all answers of the same question to not accepted
        List<Answer> answers = answerRepository.findByQuestionId(answer.getQuestion().getId());
        for (Answer a : answers) {
            a.setAccepted(false);
            answerRepository.save(a);
        }

        // Mark the selected answer as accepted
        answer.setAccepted(true);
        return answerRepository.save(answer);
    }


    private FavoriteDto mapFavoriteToDto(Favorite favorite) {
        FavoriteDto dto = new FavoriteDto();
        dto.setId(favorite.getId());
        dto.setUsername(favorite.getUser().getUsername());
        return dto;
    }


   @Transactional
    @Override
    public Question updateQuestion(Long questionId, QuestionRequestWrapper questionRequestWrapper, MultipartFile file) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found"));

        QuestionRequest questionRequest = questionRequestWrapper.getQuestionRequest();

        question.setTitle(questionRequest.getTitle());
        question.setContent(questionRequest.getContent());
        question.setUpdatedAt(new Date());


       // Handle the new properties from QuestionRequestWrapper
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

        // If you want to handle the tags
        List<Long> tagIds = questionRequestWrapper.getTagIds();
        if (tagIds != null) {
            List<Tag> tags = tagRepository.findAllById(tagIds);
            if (tags.size() != tagIds.size()) {
                throw new RuntimeException("Some tags were not found");
            }
            question.getTags().clear();
            question.getTags().addAll(tags);
        }

        // If you want to handle the anonymous user flag
       Boolean isUserAnonymous = questionRequestWrapper.getUserAnonymous();

           question.setUserAnonymous(isUserAnonymous);


       return questionRepository.save(question);
    }


// Dans QuestionServiceImp.java
@Override
@Transactional
public void deleteQuestion(Long questionId) {
    Question question = questionRepository.findById(questionId)
            .orElseThrow(() -> new RuntimeException("Question not found"));

    // Supprimez tous les favoris associés à la question
    favoriteRepository.deleteByQuestionId(questionId);

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
    @Transactional
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
    @Transactional
    public Answer createAnswer(Long questionId, AnswerRequest answerRequest, String username,MultipartFile file) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        Question question = questionRepository.findById(questionId).orElseThrow(() -> new RuntimeException("Question not found"));
        Hibernate.initialize(question.getUser());
        String questionCreatorEmail = question.getUser().getEmail();
        Answer answer = new Answer();
        answer.setContent(answerRequest.getContent());
        answer.setUser(user);
        answer.setQuestion(question);


        answer.setCreatedAt(new Date());
        if (file != null) {

            String contentType = file.getContentType();

            if (
                    !contentType.equals("image/jpeg") &&
                            !contentType.equals("image/png") &&
                            !contentType.equals("image/gif") &&
                            !contentType.equals("image/bmp") &&
                            !contentType.equals("image/svg+xml") &&
                            !contentType.equals("application/pdf") &&
                            !contentType.equals("application/msword") &&
                            !contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document") &&
                            !contentType.equals("application/vnd.ms-excel") &&
                            !contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") &&
                            !contentType.equals("text/csv") &&
                            !contentType.equals("application/rtf") &&
                            !contentType.equals("text/plain")
            ) {
                throw new RuntimeException("Unsupported file type");
            }

            try {
                answer.setFile(file.getBytes());
                answer.setContentType(file.getContentType());
            } catch (IOException e) {
                throw new RuntimeException("Error reading file", e);
            }
        }

        Answer savedAnswer = answerRepository.save(answer);
       userServiceImp.increaseReputation(user.getMatricule());


        Notification notification = new Notification();
        notification.setUser(question.getUser());
        notification.setContent("Une nouvelle réponse a été ajoutée à votre question");
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now());
        notification.setQuestionId(questionId);
        notificationRepository.save(notification);
        sendNotificationEmail(questionCreatorEmail, "Une nouvelle réponse a été ajoutée à votre question");


        return savedAnswer;
    }

    @Override
    @Transactional
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
        userServiceImp.increaseReputation(user.getMatricule());


        // Création de la notification avec l'ID de la question de la réponse parente
        Notification notification = new Notification();
        notification.setUser(parentAnswer.getUser());
        notification.setContent("Une nouvelle réponse à une réponse a été ajoutée");
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now());
        notification.setQuestionId(questionId); // Utilisation de l'ID de la question associée à la notification

        notificationRepository.save(notification);
      // sendNotificationEmail(parentAnswer.getUser(), "Une nouvelle réponse a été ajoutée à votre réponse de question");

        return savedResponse;
    }
    @Transactional
    public void sendNotificationEmail(String userEmail, String content) {
        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(userEmail);
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
