package io.emmet;


public class SelectionData {
	private int start = 0;
	private int end = 0;
	
	public SelectionData() {
		
	}
	
	public SelectionData(int start, int end) {
		updateRange(start, end);
	}
	
	public void setStart(int start) {
		this.start = start;
	}
	
	public int getStart() {
		return start;
	}
	
	public void setEnd(int end) {
		this.end = end;
	}
	
	public int getEnd() {
		return end;
	}
	
	/**
	 * Updates selection's start and end indexes
	 */
	public void updateRange(int start, int end) {
		this.start = Math.min(start, end);
		this.end = Math.max(start, end);
	}
	
	/**
	 * Updates selection ranges by passing start offset and selection length
	 * (commonly used notation in most editors)
	 */
	public void updateRangeWithLength(int start, int length) {
		int end = start + length;
		updateRange(start, end);
	}
	
	/**
	 * Returns selection length
	 */
	public int getLength() {
		return end - start;
	}
	
	public String toString() {
		return "selection start: " + start + ", end: " + end;
	}
}
