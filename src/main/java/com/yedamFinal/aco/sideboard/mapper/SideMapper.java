package com.yedamFinal.aco.sideboard.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.yedamFinal.aco.sideboard.SideVO;
@Mapper
public interface SideMapper {
	public List<SideVO> selectRecruitingList(String status);
	public SideVO selectSideInfo(@Param("bno") int bno);
	public int updateStatus(@Param(value="bno") int bno, @Param(value="status") String status, SideVO sideVO);
}
