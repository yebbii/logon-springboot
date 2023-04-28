package project.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

import project.dto.DiaryDto;
import project.dto.NameDto;

@Mapper
public interface DiaryMapper {
	// 일기목록 조회
	public List<DiaryDto> selectDiaryList() throws Exception;

	// 1. 일기 목록 조회 (캘린더)
	// 셀렉을 할 때 웨어절에 현재 달이랑 맞는 거 목록에서 무드 아이디만 줘 프론트에서 아이디에 따라 if로 이미지로 변환
	public List<DiaryDto> selectDiaryListDt(@Param("createDt") String createDt) throws Exception;

	// 2. 일기 상세조회
	public DiaryDto selectDiaryDetail(@Param("diaryId") int diaryId) throws Exception;

	// 3. 일기 수정
	public int updateDiary(DiaryDto diaryDto) throws Exception;

	// 4. 일기 삭제
	public int deleteDiary(int diaryId) throws Exception;

	// 5-0. 일기 작성 전 중복 체크
	public DiaryDto writeDiary() throws Exception;

	// 5. 일기 작성
	public int insertDiary(DiaryDto diaryDto) throws Exception;

	// 이름 수정
	public int updateName(NameDto nameDto) throws Exception;

	// 이름 조회
	public String selectName() throws Exception;

	// 5-1. 기분 선택
//	List<MoodDto> moodList() throws Exception;
}
