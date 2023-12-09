package org.koreait.models.board;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.koreait.commons.ListData;
import org.koreait.commons.MemberUtil;
import org.koreait.commons.Pagination;
import org.koreait.commons.Utils;
import org.koreait.controllers.boards.BoardDataSearch;
import org.koreait.controllers.boards.BoardForm;
import org.koreait.entities.BoardData;
import org.koreait.entities.FileInfo;
import org.koreait.entities.Member;
import org.koreait.entities.QBoardData;
import org.koreait.models.file.FileInfoService;
import org.koreait.repositories.BoardDataRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class BoardInfoService {

    private final BoardDataRepository boardDataRepository;
    private final FileInfoService fileInfoService;
    private final HttpServletRequest request;
    private final EntityManager em;
    private final MemberUtil memberUtill;
    private final HttpSession session;

    public BoardData get(Long seq) {

        BoardData data = boardDataRepository.findById(seq).orElseThrow(BoardDataNotFoundException::new);

        addFileInfo(data);

        return data;
    }

    public BoardForm getForm(Long seq) {
        BoardData data = get(seq);
        BoardForm form = new ModelMapper().map(data, BoardForm.class);
        form.setMode("update");
        form.setBId(data.getBoard().getBId());

        return form;
    }

    public ListData<BoardData> getList(BoardDataSearch search) {
        QBoardData boardData = QBoardData.boardData;
        int page = Utils.getNumber(search.getPage(), 1);
        int limit = Utils.getNumber(search.getLimit(), 20);
        int offset = (page - 1) * limit;
        
        String bId = search.getBId(); // 게시판 아이디
        String sopt  = Objects.requireNonNullElse(search.getSopt(), "subject_content"); // 검색 옵션
        String skey = search.getSkey(); // 검색 키워드

        BooleanBuilder andBuilder = new BooleanBuilder();
        andBuilder.and(boardData.board.bId.eq(bId));

        // 키워드 검색 처리
        if (StringUtils.hasText(skey)) {
            skey = skey.trim();

            if (skey.equals("subject")) { // 제목 검색
                andBuilder.and(boardData.subject.contains(skey));

            } else if (skey.equals("content")) { // 내용 검색
                andBuilder.and(boardData.content.contains(skey));

            } else if (skey.equals("subject_content")) { // 제목 + 내용 검색
                BooleanBuilder orBuilder = new BooleanBuilder();
                orBuilder.or(boardData.subject.contains(skey))
                        .or(boardData.content.contains(skey));

                andBuilder.and(orBuilder);
            } else if (skey.equals("poster")) { // 작성자 + 아이디
                BooleanBuilder orBuilder = new BooleanBuilder();
                orBuilder.or(boardData.poster.contains(skey))
                        .or(boardData.member.email.contains(skey))
                        .or(boardData.member.userNm.contains(skey));

                andBuilder.and(orBuilder);

            }
        }

        PathBuilder pathBuilder = new PathBuilder(BoardData.class, "boardData");
        List<BoardData> items = new JPAQueryFactory(em)
                .selectFrom(boardData)
                .where(andBuilder)
                .offset(offset)
                .limit(limit)
                .fetchJoin()
                .orderBy(
                        new OrderSpecifier(Order.valueOf("DESC"),
                                pathBuilder.get("createdAt")))
                .fetch();

        int total = (int)boardDataRepository.count(andBuilder);

        Pagination pagination = new Pagination(page, total, 10, limit, request);

        // 파일 정보 추가
        items.stream().forEach(this::addFileInfo);

        ListData<BoardData> data = new ListData<>();
        data.setContent(items);
        data.setPagination(pagination);

        return data;
    }


    private void addFileInfo(BoardData data) {
        String gid = data.getGid();
        List<FileInfo> editorImages = fileInfoService.getListDone(gid, "editor");
        List<FileInfo> attachFiles = fileInfoService.getListDone(gid, "attach");

        data.setEditorImages(editorImages);
        data.setAttachFiles(attachFiles);
    }

    public boolean isMine(Long seq) {
        if (memberUtill.isAdmin()) { // 관리자는 수정, 삭제 모두 가능
            return true;
        }

        BoardData data = get(seq);
        if (data.getMember() != null) {
         // 회원 등록 게시물이만 직접 작성한 게시글인 경우
            Member boardMember = data.getMember();
            Member member = memberUtill.getMember();

            return memberUtill.isLogin() && boardMember.getUserNo() == member.getUserNo();
        } else { // 비회원 게시글
            // 세션에 chk_게시글번호 항목이 있으면 비번 검증 완료
            String key = "chk_" + seq;
            if (session.getAttribute(key) == null) { // 비회원 비밀번호 검증 X -> 검증 화면으로 이동
                throw new RequiredPasswordCheckException();
            } else { // 비회원 게시글 검증 성공시
                return true;
            }
        }


        return false;
    }
}
