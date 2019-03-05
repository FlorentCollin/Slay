package logic.board;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import logic.board.cell.Cell;
import logic.item.Capital;
import logic.item.Tomb;
import logic.item.Item;
import logic.item.Soldier;
import logic.item.Tree;
import logic.player.Player;

public class District {
	private Player player;
	private int gold;
	private transient Cell capital;
	private volatile List<Cell> cells;

	public static int globalId = 0;
	private int id;
	
	public District(Player player) {
		cells = Collections.synchronizedList(new ArrayList<>());
		this.player = player;

		globalId++;
		id = globalId;
	}
	
	public void addCell(Cell cell) {
		if(cells.indexOf(cell) == -1) {
			cells.add(cell);
		}
	}

	//TODO supprimer cette méthode
	/**
	 * Permet d'ajouter au district toutes les cellules d'un autre district
	 * @param district le district dont on souhaite obtenir les cellules
	 * */
	public void addAllCell(District district) {
		cells.addAll(district.getCells());
	}
	
	public void removeCell(Cell cell) {
		cells.remove(cells.indexOf(cell));
	}
	
	public void remove() {
		for(Cell c : cells) {
			if(c.getItem() instanceof Soldier) {
				c.setItem(new Tomb());
			}
		}
	}
	
	public void addCapital(Cell cell) {
		if(cells.indexOf(cell) >= 0) { // On vérifie que la cellule appartient bien au district
			cell.setItem(new Capital());
			capital = cell;
		}
	}
	
	public void removeCapital() {
		if(capital != null) {
			capital.removeItem();
		}
	}
	
	public void setPlayer(Player player) {
		this.player = player;
	}
	
	public Player getPlayer() {
		return this.player;
	}
	
	/**
	 * Permet de calculer le revenu du district
	 * @return le revenu du district
	 * */
	public void calculateGold() {
		Item item;
		for(Cell cell : cells) {
			item = cell.getItem();
			setGold(getGold() + 1);
			if(item instanceof Soldier) {
				setGold(getGold() - ((Soldier) item).getLevel().getSalary());
			}
			else if(item instanceof Tree) {
				setGold(getGold() - 1);
			}
		}
	}
	
	public List<Cell> getCells() {
		return this.cells;
	}

	public int getGold() {
		return gold;
	}

	public void setGold(int gold) {
		this.gold = gold;
	}
	
	public void addGold(int gold) {
		this.gold += gold;
	}
	
	public Cell getCapital() {
		return capital;
	}

    public int getId() {
        return id;
    }
}
