package com.example.rpg2.status;

public interface Status {
	
	public String statusMessageAfter();
	
	public String statusMessageBefore();
	
	public Integer actionStatusBefore();
	
	public Integer actionStatusAfter();
	
	public Integer countDown();
	
	public String getName();

}
