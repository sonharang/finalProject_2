package com.yedamFinal.aco.member.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.yedamFinal.aco.activity.ActivityPointVO;
import com.yedamFinal.aco.bookmark.MybookmarkVO;
import com.yedamFinal.aco.common.TagVO;
import com.yedamFinal.aco.member.MemberVO;
import com.yedamFinal.aco.myemoticon.MyemoticonVO;
import com.yedamFinal.aco.point.PointDetailJoinVO;
import com.yedamFinal.aco.questionboard.MyquestionVO;

@Service

public interface MemberService {
	public Map<String,Object>  checkDuplicateId(String id);
	public Map<String,Object>  checkDuplicateEmail(String email);
	public Map<String,Object> sendAuthNumberToPhone(String phoneNum);
	public Map<String,Object> verifyAuthNumber(String randNumber, String phoneNum);
	public Map<String,Object> joinMember(MemberVO vo, MultipartFile file);
	public Map<String,Object> loginMember(String userid, String userpw);
	public Map<String,Object> processGitLink(String userid, String tempUserGitCode);
	public Map<String,Object> findAccount(String email);
	public boolean verifyChangePasswordForm(String accessKey);
	public Map<String,Object> changePassword(String accessKey, String pwd, String pwdVerify);
	
	//회원 정보 단건조회
	public MemberVO getMemberInfo(MemberVO memberVO);
	//이모티콘 구매내역
	public List<MyemoticonVO> getMyemoList(MemberVO memberVO);
	//책갈피 목록
	public List<MybookmarkVO> getMybmList(MemberVO memberVO);
	// 책갈피 더보기 목록
	public List<MybookmarkVO> getMyBookList(MemberVO memberVO);
	//내가 작성한 질답글 목록
	public List<MyquestionVO> getMyqList(MemberVO memberVO);
	//질답글 모달
	public List<MyquestionVO> getMyQuestionList(MemberVO memberVO);
	//사이드 프로젝트 모달 리스트 --> 나중에 옮기기
	//public List<SideVo> getMySideProjectList(MemberVO memberVO);
 	//포인트 사용내역
	public List<PointDetailJoinVO> getPointList(MemberVO memberVO);
	public List<TagVO> getTagList();
	//활동점수 내역 리스트 가져오기
	public List<ActivityPointVO> getActivityList(MemberVO memberVO);
	
}
