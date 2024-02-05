package com.yedamFinal.aco.member.serviceImpl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.yedamFinal.aco.bookmark.MybookmarkVO;
import com.yedamFinal.aco.common.NaverMailSender;
import com.yedamFinal.aco.common.RandomString;
import com.yedamFinal.aco.common.TagVO;
import com.yedamFinal.aco.common.serviceImpl.FileServiceImpl;
import com.yedamFinal.aco.common.serviceImpl.GitHubServiceImpl;
import com.yedamFinal.aco.member.FindAccountEmailLinkVO;
import com.yedamFinal.aco.member.MemberVO;
import com.yedamFinal.aco.member.UserDetailVO;
import com.yedamFinal.aco.member.mapper.MemberMapper;
import com.yedamFinal.aco.member.service.MemberService;
import com.yedamFinal.aco.myemoticon.MyemoticonVO;
import com.yedamFinal.aco.point.PointDetailJoinVO;
import com.yedamFinal.aco.questionboard.MyquestionVO;

import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;


@Service
public class MemberServiceImpl implements MemberService, UserDetailsService {
	private int test = 1;
	
	@Autowired
	private MemberMapper memberMapper;
	
	@Value("${cool.sms.key}")
	private String coolSmsApiKey;
	
	@Value("${cool.sms.secret.key}")
	private String coolSmsApiSecretKey;
	
	@Value("${cool.sms.from.number}")
	private String fromNumber;
	//
    private DefaultMessageService messageService;
    
    @Autowired
    private PasswordEncoder bCryptPasswordEncoder;
    
	@Autowired
	private FileServiceImpl fileService;
	
	@Autowired
	private GitHubServiceImpl githubService;
	
	@Autowired
	private NaverMailSender mailSender;
    
    @PostConstruct
    public void init() {
        // 반드시 계정 내 등록된 유효한 API 키, API Secret Key를 입력해주셔야 합니다!
        this.messageService = NurigoApp.INSTANCE.initialize(coolSmsApiKey, coolSmsApiSecretKey, "https://api.coolsms.co.kr");
    }
    
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// TODO Auto-generated method stub
		MemberVO vo = memberMapper.selectLogin(username);
		if(vo == null) {
			throw new UsernameNotFoundException("no name");
		}
		
		return new UserDetailVO(vo);
	}

	@Override
	public Map<String,Object> checkDuplicateId(String id) {
		// TODO Auto-generated method stub
		MemberVO vo = memberMapper.selectCheckDuplicateId(id);
		Map<String,Object> retByMemberVO = new HashMap<String, Object>();
		retByMemberVO.put("result", vo != null ? true : false);
		return retByMemberVO;
	}

	@Override
	public Map<String, Object> checkDuplicateEmail(String email) {
		MemberVO vo = memberMapper.selectCheckDuplicateEmail(email);
		Map<String,Object> retByMemberVO = new HashMap<String, Object>();
		retByMemberVO.put("result", vo != null ? true : false);
		return retByMemberVO;
	}

	@Override
	public Map<String, Object> sendAuthNumberToPhone(String phoneNum) {
		// TODO Auto-generated method stub
		int randNumber = (int)(Math.random() * 8999) + 1000;
		Map<String,Object> ret = new HashMap<String,Object>();
		TestPhoneMessage m = new TestPhoneMessage();
		
		String existAuth = memberMapper.selectAuthNumber(phoneNum);
		if(existAuth != null && !existAuth.isEmpty()) {
			m.setStatusCode("9999");
			ret.put("result", m);
		}
		else {
			if(memberMapper.insertAuthNumber(String.valueOf(randNumber), phoneNum) <= 0) {
				m.setStatusCode("10000");
				ret.put("result", m);
			}
			
			// 나중에 시연시에는 밑에걸로 바꾸기.
			if(test == 0) {
				Message message = new Message();
				message.setFrom(fromNumber);
	        	message.setTo(phoneNum);
	        	message.setText("[AskCode] 인증번호 "+randNumber+" 를 입력하세요.");
	        
	        	SingleMessageSentResponse response = this.messageService.sendOne(new SingleMessageSendingRequest(message));        
	        	ret.put("result", response);
			}
			else {
				m.setAuthCode(randNumber);
				m.setStatusCode("9876");
				ret.put("result", m);
			}
		}
		        
		return ret;
	}
	@Override
	public Map<String, Object> verifyAuthNumber(String randNumber, String phoneNum) {
		// TODO Auto-generated method stub
		String result = memberMapper.selectVerifyAuthNumber(randNumber, phoneNum);
		Map<String,Object> ret = new HashMap<String,Object>();
		if(result == null || result.isEmpty()) {
			ret.put("result", "400");
		}
		else {
			ret.put("result", "200");
			memberMapper.deleteAuthNumber(phoneNum);
		}
		return ret;
	}
	@Override
	public List<TagVO> getTagList() {
		// TODO Auto-generated method stub
		return memberMapper.selectTagList();
	}
	@Override
	public Map<String, Object> joinMember(MemberVO vo, MultipartFile file) {
		Map<String,Object> ret = new HashMap<String,Object>();
		if(file != null) {
			String profileSaveName = fileService.profileUpload(file);
			if(profileSaveName == null || profileSaveName.isEmpty()) {
				ret.put("result", "500");
				return ret;
			}
			
			vo.setProfileImage(profileSaveName);
		}
		
		vo.setPermission("ROLE_USER");
		vo.setAccumActivityPoint(0);
		vo.setAvailableActivityPoint(0);
		vo.setAcoMoney(0);
		vo.setMemberLevel("K001");
		
		vo.setPassword(bCryptPasswordEncoder.encode(vo.getPassword()));
		
		int insertId = memberMapper.insertMember(vo);
		if(insertId <= 0) {
			ret.put("result", "500");
		}
		else {
			ret.put("result", "200");
			ret.put("vo", vo);
		}
		return ret;
	}
	@Override
	public Map<String,Object> loginMember(String userid, String userpw) {
		// TODO Auto-generated method stub
		MemberVO vo = memberMapper.selectLogin(userid);
		Map<String,Object> ret = new HashMap<String,Object>();
		ret.put("result", "404");
		if(vo != null) {
			if(bCryptPasswordEncoder.matches(userpw, vo.getPassword())) {
				ret.put("result","200");
			}
		}
		
		return ret;
	}

	@Override
	public Map<String, Object> processGitLink(String userid, String tempUserGitCode) {
		// TODO Auto-generated method stub
		Map<String,Object> ret = new HashMap<String, Object>();
		Map<String,String> map = null;
		ret.put("result", "400");
		
		MemberVO vo = memberMapper.selectLogin(userid);
		if(vo != null) {
			map = githubService.getAccessTokenByGitLink(tempUserGitCode);
			String tokenValue = map.get("access_token");
			if(memberMapper.updateMemberGitToken(tokenValue, userid) > 0) {
				ret.put("result", "200");
			}
		}
		return ret;
	}
	
	//회원 단건조회
	@Override
	public MemberVO getMemberInfo(MemberVO memberVO) {
		return memberMapper.selectMemberInfo(memberVO);
	}
	//이모티콘 구매내역
	@Override
	public List<MyemoticonVO> getMyemoList(MemberVO memberVO){
		return memberMapper.selectMyemoticonList(memberVO);
	}
	//책갈피 목록
	@Override
	public List<MybookmarkVO> getMybmList(MemberVO memberVO) {
		return memberMapper.selectMybmList(memberVO);
	}
	//내가 작성한 질문글 목록
	@Override
	public List<MyquestionVO> getMyqList(MemberVO memberVO) {
		return memberMapper.selectMyqList(memberVO);
	}
	@Override
	public List<MybookmarkVO> getMyBookList(MemberVO memberVO){
		return memberMapper.selectMyBookList(memberVO);
	}

	@Override
	public List<PointDetailJoinVO> getPointList(MemberVO memberVO) {
		return memberMapper.selectPointDetailList(memberVO);
	}

	@Override
	public List<MyquestionVO> getMyQuestionList(MemberVO memberVO) {
		return memberMapper.selectQuestionList(memberVO);
	}
	

	@Override
	public Map<String, Object> findAccount(String email) {
		Map<String,Object> ret = new HashMap<String,Object>();
		ret.put("result", "200");
		
		MemberVO vo = memberMapper.selectCheckDuplicateEmail(email);
		if(vo == null) {
			ret.put("result", "400");
			return ret;
		}
		
		Date curDate = new Date();
		String dateStr = String.valueOf(curDate.getTime());
		String randomAccessKey = RandomString.generateRandomString(40) + dateStr;
		
		FindAccountEmailLinkVO insertVO = new FindAccountEmailLinkVO();
		insertVO.setAccessKey(randomAccessKey);
		insertVO.setExpireDate(new Date(curDate.getTime() + (10 * 60 * 1000)));
		insertVO.setMemberId(vo.getId());
		
		if(memberMapper.insertFindAccountEmailLink(insertVO) <= 0) {
			ret.put("result", "500");
			return ret;
		}
		
		
		String emailTitle = "[AskCode] 계정찾기 이메일 정보입니다.";
		StringBuilder emailBodyBuilder = new StringBuilder();
		emailBodyBuilder.append("<div>");
		emailBodyBuilder.append("<h1 style='color:#3e6ce3; margin-bottom:30px;'>AskCode</h1>");
		emailBodyBuilder.append("<h1>계정찾기</h1>");
		emailBodyBuilder.append("<p style='margin-top:30px;'>아이디 : <b>" + vo.getId() + "</b></p>");
		emailBodyBuilder.append("<p>비밀번호 변경을 원하시면 아래 링크를 통해 변경하실 수 있습니다.</p>");
		emailBodyBuilder.append("<p>(비밀번호 변경 링크의 유효시간은 10분입니다.)</p>");
		emailBodyBuilder.append("<div style='margin-top:20px;'>");
		emailBodyBuilder.append("<a href='http://localhost/changePassword?key=" + randomAccessKey
				+ "' style='background-color:#3e6ce3; color:white; padding-left:50px; padding-right:50px; padding-top:10px; padding-bottom:10px;'"
				+ ">비밀번호 변경하기 ></a>");
		emailBodyBuilder.append("</div>");
		emailBodyBuilder.append("</div>");
		
		mailSender.sendEmail(emailTitle, emailBodyBuilder.toString());
		return ret;
	}

	@Override
	public boolean verifyChangePasswordForm(String accessKey) {
		if(accessKey == null || accessKey.isEmpty()) {
			return false;
		}
		 
		FindAccountEmailLinkVO vo = memberMapper.selectFindAccountEmailInfo(accessKey);
		if(vo == null) {
			return false;
		}
		return true;
	}

	@Override
	@Transactional
	public Map<String, Object> changePassword(String accessKey, String pwd, String pwdVerify) {
		// TODO Auto-generated method stub
		Map<String, Object> ret = new HashMap<String,Object>();
		ret.put("result", "200");
		
		if(accessKey == null || accessKey.isEmpty()) {
			ret.put("result", "400");
			return ret;
		}
		
		if(!pwd.equals(pwdVerify)) {
			ret.put("result", "400");
			return ret;
		}
		
		FindAccountEmailLinkVO vo = memberMapper.selectFindAccountEmailInfo(accessKey);
		if(vo == null) {
			ret.put("result", "400");
			return ret;
		}
		
		String bCryptPassword = bCryptPasswordEncoder.encode(pwd);
		String id = vo.getMemberId();
		if(memberMapper.updateMemberPassword(id, bCryptPassword) <= 0) {
			ret.put("result", "500");
			return ret;
		}
		
		if(memberMapper.deleteFindAccountEmailLink(accessKey) <= 0) {
			ret.put("result", "500");
			return ret;
		}
		return ret;
	}
}
