package com.oneteam.dormease.board;

import com.oneteam.dormease.board.reply.ReplyService;
import com.oneteam.dormease.user.student.StudentDto;
import com.oneteam.dormease.utils.UploadFileDto;
import com.oneteam.dormease.utils.UploadFileService;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpSession;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
@Controller
@RequestMapping("/board")
public class BoardController {
    private final BoardService boardService;
    private final UploadFileService uploadFileService;
    public BoardController(BoardService boardService, UploadFileService uploadFileService) {
        this.boardService = boardService;
        this.uploadFileService = uploadFileService;
    }

    /*
     * 자유 게시판 게시글 리스트 페이지
     */
    @GetMapping("/freeBoardListForm")
    public String freeBoardListForm(HttpSession session, Model model) {
        log.info("freeBoardListForm()");
        StudentDto loginedStudentDto = (StudentDto) session.getAttribute("loginedStudentDto");
        String schoolNo = loginedStudentDto.getSchool_no();
        List<BoardDto> boardDtos = boardService.getAllFreeBoardContent(schoolNo);
        String nextPage = "board/freeBoardListForm";
        model.addAttribute("boardDtos", boardDtos);

        return nextPage;
    }

    /*
     * 게시글 디테일 페이지
     */
    @GetMapping("/detailContentForm")
    public String detailContentForm(@RequestParam("no") int no, Model model) {
        log.info("detailContentForm()");
        String nextPage = "board/detailContentForm";
        Map<String, Object> boardAndReplyMap = boardService.getdetailContent(no);
        model.addAttribute("boardAndReplyMap", boardAndReplyMap);

        return nextPage;
    }

    /*
     * 게시글 작성 페이지
     */
    @GetMapping("/writeContentForm")
    public String writeContentForm() {
        log.info("writeContentForm()");
        String nextpage = "board/writeContentForm";

        return nextpage;
    }

    /*
     * 게시글 작성 컨펌
     */
    @PostMapping("/writeContentConfirm")
    public String writeContentConfirm(@RequestParam(value = "files", required = false)  List<MultipartFile> files,
                                      HttpSession session,
                                      Model model,
                                      BoardDto boardDto) {
        log.info("writeContentConfirm()");
        StudentDto loginedStudentDto = (StudentDto) session.getAttribute("loginedStudentDto");
//        UploadFileDto uploadedfileDto = null;
        int result = -1;

        try {
            List<UploadFileDto> uploadedFileDtos = new ArrayList<>();
            if (files != null || !files.isEmpty()) {
                for (MultipartFile file : files) {
                    String fileOriName = file.getOriginalFilename();
                    // SAVE FILES
                    if (fileOriName != "") {
                        UploadFileDto uploadedFileDto = uploadFileService.upload(loginedStudentDto.getId(), file);
                        uploadedFileDto.setBoard_attach_file(uploadedFileDto.getBoard_attach_file());
                        if (uploadedFileDto != null) {
                            // 업로드된 파일 정보를 리스트에 추가
                            uploadedFileDtos.add(uploadedFileDto);
                        }
                    }
                }
            }
            result = boardService.writeContentConfirm(loginedStudentDto, boardDto, uploadedFileDtos);
        } catch(Exception e) {
            e.printStackTrace();
        }
        // 업로드한 파일들을 처리
        String nextpage = "board/writeContentResult";
        model.addAttribute("result", result);

        return nextpage;
    }

    /*
     * 게시글 수정 페이지
     */
    @PostMapping("/modifyContentForm")
    public String modifyContentForm(@RequestParam(value = "board_no", required = false) int no, Model model) {
        log.info("modifyContentForm()");
        String nextPage = "board/modifyContentForm";
        Map<String, Object> boardAndReplyMap = boardService.getdetailContentForModify(no);
        model.addAttribute("boardAndReplyMap", boardAndReplyMap);

        return nextPage;
    }

    /*
     * 게시글 수정 컨펌
     */
    @PostMapping("/modifyContentConfirm")
    public String modifyContentConfirm(BoardDto boardDto, Model model) {
        log.info("modifyContentConfirm()");
        String nextPage = "board/modifyContentResult";
        int result = boardService.modifyContentConfirm(boardDto);
        model.addAttribute("result", result);

        return nextPage;
    }

    /*
     * 게시글 삭제 컨펌
     */
    @GetMapping("/deleteContentConfirm")
    public String deleteContentConfirm(int no, Model model) {
        log.info("deleteContentConfirm()");
        String nextPage = "board/deleteContentResult";
        int result = boardService.deleteContentConfirm(no);
        model.addAttribute("result", result);

        return nextPage;
    }
}
