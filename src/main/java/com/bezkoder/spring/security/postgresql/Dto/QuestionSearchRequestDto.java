    package com.bezkoder.spring.security.postgresql.Dto;

    import lombok.Getter;
    import lombok.Setter;

    import java.util.List;
    @Getter @Setter
    public class QuestionSearchRequestDto extends SearchList {

        private String title;
        private String content;
        private Long userId;
        private List<String> tags;
        private Boolean userAnonymous;
    }
