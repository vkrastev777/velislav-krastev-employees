package employees.swing.model;

import java.time.LocalDate;

/**
 * Class model used to read the csv file data
 */
public class EmployeeModel {
	
	/**
	 * Employee ID
	 */
	private Integer empoyeeId;
	/**
	 * Project ID
	 */
	private Integer projectId; 
	/**
	 * Starting date
	 */
	private LocalDate dateFrom; 
	/**
	 * End date
	 */
	private LocalDate dateTo;
	
	public EmployeeModel() {
		
	}
	
	public EmployeeModel(Integer empoyeeId, Integer projectId, LocalDate dateFrom, LocalDate dateTo) {
		super();
		this.empoyeeId = empoyeeId;
		this.projectId = projectId;
		this.dateFrom = dateFrom;
		this.dateTo = dateTo;
	}

	public Integer getEmployeeId() {
		return empoyeeId;
	}

	public void setEmpployeeId(Integer empId) {
		this.empoyeeId = empId;
	}

	public Integer getProjectId() {
		return projectId;
	}

	public void setProjectId(Integer projectId) {
		this.projectId = projectId;
	}

	public LocalDate getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(LocalDate dateFrom) {
		this.dateFrom = dateFrom;
	}

	public LocalDate getDateTo() {
		return dateTo;
	}

	public void setDateTo(LocalDate dateTo) {
		this.dateTo = dateTo;
	}
	
	

}
