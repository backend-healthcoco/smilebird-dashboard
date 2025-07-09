package com.dpdocter.beans;

public class DoctorOnlineConsultationAddEditRequest {

	
	 	private String id;

	    private String doctorId;

	  

	    private AppointmentSlot onlineConsultationSlot;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getDoctorId() {
			return doctorId;
		}

		public void setDoctorId(String doctorId) {
			this.doctorId = doctorId;
		}

		

		public AppointmentSlot getOnlineConsultationSlot() {
			return onlineConsultationSlot;
		}

		public void setOnlineConsultationSlot(AppointmentSlot onlineConsultationSlot) {
			this.onlineConsultationSlot = onlineConsultationSlot;
		}
	    
	    
}
