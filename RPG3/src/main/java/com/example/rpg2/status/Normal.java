package com.example.rpg2.status;

public class Normal implements Status{
	
	
	private String name = "正常";
	
	
	public int hashCode() {
		return name.hashCode();
	}
	
	public boolean equals( Object obj ) {
		
		return this.hashCode() == obj.hashCode();
	}
	
	@Override
	public Integer actionStatusBefore() {
		//no
		
		return 0;
	}
	
	@Override
	public Integer actionStatusAfter() {
		//no

		return 0;
	}
	
	@Override
	public String statusMessageAfter() {
		//no
		
		return "no";
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	

}
