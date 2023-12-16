package org.koreait.controllers.comments;

import lombok.RequiredArgsConstructor;
import org.koreait.models.comment.CommentDeleteService;
import org.koreait.models.comment.CommentSaveService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentSaveService saveService;
    private final CommentDeleteService deleteService;

    @PostMapping("/save")
    public String save(CommentForm form, Errors errors, Model model) {

        saveService.save(form, errors);

        if (errors.hasErrors()) {
            errors.getFieldErrors().stream().map(e -> e.)
        }


        // 댓글 작성 완료 시에 부모창을 새로고침 -> 새로운 목록 갱신
        model.addAttribute("script", "parent.location.reload();");
        return "common/_execute_script";
    }

    @RequestMapping("/delete/{seq}")
    public String delete(@PathVariable("seq") Long seq) {

        return "redirect:/board/view/게시글번호";
    }
}
