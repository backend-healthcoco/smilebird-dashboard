package com.dpdocter.beans;

import org.bson.types.ObjectId;

public class TestAndRecordData {

    private ObjectId testId;

    private ObjectId recordId;

    public TestAndRecordData(ObjectId testId, ObjectId recordId) {
		this.testId = testId;
		this.recordId = recordId;
	}

	public ObjectId getTestId() {
		return testId;
	}


	public void setTestId(ObjectId testId) {
		this.testId = testId;
	}


	public ObjectId getRecordId() {
	return recordId;
    }

    public void setRecordId(ObjectId recordId) {
	this.recordId = recordId;
    }

	@Override
	public String toString() {
		return "TestAndRecordData [testId=" + testId + ", recordId=" + recordId + "]";
	}
}
