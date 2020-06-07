

import org.junit.Test;

public class CrackerTest {
	//test hashing
	 @Test
	 public void test1(){
	
			String[]arg = new String[] {"molly"};
			Cracker.main(arg);
			
	 }
	 //test cracking
	 @Test
	 public void test2(){
		 String[]arg = new String[] {"34800e15707fae815d7c90d49de44aca97e2d759","2","2"};
		 Cracker.main(arg);
	 }
	 //test no arguments
	 @Test
	 public void test3(){
		 String[]arg = new String[]{};
		 Cracker.main(arg);
	 }
	 //test hexToArray.
	 @Test
	 public void test4(){
		 String[]arg = new String[]{};
		 Cracker.main(arg);
		 
	 }

}
