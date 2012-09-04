package io.emmet;


public class TabStop {
	private int start = 0;
	private int end = 0;
	
	public TabStop(int start, int end) {
		this.start = start;
		this.end = end;
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
	
	public boolean isZeroWidth() {
		return start == end;
	}

	public int getLength() {
		return end - start;
	}
}
