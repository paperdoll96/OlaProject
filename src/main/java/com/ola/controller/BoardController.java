package com.ola.controller;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ola.entity.Community;
import com.ola.entity.TradeBoard;
import com.ola.repository.CommunityRepository;
import com.ola.repository.TradeBoardRepository;
import com.ola.security.SecurityUser;
import com.ola.service.BoardService;

@Controller
public class BoardController {

	@Autowired
	private BoardService boardService;
	@Autowired
	private TradeBoardRepository boardRepo;
	@Autowired
	private CommunityRepository comRepo;

	@RequestMapping("/tradeBoardList")
	public String TradeBoardList(Model model, Authentication authentication,
			@PageableDefault(size = 10, sort = "registrationDate", direction = Direction.DESC) Pageable pageable) {
		if (authentication == null || !authentication.isAuthenticated()) {
			// 사용자가 로그인하지 않았거나 인증되지 않았을 경우, 로그인 페이지로 리다이렉트
			return "redirect:/system/login";
		}
		List<TradeBoard> adminWrite = boardRepo.findByAdminWrite();
		model.addAttribute("adminWrite", adminWrite);

		Page<TradeBoard> memberWrite = boardRepo.findByMemberWrite(pageable);

		model.addAttribute("memberWrite", memberWrite);
		model.addAttribute("memberCurrentPage", memberWrite.getNumber() + 1);
		model.addAttribute("memberTotalPages", memberWrite.getTotalPages());

		return "board/tradeBoardList";
	}

	@GetMapping("/communityBoardList")
	public String CommunityBoardList(Model model, Authentication authentication,
			@PageableDefault(size = 10, sort = "regDate", direction = Direction.DESC) Pageable pageable) {
		if (authentication == null || !authentication.isAuthenticated()) {
			// 사용자가 로그인하지 않았거나 인증되지 않았을 경우, 로그인 페이지로 리다이렉트
			return "redirect:/system/login";
		}
		List<Community> adminWrite = comRepo.findByAdminWrite();
		model.addAttribute("adminWrite", adminWrite);

		Page<Community> memberWrite = comRepo.findByMemberWrite(pageable);

		model.addAttribute("memberWrite", memberWrite);
		model.addAttribute("memberCurrentPage", memberWrite.getNumber() + 1);
		model.addAttribute("memberTotalPages", memberWrite.getTotalPages());

		return "board/communityBoardList";
	}

	@GetMapping("/getTradeBoard")
	public String getTradeBoardView(@RequestParam Long tradeBoardNo, Model model) {
		TradeBoard tradeBoard = boardRepo.findById(tradeBoardNo).orElse(null);

		if (tradeBoard != null) {
			model.addAttribute("tradeBoard", tradeBoard);
			return "board/getTradeBoard";
		} else {
			return "errorPage";
		}

	}

	@GetMapping("/getCommuBoard")
	public String getCommunity(@RequestParam Long communityNo, Model model) {
		// 조회수 증가를 위해 서비스 계층의 메소드를 호출
		Community community = boardService.getCommunityWithRepliesByNo(communityNo);

		if (community != null) {
			model.addAttribute("community", community);
			return "board/getCommuBoard";
		} else {
			return "errorPage";
		}
	}

	@GetMapping("/board/communityInsert")
	public String communityInsertView() {
		return "board/communityInsert";
	}

	@GetMapping("/board/tradeInsert")
	public void tradeInsertView() {

	}

	@GetMapping("/editBoard/{communityNo}")
	public String editCommunityForm(@PathVariable Long communityNo, Model model) {
		Community community = boardService.getCommunityById(communityNo);

		if (community != null) {
			model.addAttribute("community", community);
			return "/board/editCommunityForm";
		} else {
			return "errorPage";
		}
	}

	@PostMapping("/editCommunity/save")
	public String saveEditedCommunity(@RequestParam Long communityNo, @RequestParam String newContent,
			@AuthenticationPrincipal UserDetails userDetails) {
		Community community = boardService.getCommunityById(communityNo);

		// 로그인한 사용자가 게시글 작성자인지 확인
		if (userDetails != null && community != null
				&& userDetails.getUsername().equals(community.getMember().getMemberId())) {
			// 여기에서 수정 작업 수행
			community.setTitle(newContent);
			community.setContent(newContent);
			boardService.saveCommunity(community);
			return "redirect:/getCommuBoard?communityNo=" + communityNo;
		} else {
			// 사용자가 게시글을 수정할 권한이 없는 경우 처리
			// 예를 들어 에러 페이지로 리다이렉션하거나 에러 메시지를 표시할 수 있습니다.
			return "redirect:/error";
		}
	}

	@PostMapping("/communityInsert")
	public String communityInsertAction(@ModelAttribute Community board,
			@AuthenticationPrincipal SecurityUser principal) {

		board.setRegDate(new Date());

		board.setMember(principal.getMember());

		boardService.insertBoard(board);

		return "redirect:communityBoardList";
	}

	@PostMapping("/board/tradeInsert")
	public String tradeInsertAction(@ModelAttribute TradeBoard board, @AuthenticationPrincipal SecurityUser principal) {

		board.setRegistrationDate(new Date());

		board.setMember(principal.getMember());

		boardService.insertBoard(board);

		return "redirect:/tradeBoardList";
	}

	// 게시글 삭제
	@PostMapping("/deleteBoard")
	public String deleteBoard(@RequestParam Long communityNo, @AuthenticationPrincipal UserDetails userDetails) {
		Community community = boardService.getCommunityById(communityNo);

		// 로그인한 사용자가 게시글 작성자인지 확인
		if (userDetails != null && community != null
				&& userDetails.getUsername().equals(community.getMember().getMemberId())) {
			boardService.deleteCommunity(communityNo);
			return "redirect:/communityBoardList";
		} else {
			// 사용자가 게시글을 삭제할 권한이 없는 경우 처리
			// 예를 들어 에러 페이지로 리다이렉션하거나 에러 메시지를 표시할 수 있습니다.
			return "redirect:/error";
		}
	}

	@PostMapping("/likeCommunity")
	public String likeCommunity(@RequestParam Long communityNo, Authentication authentication) {
		String memberId = authentication.getName();
		boardService.likeCommunity(communityNo, memberId);

		return "redirect:/getCommuBoard?communityNo=" + communityNo;
	}

	@PostMapping("/unlikeCommunity")
	public String unlikeCommunity(@RequestParam Long communityNo, Authentication authentication) {
		String memberId = authentication.getName();
		boardService.unlikeCommunity(communityNo, memberId);

		return "redirect:/getCommuBoard?communityNo=" + communityNo;
	}

}
