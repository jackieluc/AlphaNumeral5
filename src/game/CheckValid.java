package game;

import java.awt.event.KeyEvent;

public class CheckValid {
	Map map;
	public static int width;
	public static int height;
	
	public  CheckValid(){
		map = new Map();
	}
	

	public static boolean checkx (int playerPosition, KeyEvent e)
	{	Map map = new Map();
		
		width= map.getWidth();
		height= map.getHeight();
		int right = 39;
		int left = 37;
		
		if (playerPosition > width-3 && e.getKeyCode() == right){
			System.err.println("inside if");
			return false;
			}
		else if (playerPosition == 1 && e.getKeyCode() == left){
			System.err.println("inside else");
			return false;
		}
		return true;
	}
	
	public static boolean checky (int playerPosition, KeyEvent e)
	{	Map map = new Map();
		
		width= map.getWidth();
		height= map.getHeight();
		int up = 38;
		int down = 40;
		if (playerPosition > height-3 && e.getKeyCode() == down)
			return false;
		else if (playerPosition == 1 && e.getKeyCode() == up)
			return false;
		return true;
	}
}
