
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

/**
 * Reads text files containing item information.
 */
public class ItemReader {

	public static List<Item> allItems;
	public static List<Item> basicItems;

	public ItemReader() {
		ItemReader.basicItems = new ArrayList<Item>();
		ItemReader.allItems = new ArrayList<Item>();
	}

	public List<Item> readItems(String fileName) {
		List<Item> returnValue = new ArrayList<Item>();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(fileName)));
			while (reader.ready()) {
				Item newItem = this.readItem(reader);
				ItemReader.allItems.add(newItem);
				returnValue.add(newItem);
				if (newItem.isBasic()) {
					ItemReader.basicItems.add(newItem);
				}
			}
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnValue;
	}

	private Item readItem(BufferedReader reader) throws Exception {
		// Initialise required variables.
		String itemName;
		BufferedImage image;
		int goldCost;
		StatList stats;
		List<Ingredient> ingredients;
		itemName = this.parseItemName(reader);
		image = this.findImage(itemName);
		ingredients = this.parseIngredients(reader);
		goldCost = this.parseGoldCost(reader);
		stats = this.parseStats(reader);
		Item newItem = new Item(itemName, image, goldCost, stats, ingredients);
		this.addBuildsIntoToIngredients(newItem, ingredients);
		return new Item(itemName, image, goldCost, stats, ingredients);
	}

	/**
	 * Given a list of ingredients for an item and the item itself, adds the item to
	 * each ingredient's 'build's into' list.
	 */
	private void addBuildsIntoToIngredients(Item item, List<Ingredient> ingredients) {
		for (Ingredient ingredient : ingredients) {
			if (ingredient.getClass() == Item.class) {
				((Item) ingredient).addBuildsInto(item);
			}
		}
	}

	private String parseItemName(BufferedReader reader) throws Exception {
		if (!reader.readLine().equals(
				"--------------------------------------------------------------------------------------------------------------")) {
			throw new Exception();
		}
		String itemName = reader.readLine();
		if (!reader.readLine().equals(
				"--------------------------------------------------------------------------------------------------------------")) {
			throw new Exception();
		}
		return itemName;
	}

	private BufferedImage findImage(String itemName) {
		String itemNameNoSpaces = itemName.replaceAll("\\s+", "");
		try {
			return ImageIO.read(new File("Images\\Items\\" + itemNameNoSpaces + ".png"));
		} catch (IOException e) {
			//System.out.println("Missing Image");
		}
		return null;
	}

	private List<Ingredient> parseIngredients(BufferedReader reader) throws IOException {
		List<Ingredient> returnValue = new ArrayList<Ingredient>();
		reader.mark(1000);
		String ingredientName = reader.readLine();
		while (!ingredientName.contains("Gold")) {
			if (Character.isDigit(ingredientName.charAt(0))) {
				returnValue.add(this.parseStatIngredient(ingredientName));
			} else {
				returnValue.add(this.parseIngredient(ingredientName));
			}
			reader.mark(1000);
			ingredientName = reader.readLine();
		}
		reader.reset();
		return returnValue;
	}

	private Ingredient parseStatIngredient(String ingredientName) {
		String[] statIngredientArray = ingredientName.split(" ");
		int statIngredientAmount = Integer.parseInt(statIngredientArray[0]);
		String statIngredientName = statIngredientArray[1];
		return new StatList.Stat(statIngredientName, statIngredientAmount);
	}

	private Item parseIngredient(String ingredientName) {
		for (Item item : ItemReader.allItems) {
			if (item.getName().equals(ingredientName)) {
				System.out.println("Ingredient '" + ingredientName + "' found.");
				return item;
			}
		}
		System.out.println("Ingredient '" + ingredientName + "' not found.");
		return null;
	}

	private int parseGoldCost(BufferedReader reader) throws Exception {
		String goldCostFullString = reader.readLine();
		String[] goldCostStringArray = goldCostFullString.split(" ");
		String goldCostString = goldCostStringArray[0];
		if (!goldCostStringArray[1].equals("Gold")) {
			throw new Exception("Text file didn't say gold.");
		}
		return Integer.parseInt(goldCostString);
	}

	private StatList parseStats(BufferedReader reader) throws IOException {
		StatList returnValue = new StatList();
		for (String statString = reader.readLine(); statString.startsWith("+ ")
				|| statString.startsWith("- "); statString = reader.readLine()) {
			if (statString.charAt(0) == '+') {
				if (statString.contains(" : ")) {
					this.parsePassive(returnValue, statString);
				} else {
					this.parseStat(returnValue, statString);
				}
			} else if (statString.charAt(0) == '-') {
				this.parseSubtractedPassive(returnValue, statString);
			}
		}
		return returnValue;
	}

	private void parseSubtractedPassive(StatList stats, String passiveSubtractString) {
		String subtractedPassiveName = passiveSubtractString.substring(2);
		stats.addPassive(subtractedPassiveName, "REPLACED", 3);
	}

	private void parseStat(StatList stats, String statString) {
		String[] statStringArray = statString.split(" ");
		String statAmountString = statStringArray[1];
		int statAmount = Integer.parseInt(statAmountString);
		String statName = statStringArray[2];
		stats.addStat(statName, statAmount);
	}

	private void parsePassive(StatList stats, String passiveString) {
		String[] passiveStringArray = passiveString.split(" : ");
		String passiveName = passiveStringArray[0].substring(2);
		String passiveEffect = passiveStringArray[1];
		if (passiveName.contains("(2)")) {
			passiveName = passiveName.substring(0, passiveName.length() - 4);
			stats.addPassive(passiveName, passiveEffect, 2);
		}
		stats.addPassive(passiveName, passiveEffect);
	}
}
