package com.edspread.meeting.DTO;

import java.io.Serializable;
import java.util.List;

public class Page implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int pageNumber;
	List<GraphicsObject> graphicsObject;
	
	public int getPageNumber() {
		return pageNumber;
	}
	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}
	public List<GraphicsObject> getGraphicsObject() {
		return graphicsObject;
	}
	public void setGraphicsObject(List<GraphicsObject> graphicsObject) {
		this.graphicsObject = graphicsObject;
	}

}
