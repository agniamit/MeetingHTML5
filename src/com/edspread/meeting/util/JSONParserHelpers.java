package com.edspread.meeting.util;

import java.io.BufferedReader;


import javax.servlet.http.HttpServletRequest;
import org.apache.struts2.ServletActionContext;
import com.edspread.meeting.DTO.MeetingDTO;
import com.google.gson.Gson;


public class JSONParserHelpers {

	public static MeetingDTO getRequestFromJson(String content) { 
		MeetingDTO request =null;
		try {
			request = new Gson().fromJson(content, MeetingDTO.class);
			} 
		catch (Exception e) {
			e.printStackTrace();
		}
		 return request;
	}
	
	public static String getRequestFromJson(Object meetingDTO) { 
		String json =null;
		try {
			json = new Gson().toJson(meetingDTO);
			} 
		catch (Exception e) {
			e.printStackTrace();
		}
		 return json;
	}
	
	public static String  getRequestFromHeader() throws Exception {
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
		return jb.toString();
	}
}
