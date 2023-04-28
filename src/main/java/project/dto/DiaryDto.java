package project.dto;

import lombok.Data;

@Data
public class DiaryDto {
	private int diaryId;
    private String diaryContent;
    private String diaryImg;
    private String createDt;
    private String deleteDt;
    private int moodId;
    private int nameId;
}