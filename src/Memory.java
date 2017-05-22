import javax.swing.table.DefaultTableModel;

public class Memory {

	Object[][] data;
	DefaultTableModel butterfly;
	private int counter;

	Memory (int size, String[] arr) {
		
		counter = 0;
		create(size,arr);
	}

	public Object[][] getData() {
		return data;
	}

	public void setData(Object[][] data) {
		this.data = data;
	}
	public void create(int size, String[] arr) {
		
		String[] columnNames = arr;
		butterfly = new DefaultTableModel(0, 0);
		butterfly.setColumnIdentifiers(columnNames);
		
		for (int j = 0; j < size; j++) {
//			if (j == 5) {
//				butterfly.addRow(new Object[] { "0","001","A","10","11" });
//			}
//			else {
				butterfly.addRow(new Object[] { j,"","","","",""});
//			}
			
		}
	}
	
	public DefaultTableModel getButterfly() {
		return butterfly;
	}

	public void setButterfly(DefaultTableModel butterfly) {
		this.butterfly = butterfly;
	}
	
	public void increaseCounter() {
		counter++;
	}
	public int getCounter() {
		return counter;
	}

	public void setCounter(int counter) {
		this.counter = counter;
	}
}