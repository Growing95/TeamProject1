package com.ict.spring.notice.controller;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.ict.spring.member.model.vo.Member;
import com.ict.spring.notice.model.service.NoticeService;
import com.ict.spring.notice.model.vo.Notice;
import com.ict.spring.common.SearchDate;

@Controller
public class NoticeController {
	private static final Logger logger = LoggerFactory.getLogger("NoticeController.class");

	@Autowired
	private NoticeService noticeService;
	
	@RequestMapping(value="nsearchTitle.do", method=RequestMethod.POST)	
	public String noticeSearchTitleMethod(
			@RequestParam("keyword") String keyword, Model model) {
		ArrayList<Notice> list = noticeService.selectSearchTitle(keyword);

		if (list.size() > 0) {
			model.addAttribute("list", list);
			return "notice/noticeListView";
		} else {
			model.addAttribute("msg", keyword + "로 검색된 공지사항 정보가 없습니다.");
			return "common/errorPage";
		}
	}
	
	@RequestMapping(value="nsearchWriter.do", method=RequestMethod.POST)	
	public String noticeSearchWriterMethod(
			@RequestParam("keyword") String keyword, Model model) {
		ArrayList<Notice> list = noticeService.selectSearchWriter(keyword);

		if (list.size() > 0) {
			model.addAttribute("list", list);
			return "notice/noticeListView";
		} else {
			model.addAttribute("msg", keyword + "로 검색된 공지사항 정보가 없습니다.");
			return "common/errorPage";
		}
	}
	
	@RequestMapping(value="nsearchDate.do", method=RequestMethod.POST)	
	public String noticeSearchDateMethod(SearchDate dates, Model model) {
		ArrayList<Notice> list = noticeService.selectSearchDate(dates);

		if (list.size() > 0) {
			model.addAttribute("list", list);
			return "notice/noticeListView";
		} else {
			model.addAttribute("msg", "날짜로 검색된 공지사항 정보가 없습니다.");
			return "common/errorPage";
		}
	}

	//ajax 로 최신 공지글 조회 요청 처리용
	@RequestMapping(value="ntop3.do", method=RequestMethod.POST)
	@ResponseBody
	public String noticeNewTop3Method(HttpServletResponse response) throws UnsupportedEncodingException {
		//최신 공지글 3개 조회해 옴
		ArrayList<Notice> list = noticeService.selectNewTop3();
		
		//전송용 json 객체 준비
		JSONObject sendJson = new JSONObject();
		//list 옮길 json 배열 준비
		JSONArray jarr = new JSONArray();
		
		//list 를 jarr 로 옮기기(복사)
		for(Notice notice : list) {
			//notice 필드값 저장할 json 객체 생성
			JSONObject job = new JSONObject();
			
			job.put("nid", notice.getNid());
			job.put("ntitle", URLEncoder.encode(notice.getNtitle(), "utf-8"));
			job.put("n_create_date", notice.getN_create_date().toString());
			
			//job 를 jarr 에 저장
			jarr.add(job);
		}
		
		//전송용 json 객체에 jarr 담음
		sendJson.put("list", jarr);
		
		return sendJson.toJSONString();  //jsonView 가 리턴됨
	}	
	
	
	// 공지사항 전체 목록보기 요청 처리용
	@RequestMapping("nlist.do")
	public String noticeListMethod(Model model) {
		ArrayList<Notice> list = noticeService.selectAll();

		if (list.size() > 0) {
			model.addAttribute("list", list);
			return "notice/noticeListView";
		} else {
			model.addAttribute("msg", "등록된 공지사항 정보가 없습니다.");
			return "common/errorPage";
		}
	}

	// 공지글 상세보기 요청 처리용
	@RequestMapping("ndetail.do")
	public String noticeDetailMethod(@RequestParam("nid") int nid, Model model, HttpSession session) {
		Notice notice = noticeService.selectNotice(nid);

		if (notice != null) {
			model.addAttribute("notice", notice);
			// 관리자가 상세보기 요청했을 때
			Member loginUser = (Member) session.getAttribute("loginUser");
			if (loginUser != null && loginUser.getId().equals("admin")) {
				return "notice/noticeAdminDetailView";
			} else {
				// 관리자가 아닌 고객이 상세보기 요청했을 때
				return "notice/noticeDetailView";
			}
		} else {
			model.addAttribute("msg", nid + "번 공지 상세보기 실패.");
			return "common/errorPage";
		}
	}

	// 공지글 등록 페이지 요청 처리용
	@RequestMapping("nwform.do")
	public String noticeWriteForm() {
		return "notice/noticeWriteForm";
	}

	// 공지글 수정 페이지 요청 처리용
	@RequestMapping("upmove.do")
	public String noticeUpdateForm(@RequestParam("nid") int nid, Model model) {
		Notice notice = noticeService.selectNotice(nid);
		if (notice != null) {
			model.addAttribute("notice", notice);
			return "notice/noticeUpdateForm";
		} else {
			model.addAttribute("msg", nid + "번 공지 수정페이지로 이동 요청 실패.");
			return "common/errorPage";
		}
	}

	// 파일업로드 기능이 있는 공지글 등록 요청 처리용
	@RequestMapping(value = "ninsert.do", method = RequestMethod.POST)
	public String noticeInsertMethod(Notice notice, HttpServletRequest request, Model model,
			@RequestParam(name = "upfile", required = false) MultipartFile mfile) {
		// 업로드된 파일 저장 폴더 지정하기
		String savePath = request.getSession().getServletContext().getRealPath("resources/notice_files");

		// 첨부파일이 있을때만 업로드된 파일을 지정 폴더로 옮기기
		if (mfile != null) {
			String fileName = mfile.getOriginalFilename();
			if (fileName != null && fileName.length() > 0) {
				try {
					mfile.transferTo(new File(savePath + "\\" + fileName));
				} catch (Exception e) {
					e.printStackTrace();
					model.addAttribute("msg", "전송파일 저장 실패.");
					return "common/errorPage";
				}
				notice.setFile_path(mfile.getOriginalFilename());
				logger.info("ninsert.do : " + notice);
			}
		}

		if (noticeService.insertNotice(notice) > 0) {
			return "redirect:nlist.do";
		} else {
			model.addAttribute("msg", "공지글 등록 실패.");
			return "common/errorPage";
		}
	}

	// 첨부파일 다운로드 요청 처리용
	@RequestMapping("nfdown.do")
	public ModelAndView fileDownMethod(HttpServletRequest request, @RequestParam("file_path") String fileName) {
		String savePath = request.getSession().getServletContext().getRealPath("resources/notice_files");
		File downFile = new File(savePath + "\\" + fileName);

		/*
		 * ModelAndView(String viewName, String modelName, Object modelObject) Model 클래스
		 * 객체 = request + response modelName == 이름, modelObject == 객체
		 * request.setAttribute("이름", 객체) 와 같은 의미임
		 */

		// 스프링에서는 파일다운하려면, 스프링이 제공하는 View 클래스를 상속받은
		// 파일다운처리용 뷰클래스를 별도로 작성하고, DispatcherServlet 에
		// 파일다운로드용 뷰클래스를 실행시키는 뷰리졸버를 등록해야 함
		return new ModelAndView("filedown", "downFile", downFile);
	}

	// 공지글 삭제 요청 처리용
	@RequestMapping("ndel.do")
	public String noticeDeleteMethod(@RequestParam("nid") int nid, 
			@RequestParam(name="file_path", required = false) String fileName,
			HttpServletRequest request, 
			Model model) {
		if (noticeService.deleteNotice(nid) > 0) {
			//첨부파일이 있는 글일 때, 저장폴더에 있는 파일도 삭제함
			if(fileName != null) {
				new File(	request.getSession().getServletContext()
						.getRealPath("resources/notice_files") + "\\" + fileName).delete();
			}
			return "redirect:nlist.do";
		} else {
			model.addAttribute("msg", nid + "번 공지글 삭제 실패.");
			return "common/errorPage";
		}
	}

	// 공지글 수정 요청 처리용
	@RequestMapping(value = "nupdate.do", method = RequestMethod.POST)
	public String noticeUpdateMethod(Notice notice, HttpServletRequest request, Model model,
			@RequestParam(name = "delFlag", required = false) String delFlag,
			@RequestParam(name = "upfile", required = false) MultipartFile mfile) {
				
		// 업로드된 파일 저장 폴더 지정하기
		String savePath = request.getSession().getServletContext().getRealPath("resources/notice_files");

		// 원래 첨부파일이 있는데, 삭제를 선택한 경우
		if (notice.getFile_path() != null && delFlag != null && delFlag.equals("yes")) {
			// 저장 폴더에서 파일을 삭제함
			new File(savePath + "\\" + notice.getFile_path()).delete();
			notice.setFile_path(null);
		}

		// 첨부파일이 없었는데, 새로 추가한 경우
		if (mfile != null) {
			String fileName = mfile.getOriginalFilename();
			System.out.println("update : " + fileName);
			if (notice.getFile_path() == null && fileName.length() > 0) {
				// 업로드된 파일을 지정 폴더로 옮기기
				try {
					mfile.transferTo(new File(savePath + "\\" + fileName));
				} catch (Exception e) {
					e.printStackTrace();
					model.addAttribute("msg", "전송파일 저장 실패.");
					return "common/errorPage";
				}
				notice.setFile_path(fileName);
			}
		}		

		if (noticeService.updateNotice(notice) > 0) {
			return "redirect:nlist.do";
		} else {
			model.addAttribute("msg", notice.getNid() + "번 공지글 수정 실패.");
			return "common/errorPage";
		}

	}

}
