package com.yedamFinal.aco.qnaBoard.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.yedamFinal.aco.qnaBoard.QnABoardVO;

public interface QnABoardMapper { 
	public List<QnABoardVO> selectMyQnaBoardList(@Param(value="pageNo") int pageNo, @Param(value="memberNo") int memberNo, @Param("orderBy") String orderBy);
	public List<QnABoardVO> selectMyQnaBoardListFromSearch(@Param(value="pageNo") int pageNo, @Param(value="search") String search, @Param(value="memberNo") int memberNo, @Param("orderBy") String orderBy);
	public int selectMyQnaBoardCount(int memberNo);
	public int selectMyQnaBoardCountFromSearch(@Param(value="memberNo") int memberNo,@Param(value="search") String search);
}
