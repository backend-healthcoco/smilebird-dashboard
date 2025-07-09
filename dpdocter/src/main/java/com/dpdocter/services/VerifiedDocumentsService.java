package com.dpdocter.services;

import com.dpdocter.beans.VerifiedDocuments;
import com.dpdocter.request.VerifiedDocumentRequest;
import com.sun.jersey.multipart.FormDataBodyPart;

public interface VerifiedDocumentsService {
	public VerifiedDocuments addDocumentsMultipart(FormDataBodyPart file, VerifiedDocumentRequest request);

	public Boolean updateVerified(String documentId, Boolean isVerified);
}
