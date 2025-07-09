package com.dpdocter.enums;

public enum ClinicalItems {

	COMPLAINTS("COMPLAINTS"), INVESTIGATIONS("INVESTIGATIONS"), OBSERVATIONS("OBSERVATIONS"), DIAGNOSIS(
			"DIAGNOSIS"), NOTES("NOTES"), DIAGRAMS(
					"DIAGRAMS"), PRESENT_COMPLAINT("PRESENT_COMPLAINT"), PROVISIONAL_DIAGNOSIS(
							"PROVISIONAL_DIAGNOSIS"), GENERAL_EXAMINATION("GENERAL_EXAMINATION"), SYSTEMIC_EXAMINATION(
									"SYSTEMIC_EXAMINATION"), HISTORY_OF_PRESENT_COMPLAINT(
											"HISTORY_OF_PRESENT_COMPLAINT"), MENSTRUAL_HISTORY(
													"MENSTRUAL_HISTORY"), OBSTETRIC_HISTORY(
															"OBSTETRIC_HISTORY"), INDICATION_OF_USG(
																	"INDICATION_OF_USG"), PA("PA"), PV("PV"), PS(
																			"PS"), ECG("ECG"), XRAY("XRAY"), ECHO(
																					"ECHO"), HOLTER(
																							"HOLTER"), PROCEDURE_NOTE(
																									"PROCEDURE_NOTE"), PC_NOSE(
																											"PC_NOSE"), PC_EARS(
																													"PC_EARS"), PC_THROAT(
																															"PC_THROAT"), PC_ORAL_CAVITY(
																																	"PC_ORAL_CAVITY"), NOSE_EXAM(
																																			"NOSE_EXAM"), NECK_EXAM(
																																					"NECK_EXAM"), EARS_EXAM(
																																							"EARS_EXAM"), ORAL_CAVITY_THROAT_EXAM(
																																									"ORAL_CAVITY_THROAT_EXAM"), INDIRECT_LAGYROSCOPY_EXAM(
																																											"INDIRECT_LAGYROSCOPY_EXAM");
	;

	private String type;

	ClinicalItems(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

}
