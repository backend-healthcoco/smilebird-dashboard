package com.dpdocter.enums;

public enum DisplayTypeThyrocare {
	
	ALL("ALL"), POP("POP"), TESTS("TESTS"),PROFILE("PROFILE"),OFFER("OFFER");
	
	private String display_type;

	public String getDisplay_type() {
		return display_type;
	}

	private DisplayTypeThyrocare(String display_type) {
		this.display_type = display_type;
	}
	
	

}
