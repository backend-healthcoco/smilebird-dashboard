package com.dpdocter.enums;

public enum DeliveryPreferences {

	ONE_TIME("ONE_TIME"),SEVEN_DAYS("SEVEN_DAYS"),FIFTEEN_DAYS("FIFTEEN_DAYS"), THIRTY_DAYS("THIRTY_DAYS"), FOURTY_FIVE_DAYS("FOURTY_FIVE_DAYS"), SIXTY_DAYS("SIXTY_DAYS");

	private String type;

	public String getType() {
		return type;
	}

	private DeliveryPreferences(String type) {
		this.type = type;
	}

}
