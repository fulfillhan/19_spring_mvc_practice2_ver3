package com.application.practice2Ver3.dto;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

@Data
public class MemberDTO {
	

	private String memberId;
	private String passwd;
	private String profileOriginalName;
	private String profileUUID;
	private String memberNm;
	private String sex;
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date birthAt;
	private String hp;
	private String smsstsYn;
	private String email;
	private String emailstsYn;
	private String zipcode;
	private String roadAddress;
	private String jibunAddress;
	private String namujiAddress;
	private String etc;
	private String activeYn;
	private Date inactiveAt;
	private Date joinAt;
	

}
