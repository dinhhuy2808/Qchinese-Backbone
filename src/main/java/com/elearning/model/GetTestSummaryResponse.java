package com.elearning.model;

public class GetTestSummaryResponse {
	private int countTests;
	private int currentEligibleTest;
	private int totalTestByHsk;
	public int getCountTests() {
		return countTests;
	}
	public void setCountTests(int countTests) {
		this.countTests = countTests;
	}
	public int getCurrentEligibleTest() {
		return currentEligibleTest;
	}
	public void setCurrentEligibleTest(int currentEligibleTest) {
		this.currentEligibleTest = currentEligibleTest;
	}
	public int getTotalTestByHsk() {
		return totalTestByHsk;
	}
	public void setTotalTestByHsk(int totalTestByHsk) {
		this.totalTestByHsk = totalTestByHsk;
	}
}
