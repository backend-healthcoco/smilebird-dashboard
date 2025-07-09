package com.dpdocter.enums;

public enum PrescriptionItems {

    DRUGS("DRUGS"), DRUGTYPE("DRUGTYPE"), DRUGDIRECTION("DRUGDIRECTION"), DRUGDOSAGE("DRUGDOSAGE"), DRUGDURATIONUNIT("DRUGDURATIONUNIT"), DRUGSTRENGTHUNIT(
	    "DRUGSTRENGTHUNIT"), LABTEST("LABTEST"), DIAGNOSTICTEST("DIAGNOSTICTEST"), DRUGBYGCODE("DRUGBYGCODE"), DRUGBYICODE("DRUGBYICODE"),ADVICE("ADVICE"),
    GCODE("GCODE"),GCODEWITHREACTION("GCODEWITHREACTION");

    private String item;

    PrescriptionItems(String item) {
	this.item = item;
    }

    public String getItem() {
	return item;
    }

}
