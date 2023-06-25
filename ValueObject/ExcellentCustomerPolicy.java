package sampleTest5;

import java.util.HashSet;
import java.util.Set;

public class ExcellentCustomerPolicy {
	
	private final Set<ExcellentCustomerRule> rules;
	
	ExcellentCustomerPolicy(){
		rules = new HashSet<>();
	}
	
	/*
	 * ルールの追加
	 * 
	 * @param rule ルール
	 */
	void add( final ExcellentCustomerRule rule ) {
		rules.add( rule );
	}
	
	/*
	 * @param history 購入履歴
	 * @return ルールをすべて満たす場合true(優良顧客である）
	 */
	boolean complyWithAll( final PurchaseHistory history ) {
		
		for( ExcellentCustomerRule each : rules ) {
			if( !each.ok( history )) {
				return false;
			}
		}
		
		return true;
	}
}
