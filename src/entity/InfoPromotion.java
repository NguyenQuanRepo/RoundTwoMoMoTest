package entity;

public class InfoPromotion {
	private int prevLimitedBudget;
	private String date;	
	
	public InfoPromotion() {
	}
	
	public InfoPromotion(int currentLimitedBudget, String date) {
		super();
		this.prevLimitedBudget = currentLimitedBudget;
		this.date = date;
	}
	public int getPrevLimitedBudget() {
		return prevLimitedBudget;
	}
	public void setPrevLimitedBudget(int prevLimitedBudget) {
		this.prevLimitedBudget = prevLimitedBudget;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	
	
}
