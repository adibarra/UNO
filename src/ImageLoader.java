import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

//Alec Ibarra
public class ImageLoader {
	
	private static ArrayList<GameIcon> icons = new ArrayList<GameIcon>();
	
	public static void prepare()
	{		
		icons.clear();
		icons.add(new GameIcon("UnoBack"));
		icons.add(new GameIcon("0"));
		icons.add(new GameIcon("1"));
		icons.add(new GameIcon("2"));
		icons.add(new GameIcon("3"));
		icons.add(new GameIcon("4"));
		icons.add(new GameIcon("5"));
		icons.add(new GameIcon("6"));
		icons.add(new GameIcon("7"));
		icons.add(new GameIcon("8"));
		icons.add(new GameIcon("9"));
		icons.add(new GameIcon("SKIP"));
		icons.add(new GameIcon("REVERSE"));
		icons.add(new GameIcon("WILD"));
		icons.add(new GameIcon("DRAW4WILD"));
		icons.add(new GameIcon("DRAW2"));
		icons.add(new GameIcon("ArrowLeft"));
		icons.add(new GameIcon("ArrowRight"));
		icons.add(new GameIcon("noTexture",generateNoTexture()));
	}
	
	public static BufferedImage getImage(String imageName)
	{	
		for(int k = 0; k < icons.size(); k++)
			if(icons.get(k).getIconName().equalsIgnoreCase(imageName))
				return icons.get(k).getIcon();
		return getImage("noTexture");
	}
	
	public static BufferedImage generateNoTexture()
	{
		BufferedImage icon = new BufferedImage(50,50,BufferedImage.TYPE_INT_ARGB);
		Graphics g2 = icon.getGraphics();
		g2.setColor(Color.BLACK);
		g2.fillRect(0,0,24,24);
		g2.fillRect(25,25,24,24);
		g2.setColor(new Color(204,51,255));//purple
		g2.fillRect(0,25,24,24);
		g2.fillRect(25,0,24,24);
		return icon;
	}
	
	public static int[] getPixelColorAt(int x, int y)
	{
		Color color = new Color(((BufferedImage) Logic.offscreen).getRGB(x,y));
		return new int[]{color.getRed(),color.getGreen(),color.getBlue()};
	}
}

class GameIcon
{
	private BufferedImage icon;
	private String iconName;
	
	public GameIcon(String iconName)
	{
		try
		{
			setIconName(iconName);
			setIcon(ImageIO.read(getClass().getClassLoader().getResource(iconName+".png")));
			
		} catch (IOException | IllegalArgumentException e)
		{
			System.out.println("Failed to load: "+iconName+".png");
		}
	}
	
	public GameIcon(String iconName,BufferedImage icon)
	{
		setIconName(iconName);
		setIcon(icon);
	}

	public BufferedImage getIcon() {
		return icon;
	}

	public void setIcon(BufferedImage icon) {
		this.icon = icon;
	}

	public String getIconName() {
		return iconName;
	}

	public void setIconName(String iconName) {
		this.iconName = iconName;
	}
}
