package com.yedamFinal.aco.sideboard.web;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yedamFinal.aco.member.MemberVO;
import com.yedamFinal.aco.member.UserDetailVO;
import com.yedamFinal.aco.member.serviceImpl.MemberServiceImpl;
import com.yedamFinal.aco.sideboard.SideVO;
import com.yedamFinal.aco.sideboard.serviceImpl.SideServiceImpl;
@Controller
public class SideController {
	@Autowired
	SideServiceImpl sideService;
	@Autowired
	MemberServiceImpl memberService;
	
	@GetMapping("/sideProject/{status}")
	public String getsideProjectForm(@PathVariable("status") String status, Model model) {
		List<SideVO> list = sideService.getRecruitingList(status);
		model.addAttribute("recList", list);
		return "sideboard/sideProject";
	}
	
	@GetMapping("/sideInfo/{status}/{bno}")
    public String getSideProjectDetails(@PathVariable("status") String status, @PathVariable("bno") int bno,
                                        Model model) {
		MemberVO memberVO = null;
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.getPrincipal() instanceof UserDetailVO) {
			UserDetailVO userDetails = (UserDetailVO) authentication.getPrincipal();
			memberVO = userDetails.getMemberVO();
		}
        SideVO vo = sideService.getSideInfo(bno);
        model.addAttribute("sideInfo", vo);
        String isCheckMember = null;
        if(memberVO != null && memberVO.getMemberNo() == vo.getMemberNo()) {
        	model.addAttribute("isCheckMember", "1");
        	}
		return isCheckMember;
    }
	
	/*
	 * @PostMapping("/updateStatus")
	 * 
	 * @ResponseBody public Map<String, Object> updateStatus(@RequestBody String
	 * status) { MemberVO memberVO = null; Authentication authentication =
	 * SecurityContextHolder.getContext().getAuthentication(); if (authentication !=
	 * null && authentication.getPrincipal() instanceof UserDetailVO) { UserDetailVO
	 * userDetails = (UserDetailVO) authentication.getPrincipal(); memberVO =
	 * userDetails.getMemberVO(); }
	 * 
	 * 
	 * return map; }
	 */
	
}
