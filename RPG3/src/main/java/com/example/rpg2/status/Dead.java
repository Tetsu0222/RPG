package com.example.rpg2.status;


public class Dead implements Status{
	
	private String name = "死亡";
	
	public int hashCode() {
		return name.hashCode();
	}
	
	public boolean equals( Object obj ) {
		
		return this.hashCode() == obj.hashCode();
	}
	
	@Override
	public Integer actionStatusBefore() {
		//no
		
		return 1;
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
