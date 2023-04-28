package project.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;
import project.dto.DiaryDto;
import project.dto.NameDto;
import project.service.DiaryService;

@Slf4j
@RestController
public class DiaryApiController {
	@Autowired
	private DiaryService diaryService;
	private DiaryDto diaryDto;
	private NameDto nameDto;
	
	final String UPLOAD_PATH = "/logon/diaryImg/";
	
	
	// 일기 목록 화면
	@GetMapping("/api/comon/logon")
	public ResponseEntity<List<DiaryDto>> openBoardList() throws Exception {
		List<DiaryDto> list = diaryService.selectDiaryList();
		if (list != null && list.size() > 0) {
			return ResponseEntity.status(HttpStatus.OK).body(list);
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
	}

	// 1. 일기 목록 조회 (월에 맞는 일기들을 조회해야함)
	// 셀렉을 할 때 웨어절에 현재 달이랑 맞는 거 목록에서 무드 아이디만 줘 프론트에서 아이디에 따라 if로 이미지로 변환  
	@GetMapping("/api/comon/logon/{createDt}")
	private ResponseEntity<List<DiaryDto>> openDiaryListDt(@PathVariable("createDt") String createDt) throws Exception {
		List<DiaryDto> list = diaryService.selectDiaryListDt(createDt);
		
		return ResponseEntity.status(HttpStatus.OK).body(list);
	}
	
	// 2. 일기 상세조회 
//	@GetMapping("/api/comon/logon/page/{diaryId}")
//	private ResponseEntity<DiaryDto> openDiaryDetail(@PathVariable("diaryId") int diaryId,
//			HttpServletResponse response) throws Exception {
//		DiaryDto diaryDto = diaryService.selectDiaryDetail(diaryId);
//		String diaryImg = diaryDto.getDiaryImg();
//		if (diaryDto == null) {
//			return ResponseEntity.status(HttpStatus.OK).body(null);
//		} else {
//			return ResponseEntity.status(HttpStatus.OK).body(diaryDto);
//		}
//	}
	
	@GetMapping("/api/comon/logon/page/{diaryId}")
	private ResponseEntity<Map<String, Object>> openDiaryDetail(@PathVariable("diaryId") int diaryId,
			HttpServletResponse response) throws Exception {
		DiaryDto diaryDto = diaryService.selectDiaryDetail(diaryId);
		String diaryImg = diaryDto.getDiaryImg();
		String nameTitle = diaryService.selectName();
		
		Map<String, Object> result = new HashMap<>();
		
		result.put("diaryDto", diaryDto);
		result.put("name", nameTitle);
		
		if (diaryDto == null || nameTitle == null) {
			return ResponseEntity.status(HttpStatus.OK).body(null);
		} else {
			return ResponseEntity.status(HttpStatus.OK).body(result);
		}
	}
	
	// 3. 일기 수정 
	@PutMapping("/api/comon/logon/page/{diaryId}")
	public ResponseEntity<Integer> updateDiary(@PathVariable("diaryId") int diaryId, @RequestBody DiaryDto diaryDto)
			throws Exception {
		diaryDto.setDiaryId(diaryId);
		int updatedCount = diaryService.updateDiary(diaryDto);
		if (updatedCount != 1) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(updatedCount);
		} else {
			return ResponseEntity.status(HttpStatus.OK).body(updatedCount);
		}
	}
	
	// 4. 일기 삭제 
	@DeleteMapping("/api/comon/logon/page/{diaryId}")
	public ResponseEntity<Integer> deleteDiary(@PathVariable("diaryId") int diaryId)
			throws Exception {
		int deletedCount = diaryService.deleteDiary(diaryId);
		if (deletedCount != 1) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(deletedCount);
		} else {
			return ResponseEntity.status(HttpStatus.OK).body(deletedCount);
		}
	}
		
	
	//5-0. 일기 작성 전 중복 체크
	@GetMapping("/api/comon/logon/distinct/write")
	public ResponseEntity<DiaryDto> writeDiary() throws Exception {
		DiaryDto diaryDto = diaryService.writeDiary();
		
		if (diaryDto != null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(diaryDto);
		} else {
			return ResponseEntity.status(HttpStatus.OK).body(diaryDto);
		}
	}
	
	// 5. 일기 작성 
	@PostMapping("/api/comon/logon/write")
	public ResponseEntity<Map<String, Object>> insertDiary(
			@RequestPart(value = "files", required = false) MultipartFile[] files,
			@RequestPart(value = "data", required = false) DiaryDto diaryDto, HttpServletRequest request)
			throws Exception {
		
		String FileNames = "";
		int insertedCount = 0;

		try {
			for (MultipartFile mf : files) {
				String originFileName = mf.getOriginalFilename(); // 원본 파일 명
				long fileSize = mf.getSize(); // 파일 사이즈

				System.out.println("originFileName : " + originFileName);
				System.out.println("fileSize : " + fileSize);
				String safeFile = System.currentTimeMillis() + originFileName;
				diaryDto.setDiaryImg(safeFile);

				try {
					File f1 = new File(UPLOAD_PATH + safeFile);
					f1.mkdir();
					mf.transferTo(f1);
				} catch (IllegalStateException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
					}
			}

			insertedCount = diaryService.insertDiary(diaryDto);
			if (insertedCount > 0) {
				Map<String, Object> result = new HashMap<>();
				result.put("message", "정상적으로 등록되었습니다.");
				result.put("count", insertedCount);
				result.put("diaryId", diaryDto.getDiaryId());
				return ResponseEntity.status(HttpStatus.OK).body(result);
			} else {
				Map<String, Object> result = new HashMap<>();
				result.put("message", "등록된 내용이 없습니다.");
				result.put("count", insertedCount);
				return ResponseEntity.status(HttpStatus.OK).body(result);
			}
		} catch (Exception e) {
			Map<String, Object> result = new HashMap<>();
			result.put("message", "등록 중 오류가 발생했습니다.");
			result.put("count", insertedCount);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
		}
	}
	
	//  다이어리 이미지 다운로드 
	@GetMapping("/api/getImage/{diaryImg}")
	public void getImage(@PathVariable("diaryImg") String diaryImg, HttpServletResponse response) throws Exception {
		// reviewImage를 읽어서 전달
		System.out.println(">>>>>>>>>>>>>>>>>>" + diaryImg);
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;

		try {
			response.setHeader("Content-Disposition", "inline;");

			byte[] buf = new byte[1024];
			fis = new FileInputStream(UPLOAD_PATH + diaryImg);
			System.out.println(">>>>>>>>>>>>>>>>>>" + diaryImg);
			bis = new BufferedInputStream(fis);
			bos = new BufferedOutputStream(response.getOutputStream());
			int read;
			while ((read = bis.read(buf, 0, 1024)) != -1) {
				bos.write(buf, 0, read);
			}
		} finally {
			bos.close();
			bis.close();
			fis.close();
		}
	}
	
	
	// 17. 이름 조회
	@GetMapping("/api/comon/logon/name")
	public ResponseEntity<String> selectName() throws Exception {
		
		String name = diaryService.selectName();
		
		if (name == null) {
			return ResponseEntity.status(HttpStatus.OK).body(null);
		} else {
			return ResponseEntity.status(HttpStatus.OK).body(name);
		}
	}




	
	// 19. 이름 수정
		@PutMapping("/api/comon/logon/name")
		public ResponseEntity<Integer> updateName(@RequestBody NameDto nameDto)
				throws Exception {
			
			System.out.println(nameDto.toString());
			nameDto.setNameId(1);
			int updateCount = diaryService.updateName(nameDto);

			if (updateCount != 0) {
				return ResponseEntity.status(HttpStatus.OK).body(updateCount);
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
			}
		}

		
//		// 19. 개인 목표 수정
//		@PutMapping("/api/comon/logon/name/{nameId}")
//		public ResponseEntity<Integer> updateName(@PathVariable("nameId") int nameId, @RequestBody NameDto nameDto)
//				throws Exception {
	//
//			nameDto.setNameId(nameId);
//			int updateCount = diaryService.updateName(nameDto);
	//
//			if (updateCount != 0) {
//				return ResponseEntity.status(HttpStatus.OK).body(updateCount);
//			} else {
//				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
//			}
//		}
	
	
	
	
	
}
