package com.yedamFinal.aco.freeboard.service.serviceImpl;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import com.yedamFinal.aco.common.PaginationDTO;
import com.yedamFinal.aco.common.ReplyJoinVO;
import com.yedamFinal.aco.common.mapper.ReplyMapper;
import com.yedamFinal.aco.common.serviceImpl.FileServiceImpl;
import com.yedamFinal.aco.freeboard.FreeBoardVO;
import com.yedamFinal.aco.freeboard.mapper.FreeBoardMapper;
import com.yedamFinal.aco.freeboard.service.FreeBoardService;
import com.yedamFinal.aco.noticeboard.NoticeBoardVO;
import com.yedamFinal.aco.noticeboard.mapper.NoticeBoardMapper;
import com.yedamFinal.aco.question.QuestionVO;


@Service
public class FreeBoardServiceImpl implements FreeBoardService {

	@Autowired
	private FileServiceImpl fileService;
	
	@Autowired
	private ReplyMapper replyMapper;
	
	@Autowired
	private FreeBoardMapper freeBoardMapper;
	


	@Override
	public List<FreeBoardVO> getFreeBoardAll(Model model,int pg) {
		var freeBoardList = freeBoardMapper.getFreeBoardAll(pg);
		PaginationDTO dto = null;
		if(freeBoardList.size() > 0) {
			dto = new PaginationDTO(freeBoardMapper.getFreeBoardAllCnt(), pg, 10);
		}
		model.addAttribute("pageDTO", dto);
		
		return freeBoardMapper.getFreeBoardAll(pg);
	}

	@Override
	public FreeBoardVO getFreeBoard(int fboardNo, Model model) {
		freeBoardMapper.updateFreeBoardViewCnt(fboardNo);
		List<ReplyJoinVO> list = replyMapper.selectReply("N004", fboardNo);
		Map<Integer, List<ReplyJoinVO>> groupByData = list.stream().collect(Collectors.groupingBy(ReplyJoinVO::getParentReplyNo));
		groupByData = groupByData.entrySet().stream()
		        .sorted(Map.Entry.comparingByKey())
		        .collect(Collectors.toMap(
		                Map.Entry::getKey,
		                Map.Entry::getValue,
		                (a, b) -> { throw new AssertionError(); },
		                LinkedHashMap::new
		        ));

		model.addAttribute("replyList", groupByData);
	
		return freeBoardMapper.getFreeBoard(fboardNo);
	}

	@Override
	@Transactional
	public Map<String, Object> insertFreeBoard(FreeBoardVO freeBoardVO, MultipartFile[] files) {
		
		Map<String, Object> ret = new HashMap<String,Object>();
		ret.put("result", "200");
		
		//게시글 등록
		if(freeBoardMapper.insertFreeBoard(freeBoardVO) <= 0) {
			ret.put("result", "500");
			return ret;
		}
		
		// 첨부파일 등록
		if(files != null && files.length > 0) {
			int boardNo = freeBoardVO.getPk();
			int memberNo = freeBoardVO.getMemberNo();
			if(!fileService.uploadAttachFiles(files, memberNo, "N004", boardNo)) {
				ret.put("result", "500");
				return ret;
			}
		}
		return ret;
	}
	
	//수정
	@Override
	public Map<String, Object> modifyFreeBoard(String title, String content, int fboardNo) {
		
		Map<String, Object> ret = new HashMap<>();
		ret.put("result", "200");
		
		int result = freeBoardMapper.updateFreeBoard(title, content, fboardNo);
		if(result<=0) {
			ret.put("result", "500");
			return ret;
		}
		
		return ret;
	}
	
	//삭제
	@Override
	public int deleteFreeBoard(int fboardNo) {
		return freeBoardMapper.deleteFreeBoard(fboardNo);
	}

	//검색
	@Override
	public List<FreeBoardVO> getSearchFreeBoard(Model model,String search,int pg) {
		var searchFreeBoardList = freeBoardMapper.searchFreeBoard(search, pg);
		PaginationDTO dto = null;
		if(searchFreeBoardList.size() > 0) {
			dto = new PaginationDTO(freeBoardMapper.searchFreeBoardCnt(search), pg, 10);
		}
		model.addAttribute("pageDTO", dto);
		return freeBoardMapper.searchFreeBoard(search,pg);
	}

	@Override
	public List<FreeBoardVO> getFreeBoardMainPage() {

		return freeBoardMapper.getFreeBoardMainPage();
	}

	@Override
	public List<NoticeBoardVO> getNoticeBoardMainPage() {
		
		return freeBoardMapper.getNoticeBoardMainPage();
	}

	@Override
	public List<QuestionVO> getQuestionBoardMainPage() {
		
		return freeBoardMapper.getQuestionBoardMainPage();
	}
	

}
