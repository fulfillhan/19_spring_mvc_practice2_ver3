package com.application.practice2Ver3.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.application.practice2Ver3.dto.MemberDTO;

@Mapper
public interface MemberDAO {

	public void createMember(MemberDTO memberDTO);

	public String getValidateId(String memberId);

	public MemberDTO getloginData(String memberId);

	public MemberDTO getMemberDetail(String memberId);

	public void updateMember(MemberDTO memberDTO);

	public void updateInactiveMember(String memberId);

	public int updateTodayMemberCnt(String today);

	public List<MemberDTO> getDeleteMemberList();

	public void deleteMember(String memberId);

	

}
