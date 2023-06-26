package sampleTest5;

public class GoldCustomerPurchaseAmountRule implements ExcellentCustomerRule {

	@Override
	public boolean ok( final PurchaseHistory history ) {
		return 100000 <= history.totalAmount;
	}
}
