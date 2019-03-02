package logic.item.level;

import logic.item.Item;

public interface Level {
	public String type = Level.class.getName();
	public boolean isNotMax();
	
	public boolean isUpperOrEquals(Item item);
	
	public int compareTo(Item item);
	
	public int getIndex();
}
