package com.example.rpgdata.form;

import java.util.ArrayList;
import java.util.List;

import com.example.rpgdata.entity.AttachedFile;
import com.example.rpgdata.entity.Monster;
import com.example.rpgdata.support.SaveAttachedFile;

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
	
	
	//画像データのファイル名などをリストで格納
	private List<AttachedFileForm> attachedFileList;
	
	
	public MonsterForm() {}
	
	
	//エンティティクラスから自クラスを生成
	public MonsterForm( Monster monster , List<AttachedFile> attachedFiles ) {
		
		//基本項目の関連付け
		this.id = monster.getId();
		this.name = monster.getName();
		this.hp = monster.getHp();
		this.mp = monster.getMp();
		this.atk = monster.getAtk();
		this.def = monster.getDef();
		this.spe = monster.getSpe();
		this.pattern = monster.getPattern();
		this.actions = monster.getActions();
		this.resistance = monster.getResistance();
		
		attachedFileList = new ArrayList<>();
		String fileName;
		String fext;
		String contentType;
		boolean isOpenNewWindow;
		
		for ( AttachedFile af : attachedFiles ) {
			
			//画像データのファイル名を取得
			fileName = af.getFileName();
			
			//ファイルの拡張子を取得
			fext = fileName.substring( fileName.lastIndexOf( "." ) + 1 );
			
			//ファイルの拡張子からコンテントタイプを取得
			contentType = SaveAttachedFile.ext2contentType( fext );
			
			//コンテントタイプが不明かnullのものは別ウィンドウで表示
			isOpenNewWindow = contentType.equals( "" ) ? false : true;
			
			//取得できたファイル情報類などでデータのやり取りをするオブジェクトを生成し格納
			attachedFileList.add( new AttachedFileForm( af.getId() , fileName , af.getNote() , isOpenNewWindow ));
		}
	}
	
	
	//自クラスからエンティティクラスを生成
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
