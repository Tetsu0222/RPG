package com.example.rpgdata.form;

import com.example.rpgdata.entity.Skill;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SkillForm {
	
	
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
    
    @Min(value = 0 )
    private Integer frequency;
    
    private String target;
    
    private Integer maxfrequency;
    
    
    public Skill toEntity() {
    	
    	Skill skill = new Skill();
    	skill.setId( id );
    	skill.setName( name );
    	skill.setMp( mp );
    	skill.setCategory( category );
    	skill.setPoint( point );
    	skill.setPercentage( percentage );
    	skill.setRange( range );
    	skill.setText( text );
    	skill.setBuffcategory( buffcategory );
    	skill.setFrequency( frequency );
    	skill.setTarget( target );
    	skill.setMaxfrequency( maxfrequency );
    	
    	return skill;
    }
}
