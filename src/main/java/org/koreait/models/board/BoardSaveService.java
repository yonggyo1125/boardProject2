package org.koreait.models.board;

import lombok.RequiredArgsConstructor;
import org.koreait.controllers.boards.BoardForm;
import org.koreait.entities.BoardData;
import org.koreait.repositories.BoardDataRepository;
import org.koreait.repositories.BoardRepository;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class BoardSaveService {
    private final BoardDataRepository boardDataRepository;
    private final BoardRepository boardRepository;

    public void save(BoardForm form) {
        Long seq = form.getSeq();
        String mode = Objects.requireNonNullElse(form.getMode(), "add");



        BoardData data = null;
        if (mode.equals("update") && seq != null) {
            data = boardDataRepository.findById(seq).orElseThrow(BoardDataNotFoundException::new);
        } else {
            data = new BoardData();
            data.setGid(form.getGid()); // 그룹 ID(GID)는 최초 글 등록시 한번만 등록
        }

        data.setSubject(form.getSubject());
        data.setContent(form.getContent());
        data.setPoster(form.getPoster());

        boardDataRepository.saveAndFlush(data);
    }
}
