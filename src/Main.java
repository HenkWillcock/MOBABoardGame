import java.util.List;

public class Main {
	public static void main(String args[]) {
		ItemReader reader = new ItemReader();
		CardRenderer renderer = new CardRenderer();
		List<Item> basicItems = reader.readItems("BasicItems.txt");
		List<Item> builtItems = reader.readItems("BuiltItems.txt");
		List<Item> finishedItems = reader.readItems("FinishedItems.txt");
		for(Item item: basicItems) {
			//System.out.println(item.getDescription());
			renderer.saveImageFromItem(item);
		}
		for(Item item: builtItems) {
			System.out.println(item.getDescription());
			renderer.saveImageFromItem(item);
		}
		for(Item item: finishedItems) {
			System.out.println(item.getDescription());
			renderer.saveImageFromItem(item);
		}
	}
	
}
