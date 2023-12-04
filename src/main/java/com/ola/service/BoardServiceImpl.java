package com.ola.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.ola.entity.Community;
import com.ola.entity.TradeBoard;
import com.ola.repository.CommunityRepository;
import com.ola.repository.TradeBoardRepository;

import jakarta.transaction.Transactional;

@Service
public class BoardServiceImpl implements BoardService {

	@Autowired
	private TradeBoardRepository boardRepo;
	@Autowired
	private CommunityRepository comRepo;

	@Override
	@Transactional
	public void insertBoard(TradeBoard board) {

		boardRepo.save(board);
	}

	@Override
	public void insertBoard(Community commu) {
		comRepo.save(commu);
	}

	@Override
	public void updateBoard(TradeBoard board) {
		TradeBoard newBoard = boardRepo.findById(board.getTradeBoardNo()).get();

		newBoard.setTitle(board.getTitle());
		newBoard.setContent(board.getContent());
		newBoard.setMember(board.getMember());
		newBoard.setTradeType(board.getTradeType());
		newBoard.setRegistrationDate(new Date());

		boardRepo.save(newBoard);
	}

	@Override
	public void updateBoard(Community commu) {
		Community newBoard = comRepo.findById(commu.getCommunityNo()).get();

		newBoard.setTitle(commu.getTitle());
		newBoard.setContent(commu.getContent());
		newBoard.setViewCount(commu.getViewCount());
		newBoard.setCommentCount(commu.getCommentCount());
		newBoard.setLikeCount(commu.getLikeCount());
		newBoard.setRegDate(new Date());

		comRepo.save(newBoard);
	}

	@Override
	public void deleteBoard(TradeBoard board) {

		boardRepo.deleteById(board.getTradeBoardNo());
	}

	@Override
	public void deleteBoard(Community commu) {

		comRepo.deleteById(commu.getCommunityNo());
	}

	@Override
	public TradeBoard getTradeBoardById(Long tradeBoardNo) {
		return boardRepo.findById(tradeBoardNo).orElse(null);
	}

	@Override
	public Community getCommunityByNo(Long communityNo) {
		return comRepo.findById(communityNo).orElse(null);
	}

	@Override
	public Page<TradeBoard> tradeBoardList(Pageable pageable) {

		return boardRepo.findAll(pageable);
	}

	@Override
	public Page<Community> communityBoardList(Pageable pageable) {

		return comRepo.findAll(pageable);
	}

//	@Override
//	public Page<TradeBoard> getBoardList(Pageable pageable, Search search) {
//		BooleanBuilder builder = new BooleanBuilder();
//
//		QBoard qboard = QBoard.board;
//
//		if (search.getSearchCondition().equals("TITLE")) {
//			builder.and(qboard.title.like("%" + search.getSearchKeyword() + "%"));
//		} else if (search.getSearchCondition().equals("CONTENT")) {
//			builder.and(qboard.content.like("%" + search.getSearchKeyword() + "%"));
//		}
//
//		return boardRepo.findAll(builder, pageable);
//	}
}
