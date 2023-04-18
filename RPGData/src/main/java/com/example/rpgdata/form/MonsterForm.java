package com.example.rpgdata.form;

import com.example.rpgdata.entity.Monster;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MonsterForm {

	private Integer id;
	
	@NotBlank
	private String name;
	
	@Min(value = 1 )
	private Integer hp;
	
	@Min(value = 1 )
	private Integer mp;
	
	@Min(value = 1 )
	private Integer atk;
	
	@Min(value = 1 )
	private Integer def;
	
	@Min(value = 1 )
	private Integer spe;
	
	private String pattern;
	
	private String actions;
	
	@Min(value = 0 )
	private Integer resistance;
	
	
	public Monster toEntity() {
		
		Monster monster = new Monster();
		monster.setId( id );
		monster.setName( name );
		monster.setHp( hp );
		monster.setMp( mp );
		monster.setAtk( atk );
		monster.setDef( def );
		monster.setSpe( spe );
		monster.setPattern( pattern );
		monster.setActions( actions );
		monster.setResistance( resistance );
		
		return monster;
	}
	
}
