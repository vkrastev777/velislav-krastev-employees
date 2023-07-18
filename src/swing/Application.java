package swing;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class Application {
	
	private static final Pattern datePattern1 = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}");
	private static final Pattern datePattern2 = Pattern.compile("^\\d{2}-\\d{2}-\\d{4}");
	
	private static Map<Pair<Integer, Integer>, Map<Integer, Long>> internalModel = new HashMap<Pair<Integer, Integer>, Map<Integer, Long>>();
	
	private static long getCutInDays(LocalDate startDate1, LocalDate endDate1, LocalDate startDate2, LocalDate endDate2) {

        // Find the intersection of the two periods
        LocalDate intersectionDate = startDate1.compareTo(startDate2) <= 0 ? startDate2 : startDate1;
        LocalDate intersectionEndDate = endDate1.compareTo(endDate2) >= 0 ? endDate2 : endDate1;

        // Print the number of days in the intersection
        long between = ChronoUnit.DAYS.between(intersectionDate, intersectionEndDate);
        if (between < 0) {
        	return 0;
        } else 
        	return between;
	}
   
	private static void readAndAnalyseFile(String fileName) {
		File file = new File(fileName);
		
		if (!file.getName().endsWith(".csv")) {
			showErrorMessage("File should be with .csv format!");
		}
		
		List<DataModel> model = new ArrayList<>();

        // Read the lines from the text file
        String line;
        try {
        	// Create a FileReader object for the File object
            FileReader fileReader = new FileReader(file);

            // Create a BufferedReader object for the FileReader object
            BufferedReader bufferedReader = new BufferedReader(fileReader);
			while ((line = bufferedReader.readLine()) != null) {
			    String[] split = line.split(",");
			    if (split.length != 4) {
			    	showErrorMessage("File has "+split.length+" columns but they should be 4 columns. For example EmpID, ProjectID, DateFrom, DateTo!");
			    }
			    int empID = Integer.parseInt(split[0].trim());
			    int projectID = Integer.parseInt(split[1].trim());
			    LocalDate dateFrom = getDate(split[2].trim());
			    LocalDate dateTo = getDate(split[3].trim());
			    if (dateFrom.isAfter(dateTo)) {
			    	showErrorMessage("Date from "+dateFrom.toString() + " is after "+dateTo.toString());
			    }
			    if (dateTo.isAfter(LocalDate.now())) {
			    	showErrorMessage("Date to "+dateTo.toString() + " is after today!");
			    }
			    if (dateFrom.isAfter(LocalDate.now())) {
			    	showErrorMessage("Date from "+dateFrom.toString() + "is after today!");
			    }
			    DataModel dataModel = new DataModel(empID, projectID, dateFrom, dateTo);
			    model.add(dataModel);
			}
			// Close the BufferedReader object
	        bufferedReader.close();
		} catch (Exception e) {
			e.printStackTrace();
			showErrorMessage(e.getMessage());
		}
        
        if (model.size() < 2) {
        	showErrorMessage("File is empty no data found!");
        }
        
        Set<Integer> projects = new HashSet<>();
        
        for (DataModel modelElement : model) {
        	projects.add(modelElement.getProjectID());
		}
        
        for (Integer projectId : projects) {
        	Set<Integer> projectEmployees = new HashSet<>();
        	for (DataModel modelElement : model) {
        		if (modelElement.getProjectID().intValue() == projectId.intValue()) {
        			projectEmployees.add(modelElement.getEmpID());
        		}
        	}
        	for (Integer empID : projectEmployees) {
        		DataModel employee = new DataModel();
        		for (DataModel modelElement : model) {
            		if (modelElement.getProjectID().intValue() == projectId.intValue() && empID.intValue() == modelElement.getEmpID().intValue()) {
            			employee = modelElement;
            		}
            	}
        		
        		for (DataModel modelElement : model) {
        			Pair<Integer, Integer> pair = null;
            		if (empID.intValue() != modelElement.getEmpID().intValue()) {
            			if (modelElement.getEmpID().intValue() < employee.getEmpID().intValue()) {
            				pair = Pair.of(modelElement.getEmpID(), employee.getEmpID());
            			} else {
            				pair = Pair.of(employee.getEmpID(), modelElement.getEmpID());
            			}
            			Map<Integer, Long> map = internalModel.get(pair);
        				long cutInDays = getCutInDays(modelElement.getDateFrom(), modelElement.getDateTo(), employee.getDateFrom(), employee.getDateTo());
        				if (map == null) {
        					if (cutInDays > 0) {
        						map = new HashMap<>();
        						map.put(projectId, cutInDays);
        						internalModel.put(pair, map);
        					}
        				} else {
        					if (cutInDays > 0) {
        						map.put(projectId, cutInDays);
        						internalModel.put(pair, map);
        					}
        				}
            		}
            	}
			}
		}
        
        Pair<Integer, Integer> maxPair = null;
        long maxDays = 0;
        
        for (Pair<Integer, Integer> pair : internalModel.keySet()) {
        	Map<Integer, Long> map = internalModel.get(pair);
        	Long sum = 0L;
        	for (Long days : map.values()) {
				sum = sum + days;
			}
        	if (sum > maxDays) {
        		maxDays = sum;
        		maxPair = pair;
        	}
		}
        
        Map<Integer, Long> map = internalModel.get(maxPair);
        Integer[][] data = new Integer[map.keySet().size()][4];
        int index = 0;
        for (Integer integer : map.keySet()) {
        	data[index][0] = maxPair.key();
        	data[index][1] = maxPair.value();
        	data[index][2] = integer;
        	data[index][3] = map.get(integer).intValue();
        	index++;
		}
		showTable(fileName, data);
	} 
	
	private static LocalDate getDate(String dateString) {
		// The formatter
        DateTimeFormatter formatter = null;
        
        if (datePattern1.matcher(dateString).find()) {
        	formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        } else if(datePattern2.matcher(dateString).find()) {
        	formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        } else if (dateString.compareTo("NULL") == 0) {
        	return LocalDate.now();
        } else
        {
        	showErrorMessage("Unknow Date Format in file string "+dateString);
        }

        // Parse the string to a `LocalDate` object
        return LocalDate.parse(dateString, formatter);
	}
	
	private static void showErrorMessage(String error) {
		int type = JOptionPane.ERROR_MESSAGE;

        // Show the error window
        JOptionPane.showMessageDialog(null, error, "Error", type);
        System.exit(0);
	}
	
	private static void showTable(String fileName, Integer[][] data) {
		 JFrame frame = new JFrame("Result for file "+fileName);
	        frame.setLayout(new BorderLayout());
	        
	        //showErrorMessage("error");
	        String[] columnNames = {"Employee ID #1", "Employee ID #2", "Project ID", "Days worked"};
	        DefaultTableModel model = new DefaultTableModel(data, columnNames);

	        // Create the table
	        JTable table = new JTable(model);
	        table.setPreferredScrollableViewportSize(table.getPreferredSize());

	        // Add the table to the frame
	        frame.add(new JScrollPane(table), BorderLayout.CENTER);

	        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	        frame.pack();
	        frame.setLocationRelativeTo(null);
	        frame.setVisible(true);
	}

    public static void main(String[] args) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        fileChooser.setDialogTitle("Choose a file");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        // Create a file filter that only accepts .csv files
        FileNameExtensionFilter csvFilter = new FileNameExtensionFilter("CSV files", "csv");
        fileChooser.addChoosableFileFilter(csvFilter);
        fileChooser.setFileFilter(csvFilter);
        
        int result = fileChooser.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            readAndAnalyseFile(selectedFile.getAbsolutePath());
            fileChooser.setVisible(false);
        }
    }
}