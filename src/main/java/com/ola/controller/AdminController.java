package com.ola.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ola.entity.Community;
import com.ola.entity.Product;
import com.ola.entity.TradeBoard;
import com.ola.repository.CommunityRepository;
import com.ola.repository.ProductRepository;
import com.ola.repository.TradeBoardRepository;
import com.ola.security.SecurityUser;
import com.ola.service.BoardService;

@Controller
public class AdminController {

	@Autowired
	private CommunityRepository communityRepo;

	@Autowired
	private TradeBoardRepository tradeRepo;

	@Autowired
	private BoardService boardService;
	
	@Autowired
	private ProductRepository prodRepo;

	@GetMapping("/adminMain2")
	public String adminMain2() {
		return "adminMain2"; // 이 부분은 실제 리턴하는 뷰의 이름입니다.
	}

	@GetMapping("/adminCommunityBoardList")
	public String adminCommuView(Model model,
			@RequestParam(name = "search", required = false) String search,
			@RequestParam(name = "searchType", defaultValue = "title") String searchType,
			@PageableDefault(size = 10, sort = "regDate", direction = Direction.DESC) Pageable pageable) {
		// admin이 작성한 게시글 가져오기
		List<Community> adminWrite = communityRepo.findByAdminWrite();
		model.addAttribute("adminWrite", adminWrite);
		
		Page<Community> memberWrite = null;
		if (search != null && !search.isEmpty()) {
			if ("author".equals(searchType)) {
				memberWrite = boardService.getBoardByAuthor(search, pageable);
			} else {
				memberWrite = boardService.getBoardByTitle(search, pageable);
			}
		} else {
			memberWrite = communityRepo.findByMemberWrite(pageable);
		}
		
		model.addAttribute("memberWrite", memberWrite);
		model.addAttribute("memberCurrentPage", memberWrite.getNumber() + 1);
		model.addAttribute("memberTotalPages", memberWrite.getTotalPages());
		model.addAttribute("search", search); // 검색어를 모델에 추가

		return "admin/adminCommunityBoardList"; // View 반환
	}
	@GetMapping("/adminTradeBoardList")
	public String adminTradeView(Model model,
			@RequestParam(name = "search", required = false) String search,
			@RequestParam(name = "searchType", defaultValue = "title") String searchType,
			@PageableDefault(size = 10, sort = "registrationDate", direction = Direction.DESC) Pageable pageable) {
		// admin이 작성한 게시글 가져오기
		List<TradeBoard> adminWrite = tradeRepo.findByAdminWrite();
		model.addAttribute("adminWrite", adminWrite);
		
		Page<TradeBoard> memberWrite = null;
		if (search != null && !search.isEmpty()) {
			if ("author".equals(searchType)) {
				memberWrite = boardService.getTradeBoardByAuthor(search, pageable);
			} else {
				memberWrite = boardService.getTradeBoardByTitle(search, pageable);
			}
		} else {
			memberWrite = tradeRepo.findByMemberWrite(pageable);
		}
		
		model.addAttribute("memberWrite", memberWrite);
		model.addAttribute("memberCurrentPage", memberWrite.getNumber() + 1);
		model.addAttribute("memberTotalPages", memberWrite.getTotalPages());
		model.addAttribute("search", search); // 검색어를 모델에 추가
		
		return "admin/adminTradeBoardList"; // View 반환
	}


	@GetMapping("/adminGetCommuBoard")
	public String getCommuBoard(@RequestParam Long communityNo, Model model) {
		Community community = communityRepo.findById(communityNo).orElse(null); // communityNo에 해당하는 Community 객체를 조회

		if (community != null) {
			model.addAttribute("community", community);
			return "admin/adminCommuBoard"; // 게시글 상세보기 페이지의 뷰 이름
		} else {
			return "errorPage"; // 에러 페이지의 뷰 이름.
		}
	}
	
	@GetMapping("/adminGetTradeBoard")
	public String getTradeBoard(@RequestParam Long tradeBoardNo, Model model) {
		TradeBoard tradeBoard = tradeRepo.findById(tradeBoardNo).orElse(null); // communityNo에 해당하는 Community 객체를 조회
		
		if (tradeBoard != null) {
			model.addAttribute("tradeBoard", tradeBoard);
			return "admin/adminTradeBoard"; // 게시글 상세보기 페이지의 뷰 이름
		} else {
			return "errorPage"; // 에러 페이지의 뷰 이름.
		}
	}

	@GetMapping("/deleteAdminCommuBoard")
	public String deleteAdminCommuBoard(@RequestParam Long communityNo) {
		communityRepo.deleteById(communityNo); // 게시글 삭제

		return "redirect:adminCommunityBoardList"; // 삭제 후 목록 페이지로 리디렉션
	}
	@GetMapping("/deleteAdminTradeBoard")
	public String deleteAdminTradeBoard(@RequestParam Long tradeBoardNo) {
		tradeRepo.deleteById(tradeBoardNo); // 게시글 삭제
		
		return "redirect:adminTradeBoardList"; // 삭제 후 목록 페이지로 리디렉션
	}

	@GetMapping("/adminRegisterCommu")
	public String registerCommuView() {
		return "admin/adminRegisterCommu";
	}

	@PostMapping("/adminRegisterCommu")
	public String registerCommuAction(Community board, @AuthenticationPrincipal SecurityUser principal) {
		board.setMember(principal.getMember());
		board.setRegDate(new Date());
		boardService.insertBoard(board);
		
		return "redirect:adminCommunityBoardList";
	}
	
	@GetMapping("/adminRegisterTrade")
	public String registerTradeView() {
		return "admin/adminRegisterTrade";
	}
	
	@PostMapping("/adminRegisterTrade")
	public String registerTradeAction(TradeBoard board, @AuthenticationPrincipal SecurityUser principal) {
		board.setMember(principal.getMember());
		board.setRegistrationDate(new Date());
		boardService.insertTradeBoard(board);
		
		return "redirect:adminCommunityBoardList";
	}
	
	@GetMapping("/admin/adminAll")
	public String showAllPage(Model model) {
	    List<Product> prodList = prodRepo.findAll();
	    List<String> category = new ArrayList<>();

	    // 카테고리 번호에 따른 문자열 매핑
	    Map<Integer, String> categoryMap = new HashMap<>();
	    categoryMap.put(1, "top");
	    categoryMap.put(2, "bottom");
	    categoryMap.put(3, "shoes");
	    categoryMap.put(4, "etc");
	    categoryMap.put(5, "sales");

	    // 모든 상품의 카테고리 값을 문자열로 변환하여 리스트에 저장
	    for (Product product : prodList) {
	        int categoryNumber = product.getProdCategory(); // getCategory()는 카테고리 번호를 반환
	        String categoryString = categoryMap.getOrDefault(categoryNumber, "unknown"); // 매핑된 문자열 얻기
	        category.add(categoryString);
	    }

	    model.addAttribute("prodList", prodList);
	    model.addAttribute("category", category);
	    return "admin/adminAll";
	}
	
	@GetMapping("/admin/adminTop")
	public String showAdminTopPage(Model model) {
		List<Product> top = prodRepo.findByProdCategory(1);

	    model.addAttribute("top", top);
	    return "admin/adminTop";
	}

	@GetMapping("/admin/adminBottom")
	public String showAdminBottomPage(Model model) {
		List<Product> bottom = prodRepo.findByProdCategory(2);

	    model.addAttribute("bottom", bottom);
		return "admin/adminBottom";
	}

	@GetMapping("/admin/adminShoes")
	public String showAdminShoesPage(Model model) {
		List<Product> shoes= prodRepo.findByProdCategory(3);

	    model.addAttribute("shoes", shoes);
		return "admin/adminShoes";
	}

	@GetMapping("/admin/adminEtc")
	public String showAdminEtcPage(Model model) {
		List<Product> etc= prodRepo.findByProdCategory(4);

	    model.addAttribute("etc", etc);
		return "admin/adminEtc";
	}

	@GetMapping("/admin/adminSales")
	public String showAdminSalesPage(Model model) {
		List<Product> sales= prodRepo.findByProdCategory(5);

	    model.addAttribute("sales", sales);
		return "admin/adminSales";
	}

	@GetMapping("/admin/adminDetails")
	public String showAdminItemDetails(@RequestParam Long productNo, Model model) {
	    Product product = prodRepo.findById(productNo).orElse(null);
	    if (product != null) {
	        String categoryName = AdminconvertCategoryToName(product.getProdCategory()); // Assuming getCategory() returns the category number
	        model.addAttribute("product", product);
	        model.addAttribute("category", categoryName); // Add the converted category name to the model
	    }
	    return "admin/adminDetail";
	}

	private String AdminconvertCategoryToName(int category) {
	    switch (category) {
	        case 1:
	            return "top";
	        case 2:
	            return "bottom";
	        case 3:
	            return "shoes";
	        case 4:
	            return "etc";
	        case 5:
	            return "sales";
	        default:
	            return "unknown"; // Default case if category does not match
	    }
	}
}
