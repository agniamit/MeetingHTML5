package com.edspread.meeting.action;

import java.io.File;
import java.util.UUID;

import org.apache.log4j.Logger;


import com.opensymphony.xwork2.ActionSupport;

public class UploadAction extends ActionSupport {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	private File[] attachment;

	private Exception exception;
	
	private String[] attachmentFileName;
	
	private String attachmentContentType;
	
	private String uploaddedFilesNameOnServer ;
	
	private String nameOfFileToBeRemoved;
		

	public String getUploaddedFilesNameOnServer() {
		return uploaddedFilesNameOnServer;
	}




	public void setUploaddedFilesNameOnServer(String uploaddedFilesNameOnServer) {
		this.uploaddedFilesNameOnServer = uploaddedFilesNameOnServer;
	}

	private static final Logger log = Logger.getLogger(UploadAction.class);
	
		

	public File[] getAttachment() {
		return attachment;
	}




	public void setAttachment(File[] attachment) {
		this.attachment = attachment;
	}




	public Exception getException() {
		return exception;
	}




	public void setException(Exception exception) {
		this.exception = exception;
	}




	public String[] getAttachmentFileName() {
		return attachmentFileName;
	}




	public void setAttachmentFileName(String[] attachmentFileName) {
		this.attachmentFileName = attachmentFileName;
	}




	public String getAttachmentContentType() {
		return attachmentContentType;
	}




	public void setAttachmentContentType(String attachmentContentType) {
		this.attachmentContentType = attachmentContentType;
	}

	


	public String getNameOfFileToBeRemoved() {
		return nameOfFileToBeRemoved;
	}




	public void setNameOfFileToBeRemoved(String nameOfFileToBeRemoved) {
		this.nameOfFileToBeRemoved = nameOfFileToBeRemoved;
	}




	private String uploadFile(String type) {
		try {
			if (type.equals("Temp")) {
				for(int i=0;i<attachment.length;i++){
					if(attachment[i]!=null && attachment[i].exists()){
						
						if(attachment[i] != null){
							int pos = attachmentFileName[i].lastIndexOf(".");
							String fileName = attachmentFileName[i].substring(0, pos);
							String ext = attachmentFileName[i].substring(pos + 1,
									attachmentFileName[i].length());
							
							if (attachment[i] != null) {
								String key = "";
								key = UUID.randomUUID() + "_" + fileName + "." + ext;
								
								//uploaddedFilesNameOnServer = Constants.TEMP + "/" + key;
							} 
						}
						
					}
				}
			}
		} catch (Exception e) {
			if(e.getMessage() != null && e.getMessage().length() > 0){
				// get logs
				log.error("[ERROR:] "+e.getMessage());
			}
		}
		return "success";
	}


	public String uploadTempFile() throws Exception {
				return uploadFile("Temp");
	}
	public String deleteTempFile(){
		try {
			if(nameOfFileToBeRemoved==null){
				addActionError("Invalid file name.");
				return "error";
			}else{
				//Thread delThread = new Thread(new DeleteTempraryFiles());				delThread.start();
			}
		}catch (Exception ex) {
			log.error("[ERROR:] "+ex.getMessage());
			addActionError("File could not be removed. Please try again.");
			return "error";
		}
		return "success";
	}
	
}