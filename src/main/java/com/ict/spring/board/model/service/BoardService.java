package com.ict.spring.board.model.service;

import java.util.ArrayList;

import com.ict.spring.board.model.vo.Board;
import com.ict.spring.common.SearchAndPage;
import com.ict.spring.common.SearchDate;

public interface BoardService {
	ArrayList<Board> selectTop3();
	int getListCount();
	ArrayList<Board> selectList(int currentPage, int limit);
	Board selectBoard(int bid);
	int addReadCount(int bid);
	int insertBoard(Board board);
	int updateBoard(Board board);
	int deleteBoard(int bid);
	ArrayList<Board> selectSearchTitle(SearchAndPage searches);
	ArrayList<Board> selectSearchWriter(SearchAndPage searches);
	ArrayList<Board> selectSearchDate(SearchAndPage searches);
	int getSearchDateListCount(SearchDate dates);
	int getSearchTitleListCount(String keyword);
	int getSearchWriterListCount(String keyword);
}
