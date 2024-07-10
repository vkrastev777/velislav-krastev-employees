package employees.swing.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.apache.log4j.Logger;

/**
 * Class that creates and shows a table with the results from analysis
 */
public class EmployeesResultTableView {

	private static final Logger logger = Logger.getLogger(EmployeesResultTableView.class);

	private final static String COLUMN_HEADER_EMPLOYEE_1 = "Employee ID #1";
	private final static String COLUMN_HEADER_EMPLOYEE_2 = "Employee ID #2";
	private final static String COLUMN_HEADER_PROJECT_ID = "Project ID";
	private final static String COLUMN_HEADER_NUMBER_WORKED_DAYS = "Days worked";

	/**
	 * This method shows JTable with the results from analysis
	 * @param fileName - the analyzed file name
	 * @param data - the prepared data for show in JTable
	 */
	public static void showResultTable(String fileName, Integer[][] data) {
		logger.info("Printing the result!");
		logger.info("Employee ID #1, Employee ID #2, Project ID, Days worked");
		for (Integer[] integers : data) {
			logger.info(integers[0] + ", " + integers[1] + ", " + integers[2] + ", " + integers[3]);
		}
		
		JFrame frame = new JFrame("Result for file " + fileName + " the found pair of employees who have worked"
				+ "together on common projects for the longest period of time");
		frame.setLayout(new BorderLayout());

		String[] columnHeaderNames = { COLUMN_HEADER_EMPLOYEE_1, COLUMN_HEADER_EMPLOYEE_2, COLUMN_HEADER_PROJECT_ID, COLUMN_HEADER_NUMBER_WORKED_DAYS };
		DefaultTableModel model = new DefaultTableModel(data, columnHeaderNames);

		// Create the table
		JTable table = new JTable(model);
		Dimension dimension = new Dimension(400, 500);
		table.setPreferredScrollableViewportSize(dimension);

		// Add the table to the frame
		frame.add(new JScrollPane(table), BorderLayout.CENTER);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
}
