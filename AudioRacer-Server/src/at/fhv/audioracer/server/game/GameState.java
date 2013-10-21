package at.fhv.audioracer.server.game;

public class GameState {
	public static int MAP_CONFIGURATION 	= 0x1;
	public static int CAR_DETECTION 		= 0x2;
	public static int ALL_PLAYERS_READY 	= 0x4;
	public static final int GAME_STARTED 	= 0x8;
	public static final int GAME_OVER 		= 0x10;
}
