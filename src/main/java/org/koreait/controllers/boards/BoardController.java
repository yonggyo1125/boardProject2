package org.koreait.controllers.boards;

import lombok.RequiredArgsConstructor;
import org.koreait.commons.MemberUtil;
import org.koreait.commons.ScriptExceptionProcess;
import org.koreait.commons.Utils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardController implements ScriptExceptionProcess {
    private final Utils utils;
    private final MemberUtil memberUtil;

    @GetMapping("/write/{bId}")
    public String write(@PathVariable String bId, Model model) {

        return utils.tpl("board/write");
    }

    @GetMapping("/update/{seq}")
    public String update(@PathVariable Long seq, Model model) {
        return utils.tpl("board/update");
    }

    @PostMapping("/save")
    public String save(Model model) {

        return "redirect:/board/list/게시판ID";
    }

    @GetMapping("/view/{seq}")
    public String view(@PathVariable Long seq, Model model) {

        return utils.tpl("board/view");
    }

    @GetMapping("/delete/{seq}")
    public String delete(@PathVariable Long seq) {

        return "redirect:/board/list/게시판 ID";
    }

    private void commonProcess(String bId, String mode, Model model) {
        String pageTitle = "게시글 목록";
        if (mode.equals("write")) pageTitle = "게시글 작성";
        else if (mode.equals("update")) pageTitle = "게시글 수정";
        else if (mode.equals("view")) pageTitle = "게시글 제목";

        model.addAttribute("pageTitle", pageTitle);
    }
}
