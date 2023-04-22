package com.example.rpgdata.form;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AttachedFileForm {
	
	private Integer id;
	
	private String fileName;
	
	private String note;
	
	private boolean openInNewTab;
	
	
}
