package project.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import project.dto.DiaryDto;
import project.dto.MoodDto;
import project.dto.NameDto;
import project.mapper.DiaryMapper;

@Slf4j
@Service
public class DiaryServiceImpl implements DiaryService {
	@Autowired
	private DiaryMapper diaryMapper;

	// 일기목록 조회
	@Override
	public List<DiaryDto> selectDiaryList() throws Exception {
		// TODO Auto-generated method stub
		return diaryMapper.selectDiaryList();
	}

	// 1. 일기 목록 조회 (캘린더)
	// 셀렉을 할 때 웨어절에 현재 달이랑 맞는 거 목록에서 무드 아이디만 줘 프론트에서 아이디에 따라 if로 이미지로 변환
	@Override
	public List<DiaryDto> selectDiaryListDt(String createDt) throws Exception {
		// TODO Auto-generated method stub
		return diaryMapper.selectDiaryListDt(createDt);
	};

	// 2. 일기 상세조회
	@Override
	public DiaryDto selectDiaryDetail(int diaryId) throws Exception {
		return diaryMapper.selectDiaryDetail(diaryId);
	};

	// 3. 일기 수정
	@Override
	public int updateDiary(DiaryDto diaryDto) throws Exception {
		return diaryMapper.updateDiary(diaryDto);
	};

	// 4. 일기 삭제
	@Override
	public int deleteDiary(int diaryId) throws Exception {
		return diaryMapper.deleteDiary(diaryId);
	};

	// 5-0. 일기 작성 전 중복 체크
	public DiaryDto writeDiary() throws Exception {
		return diaryMapper.writeDiary();
	}

	// 5. 일기 작성
	@Override
	public int insertDiary(DiaryDto diaryDto) throws Exception {
		return diaryMapper.insertDiary(diaryDto);
	}

	// 수정
	@Override
	public int updateName(NameDto nameDto) throws Exception {
		// TODO Auto-generated method stub
		return diaryMapper.updateName(nameDto);
	}

	@Override
	public String selectName() throws Exception {
		// TODO Auto-generated method stub
		return diaryMapper.selectName();
	};

}
