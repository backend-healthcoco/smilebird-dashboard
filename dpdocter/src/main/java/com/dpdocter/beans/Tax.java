package com.dpdocter.beans;

import com.dpdocter.enums.UnitType;

public class Tax {

	private Double value = 0.0;
	
	private String taxType ;
	
	private UnitType unit;
	
	public String getTaxType() {
		return taxType;
	}

	public void setTaxType(String taxType) {
		this.taxType = taxType;
	}

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}

	public UnitType getUnit() {
		return unit;
	}

	public void setUnit(UnitType unit) {
		this.unit = unit;
	}

	@Override
	public String toString() {
		return "Tax [value=" + value + ", taxType=" + taxType + ", unit=" + unit + "]";
	}

		
	
}
