package org.koreait.models.comment;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.koreait.entities.BoardData;
import org.koreait.entities.CommentData;
import org.koreait.entities.QCommentData;
import org.koreait.repositories.BoardDataRepository;
import org.koreait.repositories.CommentDataRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentInfoService {

    private final CommentDataRepository commentDataRepository;
    private final BoardDataRepository boardDataRepository;

    private final EntityManager em;

    public CommentData get(Long seq) {
        CommentData comment = commentDataRepository.findById(seq).orElseThrow(CommentNotFoundException::new);

        return comment;
    }

    /**
     * 댓글 목록
     *
     * @param boardDataSeq : 게시글 번호
     * @return
     */
    public List<CommentData> getList(Long boardDataSeq) {
        QCommentData commentData = QCommentData.commentData;

        PathBuilder<CommentData> pathBuilder = new PathBuilder<>(CommentData.class, "commentData");
        List<CommentData> items = new JPAQueryFactory(em)
                .selectFrom(commentData)
                .where(commentData.boardData.seq.eq(boardDataSeq))
                .leftJoin(commentData.member)
                .fetchJoin()
                .orderBy(new OrderSpecifier(Order.ASC, pathBuilder.get("createdAt")))
                .fetch();

        return items;
    }

    /**
     * 댓글 수 업데이트
     *
     * @param seq
     */
    public void updateCommentCnt(Long seq) {
        CommentData comment = get(seq);
        BoardData boardData = comment.getBoardData();
        Long boardDataSeq = boardData.getSeq();
        boardData.setCommentCnt(commentDataRepository.getTotal(boardDataSeq));

        boardDataRepository.flush();
    }

}
