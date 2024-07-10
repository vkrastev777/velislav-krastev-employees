package employees.swing.ui;

import java.awt.Dimension;

import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

import org.apache.log4j.Logger;

/**
 * Class implementing show of error message.
 */
public class EmployeesErrorMessage {

	private static final Logger logger = Logger.getLogger(EmployeesErrorMessage.class);
	
	private static final int PANE_WIDTH = 500;
	private static final int PANE_HEIGHT = 125;
	
	private static final String TERMINATION_MESSAGE =  "Error! Application terminates after error !";

	/**
	 * Method implementing show of error message. Every error message is considered
	 * critical and the application terminates after show of the error message to user.
	 * 
	 * @param error - the message to show
	 */
	public static void showErrorMessage(String error) {
		int type = JOptionPane.ERROR_MESSAGE;
		logger.info("Error message to show to user: " + error);
		String[] errors = { error };
		JList<String> listErrors = new JList<>(errors);
		JScrollPane scrollpane = new JScrollPane(listErrors);
		scrollpane.setPreferredSize(new Dimension(PANE_WIDTH, PANE_HEIGHT));

		// Show the error window
		JOptionPane.showMessageDialog(null, scrollpane, "Error", type);
		logger.info(TERMINATION_MESSAGE);
		System.exit(0);
	}

}
