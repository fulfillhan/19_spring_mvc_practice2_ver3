package com.application.practice2Ver3.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import com.application.practice2Ver3.dto.MemberDTO;

public interface MemberSerivce {

	public void createMember(MultipartFile uploadProfile, MemberDTO memberDTO) throws IllegalStateException, IOException;

	public String checkValidId(String memberId);

	public String loginMember(MemberDTO memberDTO);

	public MemberDTO getMemberDetail(String memberId);

	public void updateMember(MemberDTO memberDTO, MultipartFile uploadProfile) throws Exception, IOException;

	public void updateInactiveMember(String memberId);
	
	public void updateTodayMemberCnt();
	public void deleteMemberScheduler();

}
