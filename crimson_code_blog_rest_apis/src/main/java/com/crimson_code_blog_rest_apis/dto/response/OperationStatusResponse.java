package com.crimson_code_blog_rest_apis.dto.response;

public class OperationStatusResponse {
	
	private String operationName;
	private String operationStatus;
	private String message;
	
	public OperationStatusResponse() {
		
	}
	
	public OperationStatusResponse(String operationName, String operationStatus, String message) {
		this.operationName = operationName;
		this.operationStatus = operationStatus;
		this.message = message;
	}



	public String getOperationName() {
		return operationName;
	}

	public void setOperationName(String operationName) {
		this.operationName = operationName;
	}

	public String getOperationStatus() {
		return operationStatus;
	}

	public void setOperationStatus(String operationStatus) {
		this.operationStatus = operationStatus;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
}