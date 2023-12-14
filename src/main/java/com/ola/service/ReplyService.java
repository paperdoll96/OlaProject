package com.ola.service;

import java.util.List;

import com.ola.entity.Community;
import com.ola.entity.Reply;

public interface ReplyService {
	Reply getReplyByReplyNo(Long replyNo);

	void addReply(Reply reply);

	void deleteReply(Long replyNo);

	Community getCommunityByReplyNo(Long replyNo);
	
	/* 관리자 페이지 게시글 상세보기에서 댓글 출력*/
	 List<Reply> getRepliesByCommunity(Community community);
	 
}
