package primary;

import java.util.ArrayList;

import aiModels.AIModel.PolicyMove;

public class Constants {
	public static final String fileDelimiter = System.getProperty("file.separator");
	public static final String newline = System.getProperty("line.separator");
	public static final int baseImageSize = 32;

	public static PolicyMove getMoveDirectionToRight(PolicyMove targetMove) {
		if (targetMove == PolicyMove.UP)
			return PolicyMove.UPRIGHT;
		if (targetMove == PolicyMove.UPRIGHT)
			return PolicyMove.RIGHT;
		if (targetMove == PolicyMove.RIGHT)
			return PolicyMove.DOWNRIGHT;
		if (targetMove == PolicyMove.DOWNRIGHT)
			return PolicyMove.DOWN;
		if (targetMove == PolicyMove.DOWN)
			return PolicyMove.DOWNLEFT;
		if (targetMove == PolicyMove.DOWNLEFT)
			return PolicyMove.LEFT;
		if (targetMove == PolicyMove.LEFT)
			return PolicyMove.UPLEFT;
		if (targetMove == PolicyMove.UPLEFT)
			return PolicyMove.UP;
		
		return PolicyMove.UNKNOWN;
	}
	
	public static Point outcomeOfMove(PolicyMove moveDirection, Point sourcePt) {
		if (moveDirection == PolicyMove.UP)
			return new Point(sourcePt.x, sourcePt.y-1);
		if (moveDirection == PolicyMove.UPRIGHT)
			return new Point(sourcePt.x+1, sourcePt.y-1);
		if (moveDirection == PolicyMove.RIGHT)
			return new Point(sourcePt.x+1, sourcePt.y);
		if (moveDirection == PolicyMove.DOWNRIGHT)
			return new Point(sourcePt.x+1, sourcePt.y+1);
		if (moveDirection == PolicyMove.DOWN)
			return new Point(sourcePt.x, sourcePt.y+1);
		if (moveDirection == PolicyMove.DOWNLEFT)
			return new Point(sourcePt.x-1, sourcePt.y+1);
		if (moveDirection == PolicyMove.LEFT)
			return new Point(sourcePt.x-1, sourcePt.y);
		if (moveDirection == PolicyMove.UPLEFT)
			return new Point(sourcePt.x-1, sourcePt.y-1);
		if (moveDirection == PolicyMove.NOWHERE)
			return new Point(sourcePt);
		
		return new Point(sourcePt);
		
	}
}
