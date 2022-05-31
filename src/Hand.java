import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class Hand {
	
	ArrayList<Card> hand = new ArrayList<Card>();
	boolean visible = false;
	int xpos = 0;
	int ypos = 0;
	
	Hand()
	{
		
	}
	
	public void draw(Graphics2D g2, String location)
	{
		if(location.equals("bottom"))
		{
			xpos = Logic.screenWidth/2-50;
			ypos = Logic.screenHeight-230;
			
			if(Tracker.whosTurn().equals(Logic.playerHand))
			{
				g2.setColor(new Color(255,255,255,128));
				g2.fill(new Rectangle(5,Logic.screenHeight-250,Logic.screenWidth-10,220));
			}
			
			for(int k = 0; k < hand.size(); k++)
			{
				Card temp = hand.get(k);
				
				int buffer = 0;
				
				if(hand.size() > 54)
					buffer = -44;
				else if(hand.size() > 44)
					buffer = -42;
				else if(hand.size() > 37)
					buffer = -40;
				else if(hand.size() > 29)
					buffer = -38;
				else if(hand.size() > 22)
					buffer = -35;
				else if(hand.size() > 18)
					buffer = -30;
				else if(hand.size() > 15)
					buffer = -25;
				else if(hand.size() > 13)
					buffer = -20;
				else if(hand.size() > 10)
					buffer = -15;
				else if(hand.size() > 8)
					buffer = -5;
				else if(hand.size() > 0)
					buffer = 5;
				
				int x = (Logic.screenWidth/2)-((50+buffer)*hand.size())+(k*(100+(buffer*2)))-(hand.size());
				
				AffineTransform at = new AffineTransform();
				at.translate(x,Logic.screenHeight-240);
				temp.setHitbox(at.createTransformedShape(new Rectangle(0,0,100,200)));
				g2.setColor(temp.getCardColorColor());
				g2.fill(temp.getHitBox());
				g2.drawImage(ImageLoader.getImage(temp.getAbility()),at,null);
				g2.setColor(Color.BLACK);
				g2.draw(temp.getHitBox());
				
				if(temp.getHovered())
				{
					g2.setColor(new Color(0,0,0,30));
					g2.fill(hand.get(k).getHitBox());
					g2.setColor(Color.WHITE);
					Stroke oldStroke = g2.getStroke();
					g2.setStroke(new BasicStroke(4));
					g2.draw(temp.getHitBox());
					g2.setStroke(oldStroke);
				}
			}
			
			if(Tracker.whosTurn().equals(Logic.playerHand))
			{
				g2.setColor(Color.GRAY);
				Stroke oldStroke = g2.getStroke();
				g2.setStroke(new BasicStroke(6));
				g2.draw(new Rectangle(5,Logic.screenHeight-250,Logic.screenWidth-10,220));
				g2.setColor(Color.ORANGE);
				g2.setStroke(new BasicStroke(2));
				g2.draw(new Rectangle(5,Logic.screenHeight-250,Logic.screenWidth-10,220));
				g2.setStroke(oldStroke);
			}
		}
		else if(location.equals("left"))
		{
			xpos = Logic.screenWidth/3-125;
			ypos = 0;
			
			if(Tracker.whosTurn().equals(Logic.comp3Hand))
			{
				g2.setColor(new Color(255,255,255,80));
				g2.fill(new Rectangle(Logic.screenWidth/3-275,0,300,165));				
			}
			
			for(int k = 0; k < hand.size(); k++)
			{
				Card temp = hand.get(k);
				float scale = 0.75f;
				
				AffineTransform at = new AffineTransform();
				at.scale(scale,scale);
				at.translate((float)(10+((float)k*(((float)(Logic.screenWidth*(1-scale)-120))/(float)(hand.size()))))+1.5*(Logic.screenWidth/10),10);
				temp.setHitbox(at.createTransformedShape(new Rectangle(0,0,100,200)));
				g2.drawImage(ImageLoader.getImage("unoBack"),at,null);
			}
			
			if(Tracker.whosTurn().equals(Logic.comp3Hand))
			{
				g2.setColor(Color.GRAY);
				Stroke oldStroke = g2.getStroke();
				g2.setStroke(new BasicStroke(6));
				g2.draw(new Rectangle(Logic.screenWidth/3-275,0,300,165));
				g2.setColor(Color.ORANGE);
				g2.setStroke(new BasicStroke(2));
				g2.draw(new Rectangle(Logic.screenWidth/3-275,0,300,165));
				g2.setStroke(oldStroke);
			}
		}
		else if(location.equals("middle"))
		{
			xpos = Logic.screenWidth/2-50;
			ypos = 0;
			
			if(Tracker.whosTurn().equals(Logic.comp2Hand))
			{
				g2.setColor(new Color(255,255,255,80));
				g2.fill(new Rectangle(2*Logic.screenWidth/3-310,0,300,165));
			}
			
			for(int k = 0; k < hand.size(); k++)
			{
				Card temp = hand.get(k);
				float scale = 0.75f;
				
				AffineTransform at = new AffineTransform();
				at.scale(scale,scale);
				at.translate((float)(10+((float)k*(((float)(Logic.screenWidth*(1-scale)-120))/(float)(hand.size()))))+5.5*(Logic.screenWidth/10),10);
				temp.setHitbox(at.createTransformedShape(new Rectangle(0,0,100,200)));
				g2.drawImage(ImageLoader.getImage("unoBack"),at,null);
			}
			
			if(Tracker.whosTurn().equals(Logic.comp2Hand))
			{	
				g2.setColor(Color.GRAY);
				Stroke oldStroke = g2.getStroke();
				g2.setStroke(new BasicStroke(6));
				g2.draw(new Rectangle(2*Logic.screenWidth/3-310,0,300,165));
				g2.setColor(Color.ORANGE);
				g2.setStroke(new BasicStroke(2));
				g2.draw(new Rectangle(2*Logic.screenWidth/3-310,0,300,165));
				g2.setStroke(oldStroke);
			}
		}
		else if(location.equals("right"))
		{
			xpos = 3*Logic.screenWidth/3-195;
			ypos = 0;
			
			if(Tracker.whosTurn().equals(Logic.comp1Hand))
			{
				g2.setColor(new Color(255,255,255,80));
				g2.fill(new Rectangle(3*Logic.screenWidth/3-345,0,300,165));
			}
			
			for(int k = 0; k < hand.size(); k++)
			{
				Card temp = hand.get(k);
				float scale = 0.75f;
				
				AffineTransform at = new AffineTransform();
				at.scale(scale,scale);
				at.translate((float)(10+((float)k*(((float)(Logic.screenWidth*(1-scale)-120))/(float)(hand.size()))))+9.5*(Logic.screenWidth/10),10);
				temp.setHitbox(at.createTransformedShape(new Rectangle(0,0,100,200)));
				g2.drawImage(ImageLoader.getImage("unoBack"),at,null);
			}
			
			if(Tracker.whosTurn().equals(Logic.comp1Hand))
			{
				g2.setColor(Color.GRAY);
				Stroke oldStroke = g2.getStroke();
				g2.setStroke(new BasicStroke(6));
				g2.draw(new Rectangle(3*Logic.screenWidth/3-345,0,300,165));
				g2.setColor(Color.ORANGE);
				g2.setStroke(new BasicStroke(2));
				g2.draw(new Rectangle(3*Logic.screenWidth/3-345,0,300,165));
				g2.setStroke(oldStroke);
			}
		}
	}
	
	public CardColor isPureColor()
	{
		int green = 0;
		int blue = 0;
		int yellow = 0;
		int red = 0;
		
		for(int k = 0; k < hand.size(); k++)
		{
			if(hand.get(k).getCardColor().equals(CardColor.GREEN))
				green++;
			else if(hand.get(k).getCardColor().equals(CardColor.BLUE))
				blue++;
			else if(hand.get(k).getCardColor().equals(CardColor.YELLOW))
				yellow++;
			else if(hand.get(k).getCardColor().equals(CardColor.RED))
				red++;
		}
		
		if(green != 0 && blue == 0 && yellow == 0 && red == 0)
			return CardColor.GREEN;
		if(blue != 0 && green == 0 && yellow == 0 && red == 0)
			return CardColor.BLUE;
		if(yellow != 0 && blue == 0 && green == 0 && red == 0)
			return CardColor.YELLOW;
		if(red != 0 && blue == 0 && yellow == 0 && green == 0)
			return CardColor.RED;
		return CardColor.NONE;
	}
	
	public CardColor getMostColor()
	{
		int green = 0;
		int blue = 0;
		int yellow = 0;
		int red = 0;
		
		for(int k = 0; k < hand.size(); k++)
		{
			if(hand.get(k).getCardColor().equals(CardColor.GREEN))
				green++;
			else if(hand.get(k).getCardColor().equals(CardColor.BLUE))
				blue++;
			else if(hand.get(k).getCardColor().equals(CardColor.YELLOW))
				yellow++;
			else if(hand.get(k).getCardColor().equals(CardColor.RED))
				red++;
		}
		
		if(green >= blue && green >= yellow && green >= red)
			return CardColor.GREEN;
		if(blue >= green && blue >= yellow && blue >= red)
			return CardColor.BLUE;
		if(yellow >= blue && yellow >= green && yellow >= red)
			return CardColor.YELLOW;
		if(red >= blue && red >= yellow && red >= green)
			return CardColor.RED;
		return CardColor.NONE;
	}
	
	public CardColor getLeastColor()
	{
		int green = 0;
		int blue = 0;
		int yellow = 0;
		int red = 0;
		
		for(int k = 0; k < hand.size(); k++)
		{
			if(hand.get(k).getCardColor().equals(CardColor.GREEN))
				green++;
			else if(hand.get(k).getCardColor().equals(CardColor.BLUE))
				blue++;
			else if(hand.get(k).getCardColor().equals(CardColor.YELLOW))
				yellow++;
			else if(hand.get(k).getCardColor().equals(CardColor.RED))
				red++;
		}
		
		if(green <= blue && green <= yellow && green <= red)
			return CardColor.GREEN;
		if(blue <= green && blue <= yellow && blue <= red)
			return CardColor.BLUE;
		if(yellow <= blue && yellow <= green && yellow <= red)
			return CardColor.YELLOW;
		if(red <= blue && red <= yellow && red <= green)
			return CardColor.RED;
		return CardColor.NONE;
	}
	
	public ArrayList<Card> getNumberCards()
	{
		ArrayList<Card> numbers = new ArrayList<Card>();
		for(int k = 0; k < hand.size(); k++)
		{
			if(hand.get(k).getNumber() < 10)
				numbers.add(hand.get(k));
		}
		return numbers;
	}
	
	public ArrayList<Card> getSymbolCards()
	{
		ArrayList<Card> symbols = new ArrayList<Card>();
		for(int k = 0; k < hand.size(); k++)
		{
			if(hand.get(k).getNumber() > 9 && hand.get(k).getNumber() < 13)
				symbols.add(hand.get(k));
		}
		return symbols;
	}
	
	public ArrayList<Card> getWildCards()
	{
		ArrayList<Card> wilds = new ArrayList<Card>();
		for(int k = 0; k < hand.size(); k++)
		{
			if(hand.get(k).getNumber() > 12)
				wilds.add(hand.get(k));
		}
		return wilds;
	}
	
	public Card findBestPlay()
	{
		Card toPlay = null;
		Card last = Logic.last;
		ArrayList<Card> numbers = getNumberCards();
		ArrayList<Card> symbols = getSymbolCards();
		ArrayList<Card> wilds = getWildCards();
		
		if(!isPureColor().equals(CardColor.NONE))//check for wild if only 1 color remains && not already needed color
			if(wilds.size() > 0)
				if(!last.getCardColor().equals(isPureColor()))
				{
					toPlay = wilds.get(0);
					toPlay.setCardColor(isPureColor());
					//System.out.println("Only one color remains must use wild...");
					return toPlay;
				}
		
		for(int k = 0; k < numbers.size(); k++)//Check for same color (number cards)
			if(numbers.get(k).getCardColor().equals(last.getCardColor()))
			{
				//System.out.println("Found number card with same color");
				return numbers.get(k);
			}
		
		for(int k = 0; k < numbers.size(); k++)//Check for same number (number cards)
			if(numbers.get(k).getNumber() == last.getNumber())
			{
				//System.out.println("Found number card with same number");
				return numbers.get(k);
			}
		
		for(int k = 0; k < symbols.size(); k++)//Check for same color (symbol cards)
			if(symbols.get(k).getCardColor().equals(last.getCardColor()))
			{
				//System.out.println("Found symbol card with same color");
				return symbols.get(k);
			}
		
		for(int k = 0; k < symbols.size(); k++)//Check for same number (symbol cards)
			if(symbols.get(k).getNumber() == last.getNumber())
			{
				//System.out.println("Found symbol card with same symbol");
				return symbols.get(k);
			}
		
		if(wilds.size() > 0)//check for wilds and if so set to most needed color
		{
			toPlay = wilds.get(0);
			toPlay.setCardColor(getMostColor());
			//System.out.println("Using a wild card to set most needed color");
			return toPlay;
		}
		
		for(int k = 0; k < hand.size(); k++)//last ditch effort
			if(hand.get(k).canPlay(last))
			{
				//System.out.println("Found this during the last ditch effort");
				return hand.get(k);
			}
		
		//draw a card and try again
		//System.out.println("Drawing a new card...");
		addToHand(Logic.deck.draw(1),true,true);
		toPlay = findBestPlay();
		return toPlay;
	}
	
	public void playCard(Card card)
	{	
		Tracker.animators.add(new Animator(0,Tracker.whosTurn().xpos,Tracker.whosTurn().ypos,4*Logic.screenWidth/11,Logic.screenHeight/2-125,card));
		if(card.getAbility().equals("SKIP"))
		{
			Tracker.nextTurn();
			Tracker.nextTurn();
		}
		else if(card.getAbility().equals("REVERSE"))
		{
			Logic.direction = !Logic.direction;
			Tracker.nextTurn();
		}
		else if(card.getAbility().equals("WILD"))
		{
			if(card.getCardColor().equals(CardColor.NONE))
				Logic.drawChooser = true;
			else
				Tracker.nextTurn();
		}
		else if(card.getAbility().equals("DRAW2"))
		{
			Tracker.whosNext().addToHand(Logic.deck.draw(2),true,false);
			Tracker.nextTurn();
			Tracker.nextTurn();
		}
		else if(card.getAbility().equals("DRAW4WILD"))
		{
			Tracker.whosNext().addToHand(Logic.deck.draw(4),true,false);
			if(card.getCardColor().equals(CardColor.NONE))
				Logic.drawChooser = true;
			else
			{
				Tracker.nextTurn();
				Tracker.nextTurn();
			}
		}
		else
		{
			Tracker.nextTurn();
		}
		
		hand.remove(card);
		Logic.tickTimer = Logic.tickTimerFull;
		Tracker.checkForWinners();
	}
	
	public void addToHand(ArrayList<Card> cards, boolean animate, boolean instantAdd)
	{	
		if(animate)
		{
			for(int k = 0; k < cards.size(); k++)
			Tracker.animators.add(new Animator((k+1),Logic.deck.x,Logic.deck.y,xpos,ypos,cards.get(k),this,instantAdd));
		}
		else
			hand.addAll(cards);
	}
	
	public boolean checkForWin()
	{
		if(hand.size() == 0)
			return true;
		return false;
	}
	
	public boolean hoverCheck(int mousex, int mousey)
	{		
		for(int k = 0; k < hand.size(); k++)
			hand.get(k).setHovered(false);
			
		for(int k = hand.size()-1; k > -1; k--)
		{
			if(hand.get(k).getHitBox() != null)
				if(hand.get(k).getHitBox().contains((Point2D)new Point(mousex,mousey)))
				{
					hand.get(k).setHovered(true);
					return true;
				}
		}
		return false;
	}
	
	public Card clickCheck(int clickx, int clicky)
	{
		for(int k = hand.size()-1; k > -1; k--)
			if(hand.get(k).getHitBox() != null)
				if(hand.get(k).getHitBox().contains((Point2D)new Point(clickx,clicky)))
					return hand.get(k);
		return null;
	}
}
