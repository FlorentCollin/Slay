import java.util.concurrent.CopyOnWriteArrayList;

import logic.board.Board;
import logic.board.District;
import logic.item.Capital;
import logic.item.Soldier;
import logic.item.Tree;
import logic.item.level.SoldierLevel;
import logic.player.Player;
import logic.player.ai.AI;
import logic.player.ai.strategy.RandomStrategy;
import logic.player.ai.strategy.Strategy;
import logic.shop.Shop;

public class InitStrategy {
	private AI ai;
	private Player enemy;
	private District district, enemyDistrict;
	private Board board;
	
	public InitStrategy(Strategy strategy) {
		Player player = new Player();
		enemy = new Player();
		CopyOnWriteArrayList<Player> players = new CopyOnWriteArrayList<>();
		players.add(player);
		players.add(enemy);
		district = new District(player);
		board = new Board(5, 5, players, false, new Shop());
		System.out.println(board.getPlayers().size());
		for(int i=0; i<3; i++) {
			for(int j=0; j<3; j++) {
				board.getCell(i, j).setDistrict(district);
				district.addCell(board.getCell(i, j));
			}
		}
		board.changeToAI(0, strategy);
		ai = (AI) board.getPlayers().get(0);
		board.getCell(0, 0).setItem(new Capital());
		board.getCell(2, 1).setItem(new Tree());
		board.getCell(1, 2).setItem(new Soldier(SoldierLevel.level1));
		board.getCell(2, 2).setItem(new Soldier(SoldierLevel.level1));
		board.getCell(1, 1).setItem(new Soldier(SoldierLevel.level1));
		board.getCell(0, 1).setItem(new Soldier(SoldierLevel.level1));
	}
	
	public void initEnemy() {
		enemy = new Player();
		board.getPlayers().add(enemy);
		enemyDistrict = new District(enemy);
		for(int i = 0; i < 3; i++) {
			for(int j = 3; j < 5; j++) {
				board.getCell(i, j).setDistrict(enemyDistrict);
				enemyDistrict.addCell(board.getCell(i, j));
			}
		}
		board.getCell(1, 4).setItem(new Capital());
		board.getCell(1, 3).setItem(new Soldier(SoldierLevel.level1));
	}
	
	public AI getAI() {
		return ai;
	}
	
	public District getDistrict() {
		return district;
	}
	
	public Board getBoard() {
		return board;
	}
	

}
