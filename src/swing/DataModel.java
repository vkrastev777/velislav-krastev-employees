package swing;

import java.time.LocalDate;

public class DataModel {
	
	private Integer empID;
	private Integer projectID; 
	private LocalDate dateFrom; 
	private LocalDate dateTo;
	
	public DataModel() {
		
	}
	
	public DataModel(Integer empID, Integer projectID, LocalDate dateFrom, LocalDate dateTo) {
		super();
		this.empID = empID;
		this.projectID = projectID;
		this.dateFrom = dateFrom;
		this.dateTo = dateTo;
	}

	public Integer getEmpID() {
		return empID;
	}

	public void setEmpID(Integer empID) {
		this.empID = empID;
	}

	public Integer getProjectID() {
		return projectID;
	}

	public void setProjectID(Integer projectID) {
		this.projectID = projectID;
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
