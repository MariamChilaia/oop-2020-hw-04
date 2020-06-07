import static org.junit.Assert.*;

import org.junit.Test;

public class BankTest{
	private Bank bank;
	//test 5k.
	@Test
	public void transactionsTest(){
		String[]arg = new String[] {"5k.txt","2"};
		//bank = new Bank(20,2);
		Bank.main(arg);
		
	}
	@Test
	public void transactionssmallTest(){
		String[]arg = new String[] {"small.txt","2"};
		//bank = new Bank(20,2);
		Bank.main(arg);
		
	}
	@Test
	public void transactionsTestnoArgument(){
		String[]arg = new String[] {};
		//bank = new Bank(20,2);
		Bank.main(arg);
		
	}
	
	
}