package com.example.rpgdata.form;

import com.example.rpgdata.entity.MonsterPattern;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;


@Data
public class PatternForm {
	
    private Integer id;
    
    @NotBlank
    private String name;
    
    @Min(value = 0 )
    private Integer mp;

    @NotBlank
    private String category;
    
    @Min(value = 0 )
    private Integer point;
    
    @Min(value = 0 )
    private Double percentage;
    
    @NotBlank
    private String range;

    @NotBlank
    private String text;
    
    @NotBlank
    private String buffcategory;

    @NotBlank
    private String attribute;
    
    
    
    public MonsterPattern toEntity() {
    	
    	MonsterPattern monsterPattern = new MonsterPattern();
    	monsterPattern.setId( id );
    	monsterPattern.setName( name );
    	monsterPattern.setMp( mp );
    	monsterPattern.setCategory( category );
    	monsterPattern.setPoint( point );
    	monsterPattern.setPercentage( percentage );
    	monsterPattern.setRange( range );
    	monsterPattern.setText( text );
    	monsterPattern.setBuffcategory( buffcategory );
    	monsterPattern.setAttribute( attribute );
    	
    	return monsterPattern;
    }

}
