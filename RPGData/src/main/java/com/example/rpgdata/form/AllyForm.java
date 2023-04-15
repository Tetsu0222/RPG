package com.example.rpgdata.form;



import com.example.rpgdata.entity.Ally;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AllyForm {
	
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
	
	private String magic;
	private String skill;
	
	@Min(value = 0 )
	private Integer resistance;
	
	@NotBlank
	private String turnstartskill;
	
	@NotBlank
	private String turnendskill;
	
	
	//入力データからエンティティクラスを生成
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
