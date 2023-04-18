package com.example.rpgdata.entity;

import javax.annotation.processing.Generated;

import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;

@Generated( value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor" )
@StaticMetamodel( Magic.class )
public class Magic_ {
	
	public static volatile SingularAttribute<Magic, Integer> id;
	public static volatile SingularAttribute<Magic, String> name;
	public static volatile SingularAttribute<Magic, String> category;
	public static volatile SingularAttribute<Magic, String> range;
	public static volatile SingularAttribute<Magic, String> buffcategory;
	
	public static final String ID   = "id";
	public static final String NAME = "name";
	public static final String CATEGORY = "category";
	public static final String RANGE = "range";
	public static final String BUFFCATEGORY = "buffcategory";
	
}
