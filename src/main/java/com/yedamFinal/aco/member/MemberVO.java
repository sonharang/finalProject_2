package com.yedamFinal.aco.member;

import java.util.Date;

import lombok.Data;
@Data

public class MemberVO {

	private int memberNo;
	private String id;
	private String password;
	private String email;
	private String phoneNum;
	private String name;
	private String nickname;
	private String profileImage;
	private String topicHashtag;
	private String permission;
	private Date leaveDate;
	private int accumActivityPoint;
	private int availableActivityPoint;
	private int acoMoney;
	private int acoPoint;
	private String memberLevel;
	private String gradeName;
	private String snsType;
	private String banType;
	private String banDate;
	private String gitToken;
	private int acoTotal;
	private int availableAccumPoint;
}
