import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

public class CardRenderer {
	private static final int INFO_PANEL_TOP = 261;
	private static final int INFO_PANEL_LEFT = 16;

	private final BufferedImage cardTemplate;
	private final BufferedImage finishedCardTemplate;

	public CardRenderer() {
		BufferedImage cardTemplate = null;
		BufferedImage finishedCardTemplate = null;
		try {
			cardTemplate = ImageIO.read(new File("Images\\CardTemplate.png"));
			finishedCardTemplate = ImageIO.read(new File("Images\\FinishedCardTemplate.png"));
		} catch (IOException exception) {
			exception.printStackTrace();
		}
		this.cardTemplate = cardTemplate;
		this.finishedCardTemplate = finishedCardTemplate;
	}

	public void saveImageFromItem(Item item) {
		BufferedImage image = this.createImageFromItem(item);
		File outputFile = new File("Images\\Cards\\" + item.getName() + ".png");
		try {
			ImageIO.write(image, "png", outputFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public BufferedImage createImageFromItem(Item item) {
		BufferedImage returnValue = null;
		if (item.isFinished() && !item.isBasic()) {
			returnValue = this.deepCopy(this.finishedCardTemplate);
		} else {
			returnValue = this.deepCopy(this.cardTemplate);
		}
		Graphics graphics = returnValue.getGraphics();
		this.drawSprite(item, graphics);
		int line = 0;
		line = this.drawIngredients(item, graphics, line);
		line = this.drawStats(item, graphics, line);
		line = this.drawBuildsInto(item, graphics, line);
		return returnValue;
	}

	private void drawSprite(Item item, Graphics graphics) {
		graphics.setFont(new Font("Monospaced", Font.BOLD, 20));
		if (item.isFinished() && !item.isBasic()) {
			graphics.setColor(new Color(128, 0, 0));
		} else {
			graphics.setColor(new Color(100, 100, 100));
		}
		graphics.drawString(item.getName(), 19, 31);
		graphics.drawImage(item.getImage(), 40, 44, 200, 200, null);
	}

	private int drawIngredients(Item item, Graphics graphics, int line) {
		List<String> ingredientsList = item.getIngredientsStringList();
		graphics.setFont(new Font("Monospaced", Font.BOLD, 12));
		graphics.setColor(new Color(191, 144, 0));
		for (String ingredientString : ingredientsList) {
			graphics.drawString(ingredientString, INFO_PANEL_LEFT, INFO_PANEL_TOP + 14 * line);
			line++;
		}
		if (item.isBasic()) {
			graphics.drawString(item.getCost() + " Gold", INFO_PANEL_LEFT, INFO_PANEL_TOP + 14 * line);
		} else {
			graphics.drawString(item.getCost() + " Gold (" + item.getTotalCost() + " Total)", INFO_PANEL_LEFT,
					INFO_PANEL_TOP + 14 * line);
		}
		return ++line;
	}

	private int drawStats(Item item, Graphics graphics, int line) {
		List<String> statList = item.getStatStringList();
		graphics.setColor(new Color(17, 85, 205));
		graphics.setFont(new Font("Monospaced", Font.PLAIN, 12));
		for (String statString : statList) {
			graphics.drawString(statString, INFO_PANEL_LEFT, INFO_PANEL_TOP + 14 * line);
			line++;
		}
		return ++line;
	}

	private int drawBuildsInto(Item item, Graphics graphics, int line) {
		List<String> buildsIntoStringList = item.getBuildsIntoStringList();
		graphics.setColor(new Color(100, 100, 100));
		graphics.setFont(new Font("Monospaced", Font.BOLD, 12));
		for (String buildsIntoString : buildsIntoStringList) {
			graphics.drawString(buildsIntoString, INFO_PANEL_LEFT, INFO_PANEL_TOP + 14 * line);
			line++;
		}
		return ++line;
	}

	private BufferedImage deepCopy(BufferedImage bi) {
		ColorModel cm = bi.getColorModel();
		boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		WritableRaster raster = bi.copyData(null);
		return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
	}
}
