package org.koreait.models.comment;

import lombok.RequiredArgsConstructor;
import org.koreait.controllers.comments.CommentForm;
import org.koreait.controllers.comments.CommentFormValidator;
import org.koreait.entities.CommentData;
import org.koreait.repositories.BoardDataRepository;
import org.koreait.repositories.CommentDataRepository;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

@Service
@RequiredArgsConstructor
public class CommentSaveService {
    private final CommentDataRepository commentDataRepository;
    private final BoardDataRepository boardDataRepository;
    private final CommentFormValidator validator;

    public void save(CommentForm form, Errors errors) {
        validator.validate(form, errors);
        if (errors.hasErrors()) {
            return;
        }

        CommentData commentData = null;
        Long seq = form.getSeq();
        if (seq == null) { // 추가 
            commentData = new CommentData();
        } else { // 수정
            commentData = commentDataRepository.findById(seq).orElseThrow(CommentNotFoundException::new);
        }
    }

    public void save(CommentData comment) {

        commentDataRepository.saveAndFlush(comment);
    }
}
