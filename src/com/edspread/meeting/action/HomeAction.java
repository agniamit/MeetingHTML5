package com.edspread.meeting.action;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.FileUtils;
import org.apache.struts2.ServletActionContext;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.edspread.meeting.constants.MeetingConstant;
import com.edspread.meeting.util.SessionUtil;
import com.opensymphony.xwork2.ActionSupport;

public class HomeAction extends ActionSupport  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int pageCount;
	private String imageUrl;
	private String meetingName;
	private String jsonContent;
	private List<String> meetings;
	private boolean status;
	private String fileName;
	private String oldFileName;
	private boolean isSaved;
	
	 public String execute() throws Exception {
	         return SUCCESS;
	    }
	 
	 public String getAllPage(){
		 try {
			String googleDocViewerUrl = "https://docs.google.com/viewer";
			String serverUrl = "http://www.topchalks.com/tc/";
			String requestUrl = googleDocViewerUrl + "?url="+serverUrl+fileName+"&a=gt";
        	URL url = new URL(requestUrl.toString());
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            String inputLine;
            StringBuilder recievedXML = new StringBuilder();
            System.out.println("-----RESPONSE START-----");
            while ((inputLine = in.readLine()) != null) {
                System.out.println(inputLine);
                recievedXML.append(inputLine);
            }
            in.close();
            
            DocumentBuilderFactory dbf =
                DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(recievedXML.toString()));

            Document doc = db.parse(is);
            NodeList pageNum = doc.getElementsByTagName("page");
            imageUrl = serverUrl +fileName ;
            pageCount = pageNum.getLength();
            System.out.println("-----RESPONSE END-----");
		} catch (Exception e) {
			e.printStackTrace();
			addActionError("Error Occured");
		}
		 return SUCCESS; 
	 }
	 
	 
	 public String saveMeetingdata(){
		 try {
			HttpServletRequest request = ServletActionContext.getRequest();
			StringBuffer jb = new StringBuffer();
			String line = null;
			try {
				BufferedReader reader = request.getReader();
				while ((line = reader.readLine()) != null)
					jb.append(line);
			} catch (Exception e) {
				e.printStackTrace();
			}
						
			String CONTEXT_PATH = SessionUtil.getServerDeploymentPath();
			Map<String,String> meetingData = getMeetingData(jb.toString());
			meetingName =  meetingData.get(MeetingConstant.MEETINGNAME);
			if( meetingName != null){
				status = writeJSONFile(CONTEXT_PATH,meetingName,jb.toString());
			}else{
				addActionError("Error Occured while saving");
				return ERROR;
			}
			if(meetingData.get(MeetingConstant.RECFILENAME) != null && meetingData.get(MeetingConstant.RECFILENAME).trim().length()>0){
				System.out.println("--------------copy recording file--------------");
				String srcPath = CONTEXT_PATH + File.separator + meetingData.get(MeetingConstant.RECFILENAME);
				String recDirPath =  CONTEXT_PATH + File.separator +  MeetingConstant.ENOTEBOOK +  File.separator + meetingName + File.separator + MeetingConstant.RECORDING ;
				File fl = new File(recDirPath);
				if(!fl.exists()){
					fl.mkdirs();
					String destPath = recDirPath + File.separator + meetingName + MeetingConstant.RECORDINGFILEFORMAT;
					if(!copyFile(srcPath,destPath)){
						addActionError("Error Occured while saving recording");
						return ERROR;
					}
				}
				System.out.println("srcFile"+srcPath);
				String destPath = CONTEXT_PATH + File.separator +  MeetingConstant.ENOTEBOOK +  File.separator + meetingName + File.separator + meetingName + MeetingConstant.RECORDINGFILEFORMAT;
				System.out.println("destFile"+destPath);
				if(!copyFile(srcPath,destPath)){
					addActionError("Error Occured while saving recording");
					return ERROR;
				}
			}
			 
		} catch (Exception e) {
			e.printStackTrace();
			addActionError("Error Occured");
			return ERROR;
		}
		 return SUCCESS; 
	 }
	 
	 
public String deleteExistingRecordingFile(){
	String CONTEXT_PATH = SessionUtil.getServerDeploymentPath();
	String destPath = CONTEXT_PATH + File.separator +  MeetingConstant.ENOTEBOOK +  File.separator + meetingName + File.separator + MeetingConstant.RECORDING + File.separator + meetingName + MeetingConstant.RECORDINGFILEFORMAT;
	File file = new File(destPath);
	if(file.exists()){
		try {
			FileUtils.deleteQuietly(file);
		} catch (Exception e) {
			e.printStackTrace();
			addActionError("Error occured while deleting file");
			return ERROR;
		}
		
	}
	return SUCCESS;
}
public String setRecordingEnv() {
	System.out.println("status::::"+status);
	String CONTEXT_PATH = SessionUtil.getServerDeploymentPath();
	String destPath = CONTEXT_PATH + File.separator +  MeetingConstant.ENOTEBOOK +  File.separator + meetingName + File.separator + MeetingConstant.RECORDING;
	File file = new File(destPath);
	if(file.exists() && file.isDirectory()){
		File[] fl=file.listFiles(); 
		for(int i=0;i<fl.length;i++){
			try {
				if(!FileUtils.deleteQuietly(fl[i])){
					addActionError("Error occured while deleting file");
					return ERROR;
				}
			} catch (Exception e) {
				e.printStackTrace();
				addActionError("Error occured while deleting file");
				return ERROR;
				
			}
			
		}
		
		destPath  = file.getAbsolutePath()+ File.separator + meetingName + MeetingConstant.RECORDINGFILEFORMAT;
		String srcPath = CONTEXT_PATH + File.separator +  MeetingConstant.ENOTEBOOK +  File.separator + meetingName + File.separator + meetingName + MeetingConstant.RECORDINGFILEFORMAT;
			if(!copyFile(srcPath,destPath)){
				addActionError("Error occured while copying file");
				return ERROR;
			}
		
	}
	status = true;
	return SUCCESS; 
}

public String copyRecordingFile(){
	String CONTEXT_PATH = SessionUtil.getServerDeploymentPath();
	String destPath =  CONTEXT_PATH + File.separator + fileName;
	String srcPath = CONTEXT_PATH + File.separator + oldFileName;
	if(!copyFile(srcPath,destPath)){
		addActionError("Error occured while copying file");
		return ERROR;
	}
	return SUCCESS; 
}



public boolean copyFile(String srcPath,String destPath) {
	try {
		File srcFile =  new File(srcPath);
		System.out.println("srcFile"+srcFile.getAbsolutePath());
		File destFile = new File(destPath);
		System.out.println("destFile"+destFile.getAbsolutePath());
		FileUtils.copyFile(srcFile, destFile);
	} catch (IOException e) {
		e.printStackTrace();
		return false;
	}
	
	return true;
}	

public Map<String,String> getMeetingData(String jsonContent) {
	Map<String,String> fileList = new HashMap<String,String>();
	JSONParser parser = new JSONParser();
	
	Object obj;
	try {
		obj = parser.parse(jsonContent);
	    JSONObject jsonObject = (JSONObject) obj;
		if(jsonObject.get(MeetingConstant.MEETING)!= null){
			fileList.put(MeetingConstant.MEETINGNAME,jsonObject.get(MeetingConstant.MEETING).toString());
		}
		if(jsonObject.get(MeetingConstant.TEMPRECFILENAME)!= null){
			fileList.put(MeetingConstant.RECFILENAME,jsonObject.get(MeetingConstant.TEMPRECFILENAME).toString());
		}
	} catch (ParseException e) {
		e.printStackTrace();
	}
	System.out.println("fileList:::"+fileList);
	return fileList;
}
	 
public boolean writeJSONFile(String path,String meetingName,String content) {
	File file = new File(path + File.separator +  MeetingConstant.ENOTEBOOK +  File.separator + meetingName);
	// if dir doesn't exists, then create it
	if (!file.exists()) {
			file.mkdirs();
	}
	System.out.println("Path:::"+file.getAbsolutePath());
	file = new File(file.getAbsolutePath()+File.separator + meetingName + MeetingConstant.JSONFORMAT);
	FileWriter fw;
	try {
		fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(content);
		bw.close();
	} catch (IOException e) {
		e.printStackTrace();
		return false;
	}
	return true;

}	

public String getallMeetingName(){
	String CONTEXT_PATH = SessionUtil.getServerDeploymentPath();
	File file = new File(CONTEXT_PATH + File.separator +  MeetingConstant.ENOTEBOOK + File.separator);
	String[] fileArray = file.list();
	if(fileArray != null){
		meetings =  new ArrayList<String>();
		for (int i = 0; i < fileArray.length; i++) {
			meetings.add(fileArray[i]);
		}
	}
	return SUCCESS;
}

public String getmeetingjson(){
	String CONTEXT_PATH = SessionUtil.getServerDeploymentPath();
	System.out.println("CONTEXT_PATH::"+CONTEXT_PATH);
	File fl = new File(CONTEXT_PATH);
	File file = new File(CONTEXT_PATH + File.separator +  MeetingConstant.ENOTEBOOK +  File.separator + meetingName +  File.separator + meetingName  + MeetingConstant.JSONFORMAT);
	System.out.println("Absolute Path123::"+fl.getParent()+"::PArent PAth"+fl.getParentFile().getAbsolutePath());
	if (!file.exists()) {
		addActionError("Meeting Not Found");
		return ERROR;
	}else{
			BufferedReader in = null; 
			try {
				in = new BufferedReader(new FileReader(file));
				String line;
				StringBuilder recievedXML = new StringBuilder();
				while((line = in.readLine()) != null) {
					recievedXML.append(line);
				}
				jsonContent = recievedXML.toString();
				if(setRecordingEnv().equals(ERROR)){
					addActionError("Error occured while copying recording file.");
					return ERROR;
				}
				/*HttpServletResponse response = ServletActionContext.getResponse();
				response.setContentType("application/json");
			    response.getWriter().write(jsonContent );*/
			System.out.println("-----JSON Content-----"+jsonContent);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				if(in != null){
						try {
							in.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
				}
			}
        
		
	}
	return SUCCESS; 
}

	public int getPageCount() {
		return pageCount;
	}
	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getMeetingName() {
		return meetingName;
	}

	public void setMeetingName(String meetingName) {
		this.meetingName = meetingName;
	}
	
	public String getJsonContent() {
		return jsonContent;
	}

	public void setJsonContent(String jsonContent) {
		this.jsonContent = jsonContent;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public List<String> getMeetings() {
		return meetings;
	}

	public void setMeetings(List<String> meetings) {
		this.meetings = meetings;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public boolean isSaved() {
		return isSaved;
	}

	public void setSaved(boolean isSaved) {
		this.isSaved = isSaved;
	}

	public String getOldFileName() {
		return oldFileName;
	}

	public void setOldFileName(String oldFileName) {
		this.oldFileName = oldFileName;
	}

	

	
}
