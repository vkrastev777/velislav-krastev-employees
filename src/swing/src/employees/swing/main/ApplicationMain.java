package employees.swing.main;

import org.apache.log4j.Logger;

import employees.swing.ui.EmployeesFileChooserView;

/**
 * Main class for starting the application
 */
public class ApplicationMain {
	
	private static final Logger logger = Logger.getLogger(ApplicationMain.class);

    public static void main(String[] args) {
    	logger.info("Application started!");
    	EmployeesFileChooserView.showEmployeesFileChooser();
    	logger.info("Application ended!");
    }
}