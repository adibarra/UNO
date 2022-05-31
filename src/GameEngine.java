import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.Serializable;

import javax.swing.JFrame;

//Alec Ibarra
public class GameEngine
{

	public static void main(String[] args)
    {
		String osName = System.getProperty("os.name").toLowerCase();
		if (osName.startsWith("mac os x")) 
		{
			//Makes Command+Q activate the windowClosing windowEvent
			System.setProperty("apple.eawt.quitStrategy","CLOSE_ALL_WINDOWS");
		}
    	
		Logic game = new Logic();
    	
		game.setMinimumSize(new Dimension(1000+game.getInsets().left+game.getInsets().right,
				650+game.getInsets().top+game.getInsets().bottom));
		game.setVisible(true);
				
		game.addComponentListener(new ComponentListener() {
			public void componentResized(ComponentEvent e)
			{
				Logic.screenWidth = game.getWidth();
				Logic.screenHeight = game.getHeight();
			}
			public void componentMoved(ComponentEvent e) {}
			public void componentShown(ComponentEvent e) {}
			public void componentHidden(ComponentEvent e) {}
		});
		
		game.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e)
			{
				System.exit(0);
			}
		});
    }
}

 /**
 * 
 * @author Alec Ibarra
 * 
 **/
class Logic extends JFrame implements MouseListener,MouseMotionListener,KeyListener,MouseWheelListener,Serializable
{
	//game initialization
	private static final long serialVersionUID = 1L;
	static BufferedImage offscreen = null;
    static Graphics g2;
    static RenderingHints rh;
    static int screenWidth = 1000;//start size will be adjusted
    static int screenHeight = 650;//start size will be adjusted
    static int offsetX = 0;//will be adjusted based on OS
    static int offsetY = 0;//will be adjusted based on OS
    static int offsetX2 = 0;//will be adjusted based on OS
    static int offsetY2 = 0;//will be adjusted based on OS
    static final double GAME_VERSION = 0.0;
    static final String PROJECT_NAME = "UNO";
    
    //game state controls
    static String gameOverStr = "Game Over";
    static String lastGameOverStr = "Game Over";
    static boolean gameOver = false;
    static boolean paused = true;
    static boolean gameStarted = false;
    static boolean showControls = false;
    static boolean showSettings= false;
    
    //game control
    static final float TARGET_FRAME_TIME_120 = 8.3f;//120FPS
    static final float TARGET_FRAME_TIME_60 = 16.7f;//60FPS
	static final float TARGET_FRAME_TIME_30 = 33.3f;//30FPS
	static final float TARGET_FRAME_TIME_15 = 66.7f;//15FPS
    static float TARGET_FRAME_TIME = TARGET_FRAME_TIME_120;//default option
	static double elapsed = TARGET_FRAME_TIME_120;
	static double start = System.nanoTime();
	
	//game control
	//static boolean playerMove = true;
	//static boolean comp1Move = false;
	//static boolean comp2Move = false;
	//static boolean comp3Move = false;
	static boolean direction = true;
	static boolean drawChooser = false;
    static int tickTimer = 0;
    static int tickTimerFull = 50;
    
    //display control
    static int mousex = 0;
    static int mousey = 0;
    static int lastmousex = 0;
    static int lastmousey = 0;
    static boolean afk = false;
    static BufferedImage screenSave = null;
	
	//class instances and etc.
	static FileLoader fl = new FileLoader();
	static Deck deck = new Deck();
	static Hand playerHand = new Hand();
	static Hand comp1Hand = new Hand();
	static Hand comp2Hand = new Hand();
	static Hand comp3Hand = new Hand();
	static Card last = new Card();
	
	public Logic()
    {
		/*********************************Initialize Game***********************************/
        super("UNO");//sets program name
        //List<Image> icons = new ArrayList<Image>();
        //icons.add(Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("Icon2048x2048.png")));//loads icon 2048x2048
        //icons.add(Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("Icon1024x1024.png")));//loads icon 1024x1024
        //icons.add(Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("Icon0512x0512.png")));//loads icon 512x512
        //icons.add(Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("Icon0256x0256.png")));//loads icon 256x256
        //icons.add(Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("Icon0128x0128.png")));//loads icon 128x128
        //super.setIconImages(icons);//loads icons
        addMouseListener(this);//adds neccessary listener
		addMouseMotionListener(this);//adds neccessary listener
		addMouseWheelListener(this);//adds neccessary listener
		addKeyListener(this);//adds neccessary listener
        requestFocusInWindow();//calls focus for window
        rh = new RenderingHints(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
        
        /*********************************Setting up JFrame*********************************/
        setResizable(false);
        pack();
        offsetX = super.getInsets().left;
        offsetY = super.getInsets().top;
        offsetX2 = super.getInsets().right;
        offsetY2 = super.getInsets().bottom;
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        
        /*********************************Setting up Misc************************************/
        Tracker.prepare();
        Keybinds.prepare();
        ImageLoader.prepare();
        deck.populate().shuffle();
        playerHand.addToHand(deck.draw(7),false,false);
        comp1Hand.addToHand(deck.draw(7),false,false);
        comp2Hand.addToHand(deck.draw(7),false,false);
        comp3Hand.addToHand(deck.draw(7),false,false);
        last = deck.draw(1).get(0);
        
        while(last.getAbility().contains("WILD"))
        	last = deck.draw(1).get(0);
    }
	
	/***************************************** 
					Main Loop
	*****************************************/
	
	public void update(Graphics g)//main method
    {    	
		//prepare to run loop
		offscreen = (BufferedImage)createImage((int)getSize().getWidth(),(int)getSize().getHeight());
		g2 = offscreen.getGraphics();
		g2.translate(offsetX,offsetY);
	    ((Graphics2D) g2).setRenderingHints(rh);
		
		if(!gameOver)
		{						
			elapsed = (System.nanoTime()-start)/1000000000;//records looptime at this point in seconds
			runLogic();//main logic method
			if(afk)
				offscreen = screenSave;
			else
				display(g2);//main display method
			tickTimer--;
		}
		else
		{	
			drawGameOver(g2);//draw GameOver
		}
		
		lastmousex = mousex;
		lastmousey = mousey;
		screenSave = offscreen;
		
		g.drawImage(offscreen,0,0,this);//draw screen from buffer
		
		start = System.nanoTime();//start timer
		elapsed = (System.nanoTime()-start)/1000000000;//records looptime at this point in seconds
		delay(TARGET_FRAME_TIME-elapsed);//tries to get to the target frame time
		repaint();
	}
	
	
	/***************************************** 
					Logic
	*****************************************/
	
	public void runLogic()//gives methods a normalized (1-ish) number to use as a multiplier for movement
	{
		runAI();
	}
	
	public void afkCheck()
	{
		//afk = mousex == lastmousex && mousey == lastmousey && playerMove && tickTimer < 0 && !drawChooser;
		afk = mousex == lastmousex && mousey == lastmousey && Tracker.whosTurn().equals(playerHand) && tickTimer < 0 && !drawChooser;
	}
	
	public void runAI()
	{
		//if(tickTimer < 0 && !playerMove)
		if(tickTimer < 0 && !Tracker.whosTurn().equals(playerHand))
		{
			Tracker.whosTurn().playCard(Tracker.whosTurn().findBestPlay());
			tickTimer = tickTimerFull;
		}
	}
	
	/***************************************** 
					Display
	*****************************************/
	
	public void display(Graphics g2)
	{
		drawBackground(g2);
		drawForeground(g2);
		drawDirectionalArrows(g2);
		drawAnimations(g2);
		drawColorChooser(g2);
		drawEffects(g2);
	}
	
	public void drawForeground(Graphics g2)
	{
		playerHand.draw((Graphics2D) g2,"bottom");
		comp1Hand.draw((Graphics2D) g2,"right");
		comp2Hand.draw((Graphics2D) g2,"middle");
		comp3Hand.draw((Graphics2D) g2,"left");
		last.drawImage((Graphics2D) g2,4*Logic.screenWidth/11,Logic.screenHeight/2-125);
		deck.draw(g2);

		g2.setColor(Color.BLACK);
		g2.drawRect(4*Logic.screenWidth/11,Logic.screenHeight/2-125,100,200);
		g2.setColor(last.getCardColorColor());
		g2.fillRect(4*Logic.screenWidth/11+105,Logic.screenHeight/2-125,15,200);
		
		Stroke oldStroke = ((Graphics2D) g2).getStroke();
		((Graphics2D) g2).setStroke(new BasicStroke(2));
		g2.setColor(Color.WHITE);
		g2.drawRect(4*Logic.screenWidth/11+105,Logic.screenHeight/2-125,15,200);
		((Graphics2D) g2).setStroke(oldStroke);
		
		g2.setColor(Color.BLACK);
		g2.drawString(last.getCardColor().toString(),(int)(4*Logic.screenWidth/11),(int)(Logic.screenHeight/2-130));
		g2.drawString(Tracker.whosTurnStr(),(int)(4*Logic.screenWidth/11),(int)(Logic.screenHeight/2-145));
	}
	
	public void drawBackground(Graphics g2)
	{
		if(Tracker.whosTurn().equals(playerHand))
			g2.setColor(new Color(128,128,128));
		else
			g2.setColor(Color.LIGHT_GRAY);
		g2.fillRect(0,0,screenWidth,screenHeight);
	}
	
	public void drawDirectionalArrows(Graphics g2)
	{
		if(direction)
		{
			AffineTransform at = new AffineTransform();
			at.translate((int)(7.5*Logic.screenWidth/10),(int)(Logic.screenHeight/2-90));
			((Graphics2D) g2).drawImage(ImageLoader.getImage("ArrowLeft"),at,null);
			
			AffineTransform at2 = new AffineTransform();
			at2.translate((int)(1.5*Logic.screenWidth/10)+50,(int)(Logic.screenHeight/2-90)+50);
			at2.rotate(Math.toRadians(180));
			at2.translate(-50,-50);
			((Graphics2D) g2).drawImage(ImageLoader.getImage("ArrowLeft"),at2,null);
		}
		else
		{
			AffineTransform at = new AffineTransform();
			at.translate((int)(1.5*Logic.screenWidth/10),(int)(Logic.screenHeight/2-90));
			((Graphics2D) g2).drawImage(ImageLoader.getImage("ArrowRight"),at,null);
			
			AffineTransform at2 = new AffineTransform();
			at2.translate((int)(7.5*Logic.screenWidth/10)+50,(int)(Logic.screenHeight/2-90)+50);
			at2.rotate(Math.toRadians(180));
			at2.translate(-50,-50);
			((Graphics2D) g2).drawImage(ImageLoader.getImage("ArrowRight"),at2,null);
		}
	}
	
	public void drawAnimations(Graphics g2)
	{
		for(int k = 0; k < Tracker.animators.size(); k++)
			if(Tracker.animators.get(k) != null)
				if(!Tracker.animators.get(k).finished)
					Tracker.animators.get(k).draw((Graphics2D)g2);
				else
					Tracker.animators.remove(k);
	}
	
	public void drawEffects(Graphics g2)
	{
		for(int k = 0; k < Tracker.effects.size(); k++)
			if(Tracker.effects.get(k) != null)
				if(!Tracker.effects.get(k).finished)
					Tracker.effects.get(k).draw((Graphics2D)g2);
				else
					Tracker.effects.remove(k);
	}
	
	public void drawColorChooser(Graphics g2)
	{
		if(drawChooser)
		{
			g2.setColor(new Color(255,0,0));
			((Graphics2D) g2).fill(new Rectangle(screenWidth/2-50,screenHeight/2-100,50,50));//red, top left
			g2.setColor(new Color(0,0,255));
			((Graphics2D) g2).fill(new Rectangle(screenWidth/2,screenHeight/2-100,50,50));//blue, top right
			g2.setColor(new Color(0,210,0));
			((Graphics2D) g2).fill(new Rectangle(screenWidth/2-50,screenHeight/2-50,50,50));//green, bottom left
			g2.setColor(new Color(255,255,0));
			((Graphics2D) g2).fill(new Rectangle(screenWidth/2,screenHeight/2-50,50,50));//yellow, bottom right
			g2.setColor(Color.BLACK);
			((Graphics2D) g2).draw(new Rectangle(screenWidth/2-50,screenHeight/2-100,100,100));//chooser outline
		}
	}
	
	public void drawGameOver(Graphics g2)
	{
		display(g2);
		g2.setColor(new Color(255,255,255,128));
		g2.fillRect(0,0,screenWidth,screenHeight);
		g2.setColor(Color.BLACK);
		drawCenteredString(g2,gameOverStr,new Rectangle2D.Double(0,0,screenWidth,screenHeight),new Font("Ariel",Font.BOLD,72));
	}
	
	/***************************************** 
					Utilities
	*****************************************/
	
	public static void drawCenteredString(Graphics g2, String text, Rectangle2D.Double rect, Font font) 
	{
	    FontMetrics metrics = g2.getFontMetrics(font);
	    int x = (int)(rect.getX()+(rect.getWidth()-metrics.stringWidth(text))/2);
	    int y = (int)(rect.getY()+((rect.getHeight()-metrics.getHeight())/2)+metrics.getAscent());
	    g2.setFont(font);
	    g2.drawString(text,x,y);
	}
	
	public static double getDistance(double x1, double y1, double x2, double y2)
	{
		return Math.sqrt(Math.pow(x2-x1,2)+Math.pow(y2-y1,2));
	}
	
	public static long getCurrentTime()
	{
		return (long)(System.nanoTime()/1000000000);
	}
	
	public static void delay(double dt)//better time delay method
	{
		try 
		{
			if(dt < 0)
				dt = 0;
			for(int k = 0; k < dt; k++)
				Thread.sleep(k);
		} catch (InterruptedException e) {}
	}
	
	
	/***************************************** 
					Listeners
	*****************************************/
	
	public void mouseDragged(MouseEvent e)
	{	
		int clickx = e.getX()-Logic.offsetX;
		int clicky = e.getY()-Logic.offsetY;
		
		Card temp = playerHand.clickCheck(clickx,clicky);
		if(temp != null && Tracker.whosTurn().equals(playerHand))
			if(temp.canPlay(Logic.last))
				playerHand.playCard(temp);
		
		if(drawChooser)
		{
			if(new Rectangle(screenWidth/2-50,screenHeight/2-100,100,200).contains(clickx,clicky))
			{
				drawChooser = false;
				if(new Rectangle(screenWidth/2-50,screenHeight/2-100,50,50).contains(clickx,clicky))//red, top left
					last.setCardColor(CardColor.RED);
				else if(new Rectangle(screenWidth/2,screenHeight/2-100,50,50).contains(clickx,clicky))//blue, top right
					last.setCardColor(CardColor.BLUE);
				else if(new Rectangle(screenWidth/2-50,screenHeight/2-50,50,50).contains(clickx,clicky))//green, bottom left
					last.setCardColor(CardColor.GREEN);
				else if(new Rectangle(screenWidth/2,screenHeight/2-50,50,50).contains(clickx,clicky))//yellow, bottom right
					last.setCardColor(CardColor.YELLOW);
				
				if(last.getAbility().equals("DRAW4WILD") && !last.getCardColor().equals(CardColor.NONE))
					Tracker.nextTurn();
				Tracker.nextTurn();
			}
		}
		else if(temp == null && Tracker.whosTurn().equals(playerHand))
			if(new Rectangle(deck.x,deck.y,100,200).contains(clickx,clicky))
				playerHand.addToHand(deck.draw(1),true,false);
	}
	
	public void mouseReleased(MouseEvent e) 
	{

	}
	
	public void mouseMoved(MouseEvent e) 
	{
		mousex = e.getX()-Logic.offsetX;
		mousey = e.getY()-Logic.offsetY;
		
		playerHand.hoverCheck(mousex,mousey);
	}

	public void keyPressed(KeyEvent e)
	{
		if(e.getKeyCode() == KeyEvent.VK_SPACE)
		{
			Keybinds.getKeyAction(KeyEvent.VK_SPACE,true);
		}
		else if(e.getKeyCode() == KeyEvent.VK_TAB)
		{
			Keybinds.getKeyAction(KeyEvent.VK_TAB,true);
		}
		else if(e.getKeyCode() == KeyEvent.VK_SHIFT)
		{
			Keybinds.getKeyAction(KeyEvent.VK_SHIFT,true);
		}
		else if(e.getKeyCode() == KeyEvent.VK_CONTROL)
		{
			Keybinds.getKeyAction(KeyEvent.VK_CONTROL,true);
		}
		else if(e.getKeyCode() == KeyEvent.VK_UP)
		{
			Keybinds.getKeyAction(KeyEvent.VK_UP,true);
		}
		else if(e.getKeyCode() == KeyEvent.VK_DOWN)
		{
			Keybinds.getKeyAction(KeyEvent.VK_DOWN,true);
		}
		else if(e.getKeyCode() == KeyEvent.VK_RIGHT)
		{
			Keybinds.getKeyAction(KeyEvent.VK_RIGHT,true);
		}
		else if(e.getKeyCode() == KeyEvent.VK_LEFT)
		{
			Keybinds.getKeyAction(KeyEvent.VK_LEFT,true);
		}
		else
		{
			for(int k = 0; k < 250; k++)//For numbers, letters, and symbols
			{
				if(e.getKeyCode() == k)
				{
					Keybinds.getKeyAction(e.getKeyCode(),true);
				}	
			}
		}
		if(Keybinds.getActionValue("debug1"))
			System.out.println(e.getKeyChar()+" "+e.getKeyCode()+" "+Keybinds.getKeyAction(e.getKeyCode())
					+" "+Keybinds.getActionValue(Keybinds.getKeyAction(e.getKeyCode())));
	}
	
	public void keyReleased(KeyEvent e) 
	{
		if(e.getKeyCode() == KeyEvent.VK_SPACE)
		{
			Keybinds.getKeyAction(KeyEvent.VK_SPACE,false);
		}
		else if(e.getKeyCode() == KeyEvent.VK_TAB)
		{
			Keybinds.getKeyAction(KeyEvent.VK_TAB,false);
		}
		else if(e.getKeyCode() == KeyEvent.VK_SHIFT)
		{
			Keybinds.getKeyAction(KeyEvent.VK_SHIFT,false);
		}
		else if(e.getKeyCode() == KeyEvent.VK_CONTROL)
		{
			Keybinds.getKeyAction(KeyEvent.VK_CONTROL,false);
		}
		else if(e.getKeyCode() == KeyEvent.VK_UP)
		{
			Keybinds.getKeyAction(KeyEvent.VK_UP,false);
		}
		else if(e.getKeyCode() == KeyEvent.VK_DOWN)
		{
			Keybinds.getKeyAction(KeyEvent.VK_DOWN,false);
		}
		else if(e.getKeyCode() == KeyEvent.VK_RIGHT)
		{
			Keybinds.getKeyAction(KeyEvent.VK_RIGHT,false);
		}
		else if(e.getKeyCode() == KeyEvent.VK_LEFT)
		{
			Keybinds.getKeyAction(KeyEvent.VK_LEFT,false);
		}
		else
		{
			for(int k = 0; k < 250; k++)//For numbers, letters, and symbols
			{
				if(e.getKeyCode() == k)
				{
					Keybinds.getKeyAction(e.getKeyCode(),false);
				}	
			}
		}
		if(Keybinds.getActionValue("debug1"))
			System.out.println(e.getKeyChar()+" "+e.getKeyCode()+" "+Keybinds.getKeyAction(e.getKeyCode())
					+" "+Keybinds.getActionValue(Keybinds.getKeyAction(e.getKeyCode())));
	}
	
	public void mousePressed(MouseEvent e) {
		mouseDragged(e);
	}
	
	public void paint(Graphics g){
		update(g);
	}

	public void keyTyped(KeyEvent e) {}
	
	public void mouseWheelMoved(MouseWheelEvent e) {}

	public void mouseClicked(MouseEvent e) {}

	public void mouseEntered(MouseEvent e) {}

	public void mouseExited(MouseEvent e) {}
	
}
