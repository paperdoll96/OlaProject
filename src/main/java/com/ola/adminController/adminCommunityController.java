package com.ola.adminController;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.ola.entity.Community;
import com.ola.entity.Reply;
import com.ola.service.CommunityService;
import com.ola.service.ReplyService;

@Controller
public class adminCommunityController {

	@Autowired
	private CommunityService communityService;
	
	@Autowired
	private ReplyService replyService;

	@GetMapping("/admin/community")
	public String showCommunityList(Model model) {
		model.addAttribute("communities", communityService.getAllCommunities());
		return "/admin/community_list"; // 해당하는 Thymeleaf 템플릿 이름
	}
	
	/* 게시글 상세보기와 댓글 출력*/
	@GetMapping("/admin/community/detail/{communityId}")
    public String showCommunityDetail(@PathVariable("communityId") Long communityId, Model model) {
		 Community community = communityService.getCommunityById(communityId);
	        List<Reply> replies = replyService.getRepliesByCommunity(community);
	        model.addAttribute("community", community);
	        model.addAttribute("replies", replies);
        
        if (community != null) {
            model.addAttribute("community", community);
            return "/admin/communityDetail";
        } else {
            return "redirect:/admin/community";
        }
    }
	
	
}