package com.dpdocter.beans;

import java.util.List;

import com.dpdocter.collections.GenericCollection;

public class Intgredient extends GenericCollection {

	private String id;

	private String name;

	private List<IngredientAddItem> ingredients;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<IngredientAddItem> getIngredients() {
		return ingredients;
	}

	public void setIngredients(List<IngredientAddItem> ingredients) {
		this.ingredients = ingredients;
	}

}
