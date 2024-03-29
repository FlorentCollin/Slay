package ac.umons.slay.g01.logic.board.cell;

import com.google.gson.annotations.SerializedName;

import ac.umons.slay.g01.logic.board.District;
import ac.umons.slay.g01.logic.item.Item;

/**
 * Classe représentant une cellule du Board. Une cellule est un hexagone dans le jeu
 */
public class Cell {
	private int x,y;
	@SerializedName("i")
	private Item item; // si null alors il n'y a pas d'item actuellement sur la cellule
	@SerializedName("d")
	private transient District district; // si null alors n'appartient actuellement à aucun district

	protected String t = this.getClass().getName();
	@SerializedName("a")
	protected boolean accessible;

	public Cell(int x, int y) {
		this.x = x;
		this.y = y;
		accessible = true;
	}
	
	public void setItem(Item item) {
		this.item = item;
	}
	
	public Item getItem() {
		return this.item;
	}
	
	public void setDistrict(District district) {
		this.district = district;
	}
	
	public District getDistrict() {
		return this.district;
	}
	
	public void removeDistrict() {
		district = null;
	}
	
	public void removeItem() {
		item = null;
	}
	
	public boolean isAccessible() {
		return accessible;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	/**
	 * Méthode qui retourne l'ID d'une cellule, l'ID est sa position en x et y dans le board
	 * Note : Cet ID est unique
	 * @return l'ID de la cellule
	 */
	public Integer[] getId() {
	    return new Integer[] {x, y};
    }

	@Override
	public boolean equals(Object obj) {
		if(this == obj) {
			return true;
		}
		if(obj == null) {
			return false;
		}
		if(!(obj instanceof Cell)) {
			return false;
		}
		Cell other = (Cell) obj;
		return this.x == other.getX() && this.y == other.getY();
	}
}
