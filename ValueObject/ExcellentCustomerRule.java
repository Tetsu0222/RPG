package sampleTest5;

public interface ExcellentCustomerRule {

	/*
	 * @return 条件を満たす場合はtrue
	 * @param history 購入履歴
	 */
	boolean ok( final PurchaseHistory history );
}
