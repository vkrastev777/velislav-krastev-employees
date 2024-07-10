package employees.swing.ui;

import java.awt.Dimension;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.log4j.Logger;

import employees.swing.service.EmployeesAnalyseService;

/**
 * Main starting window for choosing the file for analysis
 */
public class EmployeesFileChooserView {
	
	private static final Logger logger = Logger.getLogger(EmployeesFileChooserView.class);
	
	private final static String USER_HOME = "user.home";
	private final static String TITLE = "Employees Application please choose a csv file for analyse";
	private final static String DESCRIPTION_CSV_FILES = "CSV files";
	private final static String FILE_EXTENSION_CSV_FILE = "csv";
	
	/**
	 * Creates and shows the file chooser dialog. Only csv files allowed
	 */
	public static void showEmployeesFileChooser() {
		JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(System.getProperty(USER_HOME)));
        fileChooser.setDialogTitle(TITLE);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        // Create a file filter that only accepts .csv files
        FileNameExtensionFilter csvFilter = new FileNameExtensionFilter(DESCRIPTION_CSV_FILES, FILE_EXTENSION_CSV_FILE);
        fileChooser.addChoosableFileFilter(csvFilter);
        fileChooser.setFileFilter(csvFilter);
        Dimension dimension = new Dimension(800, 600);
        fileChooser.setPreferredSize(dimension);
        
        int result = fileChooser.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            logger.info("Selected file that will be analysed "+selectedFile.getAbsolutePath());
            EmployeesAnalyseService.analyseFile(selectedFile.getAbsolutePath());
            fileChooser.setVisible(false);
        }
	}
}
