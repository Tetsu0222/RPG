package com.example.rpgdata.form;

import com.example.rpgdata.entity.Magic;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MagicForm {
	
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
    
    @Min(value = 1 )
    private Integer frequency;
    
    
    public Magic toEntity() {
    	
    	Magic magic = new Magic();
    	magic.setId( id );
    	magic.setName( name );
    	magic.setMp( mp );
    	magic.setCategory( category );
    	magic.setPoint (point );
    	magic.setPercentage( percentage );
    	magic.setRange( range );
    	magic.setText( text );
    	magic.setBuffcategory( buffcategory );
    	magic.setFrequency( frequency );
    	
    	return magic;
    }

}
