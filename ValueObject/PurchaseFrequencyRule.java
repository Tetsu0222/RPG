package sampleTest5;

public class PurchaseFrequencyRule implements ExcellentCustomerRule{

	@Override
	public boolean ok( PurchaseHistory history ) {
		return 10 <= history.purchaseFrequencyPerMonth;
	}

}
