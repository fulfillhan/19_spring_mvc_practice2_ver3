package com.application.practice2Ver3.service;


import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import javax.print.attribute.standard.OrientationRequested;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.application.practice2Ver3.dao.MemberDAO;
import com.application.practice2Ver3.dto.MemberDTO;


@Service
public class MemberServiceImpl implements MemberSerivce {
	
	@Value("${file.repo.path}")
	private String fileRepositoryPath;
	
	@Autowired
	private MemberDAO memberDAO;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	

	private static Logger logger = LoggerFactory.getLogger(MemberServiceImpl.class);


	@Override
	public void createMember(MultipartFile uploadProfile, MemberDTO memberDTO) throws IllegalStateException, IOException {
		
		
		// 프로파일 객체화하기
		
		if(!uploadProfile.isEmpty()) {
			
			//기존파일 DTO 전송하기
			String originalFilename  = uploadProfile.getOriginalFilename();
			memberDTO.setProfileOriginalName(originalFilename);
			
			//UUID파일로 DTO전송하기
			UUID uuid = UUID.randomUUID();
			String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
			String uploadFileName = uuid + extension;
			memberDTO.setProfileUUID(uploadFileName);
			
			uploadProfile.transferTo(new File(fileRepositoryPath+uploadFileName));
			
		}
		
		if(memberDTO.getSmsstsYn() == null) memberDTO.setSmsstsYn("n"); // 체크된게 없다면 'n'으로 전송한다
		if(memberDTO.getEmailstsYn() == null) memberDTO.setEmailstsYn("n"); 
		
		//패스워드 암호화하기
		 String encodingPasswd = passwordEncoder.encode(memberDTO.getPasswd());
		memberDTO.setPasswd(encodingPasswd);
		
		memberDAO.createMember(memberDTO);
		
		
	}


	@Override
	public String checkValidId(String memberId) {
		String validIdCeck = "y";
		
		//만약에 입력한 아이디가 데이터베이스에 있어서 반환값이 있다면 중복되는것
		if(memberDAO.getValidateId(memberId) != null) {
			validIdCeck = "n";// 중복된다
		}
		return validIdCeck;
	}


	@Override
	public String loginMember(MemberDTO memberDTO) {
		String isvalidMember = "n";
		// id를 메개변수로 넣어서 암호화된 passwd와 활성여부를 꺼내서 비교한다.
		MemberDTO loginData = memberDAO.getloginData(memberDTO.getMemberId());
		
		if(loginData != null) {
			//패스워드 암호화 풀기
			//passwordEncoder.matches(memberDTO.getPasswd(), loginData.getPasswd());
			if(passwordEncoder.matches(memberDTO.getPasswd(), loginData.getPasswd()) && loginData.getActiveYn().equals("y")){
				isvalidMember = "y";
				}
			}
		
		return isvalidMember;
	}


	@Override
	public MemberDTO getMemberDetail(String memberId) {
		
		return memberDAO.getMemberDetail(memberId);
	}


	@Override
	public void updateMember(MemberDTO memberDTO, MultipartFile uploadProfile) throws Exception, IOException {
		if(!uploadProfile.isEmpty()) {
			//기존 파일 삭제후 진행
			new File(fileRepositoryPath+memberDTO.getProfileUUID()).delete();
			
			String orginalFileName = uploadProfile.getOriginalFilename();
			memberDTO.setProfileOriginalName(orginalFileName);
			
			String extestion = orginalFileName.substring(orginalFileName.lastIndexOf("."));
			String uploadNewFile = UUID.randomUUID()+extestion;
			memberDTO.setProfileUUID(uploadNewFile);
			
			uploadProfile.transferTo(new File(fileRepositoryPath+uploadNewFile));
			
		}
		
		if(memberDTO.getSmsstsYn() == null) memberDTO.setSmsstsYn("n");
		if(memberDTO.getEmailstsYn() == null) memberDTO.setEmailstsYn("n");
		
		memberDAO.updateMember(memberDTO);
		
	}


	@Override
	public void updateInactiveMember(String memberId) {
		
		memberDAO.updateInactiveMember(memberId);
	}

	@Override
	@Scheduled(cron = "59 59 23 * * *")
	public void updateTodayMemberCnt() {
		SimpleDateFormat spdf = new SimpleDateFormat("yyyy-MM-dd");
		String today = spdf.format(new Date());
		int memberCnt = memberDAO.updateTodayMemberCnt(today);
		logger.info("(" + today + ") 신규회원수 : " + memberCnt);

	}


	@Override
	@Scheduled(cron="59 59 23 * * *")
	public void deleteMemberScheduler() {
		 List<MemberDTO> deleteMemberList= memberDAO.getDeleteMemberList();
		for (MemberDTO memberDTO : deleteMemberList) {
			File deleteFileList = new File(fileRepositoryPath+memberDTO.getProfileUUID());
			deleteFileList.delete();
			memberDAO.deleteMember(memberDTO.getMemberId());
		}
	}

}
