package com.example.rpg2.action.endskill;

public class SortingEndSkill {
	
	public static EndSkill sortingSkill( String endSkillName ) {
		
		EndSkill endSkill = null;
		
		if( endSkillName.equals( "HP" )) {
			endSkill = new AutoRecovery();
		}
		
		return endSkill;
	}

}
