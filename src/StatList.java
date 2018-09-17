import java.util.ArrayList;
import java.util.List;

public class StatList {
	private List<Stat> stats;
	private List<Passive> passives;

	public StatList(Stat... stats) {
		this.stats = new ArrayList<Stat>();
		this.passives = new ArrayList<Passive>();
		for (Stat stat : stats) {
			this.addStat(stat.getStatName(), stat.getStatAmount());
		}
	}

	public void addStat(String statName, int statAmount) {
		this.stats.add(new Stat(statName, statAmount));
	}

	public void addPassive(String passiveName, String passiveEffect) {
		this.passives.add(new Passive(passiveName, passiveEffect));
	}

	public void addPassive(String passiveName, String passiveEffect, int level) {
		this.passives.add(new Passive(passiveName, passiveEffect, level));
	}

	public StatList(String statName, int statAmount) {
		this.stats = new ArrayList<Stat>();
		this.passives = new ArrayList<Passive>();
		this.addStat(statName, statAmount);
	}

	public StatList(String passiveName, String passiveEffect) {
		this.stats = new ArrayList<Stat>();
		this.passives = new ArrayList<Passive>();
		this.addPassive(passiveName, passiveEffect);
	}

	public Stat getBasicStat() {
		if(this.stats.isEmpty()) {
			return null;
		}
		return this.stats.get(0);
	}

	/*
	 * Combines a stat list with this one, adding stats and taking the superior
	 * passive if they have the same name.
	 */
	public void mergeStatList(StatList statList) {
		for (Stat stat : statList.stats) {
			this.mergeStat(stat.getStatName(), stat.getStatAmount());
		}
		for (Passive passive : statList.passives) {
			this.mergePassive(passive);
		}
	}

	private void mergePassive(Passive newPassive) {
		for (Passive oldPassive : this.passives) {
			if (newPassive.getPassiveName().equals(oldPassive.getPassiveName())) {
				if (newPassive.getPassiveLevel() > oldPassive.getPassiveLevel()) {
					oldPassive.changePassiveEffect(newPassive.getPassiveEffect());
				}
				return;
			}
		}
		this.addPassive(newPassive.getPassiveName(), newPassive.getPassiveEffect());
	}

	private void mergeStat(String statName, int statAmount) {
		for (Stat stat : this.stats) {
			if (stat.getStatName().equals(statName)) {
				stat.increaseStatAmount(statAmount);
				return;
			}
		}
		this.addStat(statName, statAmount);
	}

	public String getDescription() {
		String returnValue = new String();
		for (Stat stat : this.stats) {
			returnValue += (stat.getDescription() + "\n");
		}
		for (Passive passive : this.passives) {
			returnValue += (passive.getDescription() + "\n");
		}
		return returnValue;
	}

	public List<String> getStringList() {
		final int charactersPerLine = 35;

		List<String> returnValue = new ArrayList<String>();
		for (Stat stat : this.stats) {
			returnValue.add(stat.getDescription());
		}
		for (Passive passive : this.passives) {
			if(passive.getPassiveEffect().equals("REPLACED")) {
				continue;
			}
			String passiveString = passive.getDescription();
			if (passiveString.length() < charactersPerLine) {
				returnValue.add(passiveString);
			} else {
				String[] wordsArray = passiveString.split(" ");
				String line = "";
				for (int wordIndex = 0; wordIndex < wordsArray.length; wordIndex++) {
					line += wordsArray[wordIndex] + " ";
					if (wordIndex + 1 < wordsArray.length
							&& (line.length() + wordsArray[wordIndex + 1].length()) > charactersPerLine) {
						returnValue.add(line);
						line = "";
					}
				}
				returnValue.add(line);
			}
		}
		return returnValue;
	}

	public static class Stat implements Ingredient {
		private String name;
		private int amount;

		public Stat(String name, int amount) {
			this.name = name;
			this.amount = amount;
		}

		public String getStatName() {
			return this.name;
		}

		public int getStatAmount() {
			return this.amount;
		}

		public void increaseStatAmount(int amount) {
			this.amount += amount;
		}

		public String getDescription() {
			return "+ " + this.amount + " " + this.name;
		}

		@Override
		public StatList getTotalStats() {
			return new StatList(this.name, this.amount);
		}

		@Override
		public String getName() {
			return new String(this.amount + " " + this.name);
		}

		/**
		 * Finds the cheapest combination of basic items to make this stat and returns
		 * the gold price.
		 */
		@Override
		public int getTotalCost() {
			List<Item> basicItems = ItemReader.basicItems;
			List<Item> basicItemsWithCorrectStat = new ArrayList<Item>();
			for (Item basicItem : basicItems) {
				Stat basicStat = basicItem.getBasicStat();
				if(basicStat == null) {
					continue;
				}
				String basicStatName = basicStat.getStatName();
				if (basicStatName.equals(this.name)) {
					basicItemsWithCorrectStat.add(basicItem);
				}
			}
			int statRemaining = this.amount;
			int returnValue = 0;
			while (statRemaining > 0) {
				int currentHighestStatAmount = 0;
				Item currentBasicItem = null;
				for (Item basicItemWithCorrectStat : basicItemsWithCorrectStat) {
					int statAmount = basicItemWithCorrectStat.getBasicStat().getStatAmount();
					if (statAmount <= statRemaining && statAmount > currentHighestStatAmount) {
						currentHighestStatAmount = statAmount;
						currentBasicItem = basicItemWithCorrectStat;
					}
				}
				statRemaining -= currentHighestStatAmount;
				returnValue += currentBasicItem.getCost();
			}
			// TODO
			return returnValue;
		}
	}

	public static class Passive {
		private String name;
		private String effect;
		private int level;

		public Passive(String name, String effect) {
			this.name = name;
			this.effect = effect;
			this.level = 1;
		}

		public Passive(String name, String effect, int level) {
			this.name = name;
			this.effect = effect;
			this.level = level;
		}

		public String getPassiveName() {
			return this.name;
		}

		public String getPassiveEffect() {
			return this.effect;
		}

		public int getPassiveLevel() {
			return this.level;
		}

		public void changePassiveEffect(String newEffect) {
			this.effect = newEffect;
		}

		public String getDescription() {
			return this.name + " : " + this.effect;
		}
	}

	// public void
}
