package io.emmet;


import java.util.ArrayList;

public class TabStopGroup {
	private ArrayList<TabStop> list;
	
	public TabStopGroup() {
		 list = new ArrayList<TabStop>();
	}
	
	public void addTabStop(int start, int end) {
		list.add(new TabStop(start, end));
	}
	
	public void addTabStop(TabStop tabStop) {
		list.add(tabStop);
	}
	
	public ArrayList<TabStop> getTabStopList() {
		return list;
	}
	
	public int getLength() {
		return list.size();
	}
}
