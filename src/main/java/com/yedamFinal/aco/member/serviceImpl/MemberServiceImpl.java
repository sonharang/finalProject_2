package com.yedamFinal.aco.member.serviceImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.yedamFinal.aco.common.TagVO;
import com.yedamFinal.aco.member.MemberVO;
import com.yedamFinal.aco.member.mapper.MemberMapper;
import com.yedamFinal.aco.member.service.MemberService;

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
	
    private DefaultMessageService messageService;
    
    @PostConstruct
    public void init() {
        // 반드시 계정 내 등록된 유효한 API 키, API Secret Key를 입력해주셔야 합니다!
        this.messageService = NurigoApp.INSTANCE.initialize(coolSmsApiKey, coolSmsApiSecretKey, "https://api.coolsms.co.kr");
    }
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// TODO Auto-generated method stub
		return null;
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
	
	
}
