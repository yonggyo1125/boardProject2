package org.koreait.models.board;

import lombok.RequiredArgsConstructor;
import org.koreait.commons.ListData;
import org.koreait.controllers.admins.BoardSearch;
import org.koreait.entities.BoardData;
import org.koreait.entities.FileInfo;
import org.koreait.models.file.FileInfoService;
import org.koreait.repositories.BoardDataRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardInfoService {

    private final BoardDataRepository boardDataRepository;
    private final FileInfoService fileInfoService;

    public BoardData get(Long seq) {

        BoardData data = boardDataRepository.findById(seq).orElseThrow(BoardDataNotFoundException::new);

        addFileInfo(data);

        return data;
    }


    public ListData<BoardData> getList(BoardDataSearch search) {

    }


    private void addFileInfo(BoardData data) {
        String gid = data.getGid();
        List<FileInfo> editorImages = fileInfoService.getListDone(gid, "editor");
        List<FileInfo> attachFiles = fileInfoService.getListDone(gid, "attach");

        data.setEditorImages(editorImages);
        data.setAttachFiles(attachFiles);
    }
}
