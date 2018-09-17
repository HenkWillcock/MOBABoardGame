import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Item implements Ingredient {

	private final String name;
	private final int cost; // Refers to combine cost on built items.
	private final int totalCost;
	private final StatList addedStats; // Refers to stats added on built items, usually just a single stat or passive.
	private final List<Ingredient> ingredients;
	private final StatList totalStats;
	private final BufferedImage image;

	private Set<Item> buildsInto;

	public Item(String name, BufferedImage image, int cost, StatList stats, List<Ingredient> ingredients) {
		this.name = name;
		this.image = image;
		this.cost = cost;
		this.addedStats = stats;
		this.ingredients = new ArrayList<Ingredient>();
		for (Ingredient ingredient : ingredients) {
			this.ingredients.add(ingredient);
		}
		this.totalCost = this.calculateTotalCost();
		this.totalStats = this.calculateTotalStats();
		this.buildsInto = new HashSet<Item>();
	}

	public Item(String name, BufferedImage image, int cost, StatList stats, Item... ingredients) {
		this.name = name;
		this.image = image;
		this.cost = cost;
		this.addedStats = stats;
		this.ingredients = new ArrayList<Ingredient>();
		for (Item ingredient : ingredients) {
			this.ingredients.add(ingredient);
		}
		this.totalCost = this.calculateTotalCost();
		this.totalStats = this.calculateTotalStats();
		this.buildsInto = new HashSet<Item>();
	}

	public Item(String name, BufferedImage image, int cost, String statName, int statAmount,
			Ingredient... ingredients) {
		this.name = name;
		this.image = image;
		this.cost = cost;
		this.addedStats = new StatList(statName, statAmount);
		this.ingredients = new ArrayList<Ingredient>();
		for (Ingredient ingredient : ingredients) {
			this.ingredients.add(ingredient);
		}
		this.totalCost = this.calculateTotalCost();
		this.totalStats = this.calculateTotalStats();
		this.buildsInto = new HashSet<Item>();
	}

	public int getCost() {
		return this.cost;
	}

	public StatList getStats() {
		return this.addedStats;
	}
	
	/**
	 * If this is a basic item returns its one stat, otherwise returns null.
	 */
	public StatList.Stat getBasicStat() {
		if(!this.isBasic()) {
			return null;
		}
		return this.getStats().getBasicStat();
	}

	private StatList calculateTotalStats() {
		StatList returnValue = new StatList();
		returnValue.mergeStatList(this.getStats());
		for (Ingredient ingredient : this.ingredients) {
			returnValue.mergeStatList(ingredient.getTotalStats());
		}
		return returnValue;
	}

	private int calculateTotalCost() {
		int returnValue = this.getCost();
		for (Ingredient ingredient : this.ingredients) {
			returnValue += ingredient.getTotalCost();
		}
		return returnValue;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public int getTotalCost() {
		return this.totalCost;
	}

	/**
	 * Adds the stats from this item's ingredients, and this items own stats and
	 * returns the total.
	 */
	@Override
	public StatList getTotalStats() {
		return this.totalStats;
	}

	/**
	 * Returns a single string representation of this item which has multiple lines.
	 */
	public String getDescription() {
		String returnValue = this.getName() + "\n";
		for (Ingredient ingredient : this.ingredients) {
			returnValue += (this.getIngredientDescription(ingredient) + "\n");
		}
		returnValue += (" " + this.getCost() + " Gold (" + this.getTotalCost() + " Total)\n");
		returnValue += this.getTotalStats().getDescription();
		return returnValue;
	}

	/**
	 * Returns a list of strings representing this item's ingredients.
	 */
	public List<String> getIngredientsStringList() {
		List<String> returnValue = new ArrayList<String>();
		for (Ingredient ingredient : this.ingredients) {
			returnValue.add(this.getIngredientDescription(ingredient));
		}
		return returnValue;
	}

	/**
	 * Returns a string representation of an ingredient.
	 */
	private String getIngredientDescription(Ingredient ingredient) {
		if(ingredient.getClass() == StatList.Stat.class) {
			return new String(ingredient.getName() + " (" + ingredient.getTotalCost() + " Cheapest)");
		}
		return new String(ingredient.getName() + " (" + ingredient.getTotalCost() + ")");
	}

	/**
	 * A basic item has no ingredients, this method returns a boolean saying if it
	 * is basic or not.
	 */
	public boolean isBasic() {
		return this.ingredients.size() == 0;
	}
	
	/**
	 * A finished item doesn't build into anything, this method returns a boolean saying if it
	 * is finished or not.
	 */
	public boolean isFinished() {
		return this.buildsInto.size() == 0;
	}

	public Image getImage() {
		return this.image;
	}

	public List<String> getStatStringList() {
		return this.totalStats.getStringList();
	}

	public void addBuildsInto(Item buildsInto) {
		this.buildsInto.add(buildsInto);
	}

	public List<String> getBuildsIntoStringList() {
		final int charactersPerLine = 35;
		List<String> returnValue = new ArrayList<String>();
		if (this.isBasic()) {
			returnValue.add("Basic Item");
			return returnValue;
		}
		if (this.buildsInto.size() == 0) {
			return returnValue;
		}
		String currentLine = "Builds To: ";
		int counter = 0;
		for (Item item : this.buildsInto) {
			if(currentLine.length() + item.getName().length() > charactersPerLine) {
				returnValue.add(currentLine);
				currentLine = "";
			}
			if (counter == this.buildsInto.size() - 1) {
				currentLine += item.getName() + ".";
			} else if (this.buildsInto.size() >= 2 && counter == this.buildsInto.size() - 2) {
				currentLine += item.getName() + " or ";
			} else {
				currentLine += item.getName() + ", ";
			}
			counter++;
		}
		returnValue.add(currentLine);
		return returnValue;
	}
}
