package com.application.practice2Ver3.controller;

import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.application.practice2Ver3.dto.MemberDTO;
import com.application.practice2Ver3.service.MemberSerivce;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
// 오류 발생 :  loginMember sql에서 memberId값이 null로 들어가서 데이터가 반환되지 못하는 상황 발생
// 왜 null로 들어갈까 했는데 
//해결 : 해당건은 ajax에서 데이터를 받는데 @RequestBody로 받은 것이 아닌, @modelAttribute로 받음


@Controller
@RequestMapping("/member")
public class MemberContorller {
	
	@Value("${file.repo.path}")
	private String fileRepositoryPath;
	
	@Autowired
	private MemberSerivce memberSerivce;
	
	@GetMapping("/mainMember")
	public String mainMember() {
		return "member/mainMember";
	}
	
	@GetMapping("/registerMember")
	public String registerMember() {
		return "member/registerMember";
	}
	
	@PostMapping("/registerMember")
	public String registerMember(@ModelAttribute MemberDTO memberDTO, @RequestParam("uploadProfile") MultipartFile uploadProfile ) throws Exception, IOException {
		memberSerivce.createMember(uploadProfile,memberDTO);
		return "member/mainMember";
	}
	
	@PostMapping("/validId")
	@ResponseBody
	public String validId(@RequestParam("memberId") String memberId) {
		return memberSerivce.checkValidId(memberId);
	}
	
	@GetMapping("/loginMember")
	public String loginMember() {
		return "member/loginMember";
	}
	
	@PostMapping("/loginMember")
	@ResponseBody
	public String loginMember(@RequestBody MemberDTO memberDTO, HttpServletRequest request) {
		
		
		if(memberSerivce.loginMember(memberDTO) != null) {
			
			HttpSession session = request.getSession();
			session.setAttribute("memberId", memberDTO.getMemberId());
		}
		return memberSerivce.loginMember(memberDTO);
	}
	
	@GetMapping("/logoutMember")
	public String logoutMember(HttpServletRequest request) {
		HttpSession session  = request.getSession();
		session.invalidate();
		
		return "redirect:mainMember";
	}
	
	@GetMapping("/updateMember")
	public String updateMember(Model model, HttpServletRequest request) {
		HttpSession session = request.getSession();
		model.addAttribute("memberDTO", memberSerivce.getMemberDetail((String)session.getAttribute("memberId")));
		return "member/updateMember";
	}
	
	@GetMapping("/thumbnails")
	@ResponseBody
	public Resource thumbnails(@RequestParam("fileName") String fileName) throws IOException {
		return new UrlResource("file : "+fileRepositoryPath+fileName);
	}
	
	@PostMapping("/updateMember")
	public String updateMember(@ModelAttribute MemberDTO memberDTO, @RequestParam("uploadProfile") MultipartFile uploadProfile) throws IOException, Exception {
		memberSerivce.updateMember(memberDTO,uploadProfile);
		return "redirect:mainMember";
	}
	
	@GetMapping("/deleteMember")
	public String deleteMember() {
		return "member/deleteMember";
	}
	
	@PostMapping("/deleteMember")
	public String deleteMember(HttpServletRequest request) {
		HttpSession session = request.getSession();
		//session.getAttribute("memberId");
		memberSerivce.updateInactiveMember((String)session.getAttribute("memberId"));
		session.invalidate();
		
		return "member/mainMember";
		
	}

}
