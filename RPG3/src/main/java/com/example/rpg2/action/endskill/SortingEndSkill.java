package com.example.rpg2.action.endskill;

public class SortingEndSkill {
	
	public static EndSkill sortingSkill( String endSkillName ) {
		
		EndSkill endSkill = null;
		
		switch( endSkillName ) {
		
			case "HP":
				endSkill = new AutoRecovery();
				
			case "MP":
				endSkill = new AutoRecoveryMp();
		}
		
		return endSkill;
	}

}
