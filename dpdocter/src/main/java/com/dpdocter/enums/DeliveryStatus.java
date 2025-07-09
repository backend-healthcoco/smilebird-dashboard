package com.dpdocter.enums;

public enum DeliveryStatus {
	PENDING("PENDING"), PLACED("PLACED"), SCHEDULED("SCHEDULED"), PICKED_UP("PICKED_UP"), DISPATCHED("DISPATCHED"),
	PACKED("PACKED"), OUT_FOR_DELIVERY("OUT_FOR_DELIVERY"), DELIVERED("DELIVERED"), CONFIRMED("CONFIRMED");

	private String type;

	public String getType() {
		return type;
	}

	private DeliveryStatus(String type) {
		this.type = type;
	}
}
