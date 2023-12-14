package com.ola.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.ola.entity.Community;
import com.ola.entity.Reply;
import com.ola.repository.CommunityRepository;
import com.ola.repository.ReplyRepository;

@Service
public class CommunityServiceImpl implements CommunityService {

	@Autowired
    private CommunityRepository communityRepository;
	@Autowired
	private ReplyRepository replyRepository;

	/* 커뮤니티 게시글 목록 최신등록 날짜 순으로 출력*/
    @Override
    public List<Community> getAllCommunities() {
    	return communityRepository.findAll(Sort.by(Sort.Direction.DESC, "regDate"));
    }

    
    /* 게시글 상세보기*/
    @Override
    public Community getCommunityById(Long communityId) {
        return communityRepository.findById(communityId).orElse(null);
    }




    // 기타 필요한 메소드 구현
}
