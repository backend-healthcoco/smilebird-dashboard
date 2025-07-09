package com.dpdocter.enums;

public enum RtpcrStatus {

	POSITIVE("POSITIVE"),NEGATIVE("NEGATIVE");
	
	 private String status;

		private RtpcrStatus(String status) {
			this.status = status;
		}

		public String getStatus() {
			return status;
		}
}
