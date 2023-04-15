package com.example.rpgdata.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table( name = "magiclist" )
@Data
public class Magic {
	
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    @Column( name = "id" )
    private Integer id;
    
    @Column( name = "name" )
    private String name;
    
    @Column( name = "mp" )
    private Integer mp;

    @Column( name = "category" )
    private String category;
    
    @Column( name = "point" )
    private Integer point;
    
    @Column( name = "percentage" )
    private Double percentage;
    
    @Column( name = "ra_nge" )
    private String range;
    
    @Column( name = "text" )
    private String text;
    
    @Column( name = "buffcategory" )
    private String buffcategory;
    
    @Column( name = "frequency" )
    private Integer frequency;
    
}
