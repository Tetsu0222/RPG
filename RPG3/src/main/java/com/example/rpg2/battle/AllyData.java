package com.example.rpg2.battle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.example.rpg2.entity.Ally;
import com.example.rpg2.entity.Magic;
import com.example.rpg2.repository.MagicRepository;

import lombok.Data;

@Data
public class AllyData {
	
	private String  name;
	private Integer maxHP;
	private Integer maxMP;
	private Integer defaultATK;
	private Integer defaultDEF;
	private Integer defaultSPE;
	
	private Integer currentHp;
	private Integer currentMp;
	private Integer currentATK;
	private Integer currentDEF;
	private Integer currentSPE;
	private String  magic;
	private int survival;
	
	//使用可能な魔法を格納
	List<Magic> magicList = new ArrayList<>();
	
	
	public AllyData( Ally ally , MagicRepository magicRepository ) {
		
		this.name = ally.getName();
		
		//固定ステータスの設定
		this.maxHP = ally.getHp();
		this.maxMP = ally.getMp();
		this.defaultATK = ally.getAtk();
		this.defaultDEF = ally.getDef();
		this.defaultSPE = ally.getSpe();
		
		//変動ステータスの設定
		this.currentHp  = ally.getHp();
		this.currentMp  = ally.getMp();
		this.currentATK = ally.getAtk();
		this.currentDEF = ally.getDef();
		this.currentSPE = ally.getSpe();
		
		//使用可能な魔法の設定
		this.magic = ally.getMagic();
		String[] magicSource = magic.split( "," );
		List<String> sourceList = Arrays.asList( magicSource );
		sourceList.stream()
		.map( s -> Integer.parseInt( s ) )
		.map( s ->  magicRepository.findById( s ) )
		.forEach( s -> magicList.add( s.orElseThrow() ));
		
		//生存設定
		this.survival = 1;

	}
	
	
	//蘇生時のステータス処理
	public void resuscitation() {
		this.survival = 1;
		this.currentATK = defaultATK;
		this.currentDEF = defaultDEF;
		this.currentSPE = defaultSPE;
	}
	
}
