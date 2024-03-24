package com.bezkoder.spring.security.postgresql.controllers;

import com.bezkoder.spring.security.postgresql.Exeception.ResourceNotFoundException;
import com.bezkoder.spring.security.postgresql.models.Tag;
import com.bezkoder.spring.security.postgresql.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/tags")
public class TagController {
    @Autowired
    private TagService tagService;


    @PostMapping("/create")
    public Tag createTag(@Valid @RequestBody Tag tag) {
        return tagService.createTag(tag);
    }


    @GetMapping("/getAll")
    public List<Tag> getAllTags() {
        return tagService.getAllTags();
    }


    @GetMapping("/{id}")
    public ResponseEntity<Tag> getTagById(@PathVariable(value = "id") Long tagId) {
        Tag tag = tagService.getTagById(tagId)
                .orElseThrow(() -> new ResourceNotFoundException("Tag", "id", tagId));
        return ResponseEntity.ok().body(tag);
    }


    @PutMapping("/{id}")
    public Tag updateTag(@PathVariable(value = "id") Long tagId, @Valid @RequestBody Tag tagDetails) {
        return tagService.updateTag(tagId, tagDetails);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTag(@PathVariable(value = "id") Long tagId) {
        tagService.deleteTag(tagId);
        return ResponseEntity.ok().build();
    }
}
