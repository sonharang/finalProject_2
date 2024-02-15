package com.yedamFinal.aco.freeboard.web;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.yedamFinal.aco.freeboard.FreeBoardVO;
import com.yedamFinal.aco.freeboard.service.FreeBoardService;
import com.yedamFinal.aco.member.MemberVO;
import com.yedamFinal.aco.member.UserDetailVO;

/**
* @author 박경석
* @since 2024.02.15
* @version 1.0
* @see
* 
* <pre>
* << 개정이력(Modification Information) >>
*  
*  *   수정일     수정자          수정내용
*  -------    --------    ---------------------------
*  2024.02.15   박경석          최초 생성
*  </pre>
* 
* 
**/
@Controller
@PropertySource("classpath:config.properties")
public class FreeBoardController {
	
	@Autowired
	private FreeBoardService freeBoardService;

	/**
	 * 자유게시판 전체조회
	 * @param model
	 * @return freeBoard/freeBoardList
	 */
	
	@GetMapping("/freeBoardList")
	public String getFreeBoardList(Model model) {
		model.addAttribute("getFreeBoardList", freeBoardService.getFreeBoardAll());
		return "freeBoard/freeBoardList";
	}
	
	
	/**
	 * 자유게시판 단건조회
	 * @param fbno
	 * @param model
	 * @return freeBoard/freeBoardInfo
	 */
	@GetMapping("/freeBoardInfo/{fbno}")
	public String getFreeBoard(@PathVariable("fbno") int fbno, Model model) {
		
		model.addAttribute("freeBoardInfo",freeBoardService.getFreeBoard(fbno));
		
		return "freeBoard/freeBoardInfo";
		
	}
	
	/**
	 * 자유게시판 작성 폼
	 * @param model
	 * @return freeBoard/freeBoardForm
	 */

	@GetMapping("/freeBoardForm")
	public String getFreeBoardForm(Model model) {
		return "freeBoard/freeBoardForm";
	}
	
	/**
	 * 자유게시판 등록
	 * @param freeBoardVO
	 * @param attachFile
	 * @param req
	 * @return ret
	 */
	
	@PostMapping("/freeBoardForm")
	@ResponseBody
	public Map<String,Object> insertFreeBoard(FreeBoardVO freeBoardVO, MultipartFile[] attachFile, HttpServletRequest req ){
		MemberVO vo = null;
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailVO) {
        	UserDetailVO userDetails = (UserDetailVO) authentication.getPrincipal();
        	vo = userDetails.getMemberVO();
        	freeBoardVO.setMemberNo(vo.getMemberNo());
        }
        var ret = freeBoardService.insertFreeBoard(freeBoardVO, attachFile);
    
        
		return ret;
	}

	
}
