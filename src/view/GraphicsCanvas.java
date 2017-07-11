package view;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Toolkit;
import java.awt.geom.Line2D;
import java.awt.image.BufferStrategy;

import primary.Constants;
import aiModels.AIModel.PolicyMove;


/*
 * Manages the drawing of the graphics screen
 */
public class GraphicsCanvas extends Canvas {
	private static final long serialVersionUID = 1L; //required by parent
	private int height;
	private int width;
	private BufferStrategy myBufferStrategy;
	
	public GraphicsCanvas(int newLeft, int newTop, int newWidth, int newHeight) {
		super();
		height = newHeight;
		width = newWidth;
		setBounds(newLeft, newTop, newWidth, newHeight);
	}

	public void initialize() {
		this.createBufferStrategy(2);
		myBufferStrategy = this.getBufferStrategy();
	}

	public void render(PrintListNode[][] printList) {
		if (printList == null)
			return;
		
		Graphics2D bkG = (Graphics2D) myBufferStrategy.getDrawGraphics();
		bkG.setFont(new Font( "SansSerif", Font.BOLD, 9));

		bkG.setPaint(bkG.getBackground());
		bkG.fillRect(0, 0, getWidth(), getHeight());

		if (printList != null) {
			for (int y = 0; (y < printList.length) && (y*Constants.baseImageSize < height); y++) {
				for (int x = 0; (x < printList[y].length) && (x*Constants.baseImageSize < width); x++) {
					if (printList[y][x] != null) {
						if (printList[y][x].overrideColor){
							bkG.setXORMode(printList[y][x].baseColor);
							bkG.drawImage(printList[y][x].myImage,  x*Constants.baseImageSize, y*Constants.baseImageSize, null);
							bkG.setPaintMode();
						}
						else
							bkG.drawImage(printList[y][x].myImage,  x*Constants.baseImageSize, y*Constants.baseImageSize, null);
						
						/* 
						 * TODO this is a hack, there should be no isHidden on the print list.  This has been created because
						 * the BasicUtility AI model I created could not handle an unknown board.  Realistically I should be able
						 * to display a policy and utility for a GameObject not visible yet. 
						 */
						
//						if (!printList[y][x].isHidden) {
							if (printList[y][x].hasUtilityValue) {
								bkG.setColor(Color.LIGHT_GRAY);
								bkG.drawString(String.valueOf(printList[y][x].utilityValue), x*Constants.baseImageSize+12, y*Constants.baseImageSize+24);
							}
							else if (printList[y][x].hasPolicyMove) {
								drawArrow(bkG, printList[y][x].myPolicyMove, x, y);
							}
							else if (printList[y][x].hasInformationZone) {
								bkG.setFont(new Font( "SansSerif", Font.BOLD, 7));
								bkG.setColor(Color.MAGENTA);
								if (printList[y][x].isBreezy)
									bkG.drawString("Breezy", x*Constants.baseImageSize+2, y*Constants.baseImageSize+12);
								if (printList[y][x].isPungent)
									bkG.drawString("Pungent", x*Constants.baseImageSize+2, y*Constants.baseImageSize+20);
							}
//						}
					}
				}
			}
		}
		bkG.dispose();
		myBufferStrategy.show();
		Toolkit.getDefaultToolkit().sync();
	}
	
	private void drawArrow(Graphics2D bkG, PolicyMove direction, int x, int y) {
		bkG.setColor(Color.blue);
		int startX, startY, endX, endY;
		int[] polyX;
		int[] polyY;
		if (direction == PolicyMove.RIGHT) {
			startX = x*Constants.baseImageSize+9;
			startY = y*Constants.baseImageSize+16;
			endX = startX + 10;
			endY = startY;
			polyX = new int[] { endX, 	endX+3,	endX} ;
			polyY = new int[] { endY+3,	endY, 	endY-3} ;
		}
		else if (direction == PolicyMove.LEFT) {
			startX = x*Constants.baseImageSize+9;
			startY = y*Constants.baseImageSize+16;
			endX = startX + 10;
			endY = startY;
			polyX = new int[] { startX, startX-3, startX} ;
			polyY = new int[] { startY+3, startY, startY-3} ;
		}
		else if (direction == PolicyMove.DOWN) {
			startX = x*Constants.baseImageSize+16;
			startY = y*Constants.baseImageSize+10;
			endX = startX;
			endY = startY+10;
			polyX = new int[] { endX-3, endX, endX+3} ;
			polyY = new int[] { endY, endY+3, endY} ;
		}
		else if (direction == PolicyMove.UP) {
			startX = x*Constants.baseImageSize+16;
			startY = y*Constants.baseImageSize+10;
			endX = startX;
			endY = startY+10;
			polyX = new int[] { startX-3, startX, startX+3} ;
			polyY = new int[] { startY, startY-3, startY} ;
		}
		else if (direction == PolicyMove.UPLEFT) {
			startX = x*Constants.baseImageSize+9;
			startY = y*Constants.baseImageSize+10;
			endX = startX+10;
			endY = startY+10;
			polyX = new int[] { startX-3, startX+2, startX-3} ;
			polyY = new int[] { startY-3, startY-3, startY+2} ;
		}
		else if (direction == PolicyMove.DOWNRIGHT) {
			startX = x*Constants.baseImageSize+9;
			startY = y*Constants.baseImageSize+10;
			endX = startX+10;
			endY = startY+10;
			polyX = new int[] { endX+3, endX-2, endX+3} ;
			polyY = new int[] { endY+3, endY+3, endY-2} ;
		}
		else if (direction == PolicyMove.UPRIGHT) {
			startX = x*Constants.baseImageSize+23;
			startY = y*Constants.baseImageSize+10;
			endX = startX-10;
			endY = startY+10;
			polyX = new int[] { startX+3, startX-2, startX+3} ;
			polyY = new int[] { startY-3, startY-3, startY+2} ;
		}
		else if (direction == PolicyMove.DOWNLEFT) {
			startX = x*Constants.baseImageSize+19;
			startY = y*Constants.baseImageSize+10;
			endX = startX-10;
			endY = startY+10;
			polyX = new int[] { endX-3, endX+2, endX-3} ;
			polyY = new int[] { endY+3, endY+3, endY-2} ;
		}
		else
			return;

		bkG.draw(new Line2D.Double(startX, startY, endX, endY));
		bkG.draw(new Polygon(polyX, polyY, polyX.length));
	}

}
