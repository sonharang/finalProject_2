package com.yedamFinal.aco.admin.mapper;

import java.util.List;

import com.yedamFinal.aco.admin.AdminEmoVO;
import com.yedamFinal.aco.admin.AdminMemberVO;
import com.yedamFinal.aco.admin.AdminQnaVO;
import com.yedamFinal.aco.admin.AdminReportVO;
import com.yedamFinal.aco.admin.AdminSettleVO;

public interface AdminMapper { 
	public List<AdminMemberVO> getAdMemberList();
	public List<AdminReportVO> getAdReportList();
	public List<AdminQnaVO>    getAdQnaList();
	public List<AdminSettleVO> getAdSettleList();
	public List<AdminEmoVO> getAdEmoList();	
}
