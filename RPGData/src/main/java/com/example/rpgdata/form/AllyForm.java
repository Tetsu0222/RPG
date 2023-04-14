package com.example.rpgdata.form;

import com.example.rpgdata.entity.Ally;

import lombok.Data;

@Data
public class AllyForm {
	
	private Integer id;
	private String name;
	private Integer hp;
	private Integer mp;
	private Integer atk;
	private Integer def;
	private Integer spe;
	private String magic;
	private String skill;
	private Integer resistance;
	private String turnstartskill;
	private String turnendskill;
	
	
	public Ally toEntity() {
		Ally ally = new Ally();
		ally.setId( id );
		ally.setName( name );
		ally.setHp( hp );
		ally.setMp( mp );
		ally.setAtk( atk );
		ally.setDef( def );
		ally.setSpe( spe );
		ally.setMagic( magic );
		ally.setSkill( skill );
		ally.setResistance( resistance );
		ally.setTurnstartskill( turnstartskill );
		ally.setTurnendskill( turnendskill );
		
		return ally;
	}

}
