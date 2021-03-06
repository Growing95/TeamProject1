package com.ict.spring.notice.model.service;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ict.spring.notice.model.dao.NoticeDao;
import com.ict.spring.notice.model.vo.Notice;
import com.ict.spring.common.SearchDate;

@Service("noticeService")
public class NoticeServiceImpl implements NoticeService {
	//의존성 주입(DI : Dependency Injection)
	@Autowired
	private NoticeDao noticeDao;
	
	public NoticeServiceImpl() {}
	
	@Override
	public ArrayList<Notice> selectAll() {
		return noticeDao.selectList();
	}

	@Override
	public Notice selectNotice(int nid) {
		return noticeDao.selectOne(nid);
	}

	@Override
	public int insertNotice(Notice notice) {
		return noticeDao.insertNotice(notice);
	}

	@Override
	public int updateNotice(Notice notice) {
		return noticeDao.updateNotice(notice);
	}

	@Override
	public int deleteNotice(int nid) {
		return noticeDao.deleteNotice(nid);
	}

	@Override
	public ArrayList<Notice> selectNewTop3() {
		return noticeDao.selectNewTop3();
	}

	@Override
	public ArrayList<Notice> selectSearchTitle(String keyword) {
		return noticeDao.selectSearchTitle(keyword);
	}

	@Override
	public ArrayList<Notice> selectSearchWriter(String keyword) {
		return noticeDao.selectSearchWriter(keyword);
	}

	@Override
	public ArrayList<Notice> selectSearchDate(SearchDate dates) {
		return noticeDao.selectSearchDate(dates);
	}

}
