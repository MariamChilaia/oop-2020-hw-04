// Cracker.java
/*
 Generates SHA hashes of short strings in parallel.
*/

import java.security.*;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

public class Cracker {
	// Array of chars used to produce strings
	public static final char[] CHARS = "abcdefghijklmnopqrstuvwxyz0123456789.,-!".toCharArray();
	private static CountDownLatch cdl;
	private static int maxLen;
	private static ArrayList <Thread> workers = new ArrayList<Thread>();
	private static String target;
	private static String result;
	
	
	/*
	 Given a byte[] array, produces a hex String,
	 such as "234a6f". with 2 chars for each byte in the array.
	 (provided code)
	*/
	public static String hexToString(byte[] bytes) {
		StringBuffer buff = new StringBuffer();
		for (int i=0; i<bytes.length; i++) {
			int val = bytes[i];
			val = val & 0xff;  // remove higher bits, sign
			if (val<16) buff.append('0'); // leading 0
			buff.append(Integer.toString(val, 16));
		}
		return buff.toString();
	}
	
	/*
	 Given a string of hex byte values such as "24a26f", creates
	 a byte[] array of those values, one byte value -128..127
	 for each 2 chars.
	 (provided code)
	*/
	public static byte[] hexToArray(String hex) {
		byte[] result = new byte[hex.length()/2];
		for (int i=0; i<hex.length(); i+=2) {
		result[i/2] = (byte) Integer.parseInt(hex.substring(i, i+2), 16);
		}
		return result;
	}
	
	
	
	public static void main(String[] args) {
		//generate mode
		if(args.length==0){
			System.out.println("No arguments");
			//System.exit(1);
			return;
		}
		else if (args.length == 1) {
			System.out.println(hashGen(args[0]));
		}
		else if(args.length==3){
			String targ = args[0];
			target=targ;
			int len = Integer.parseInt(args[1]);
			maxLen=len;
			int num = 1;
			num = Integer.parseInt(args[2]);
			cdl = new CountDownLatch(num);
			String r = startThreads(num);
			System.out.print(r);
		}
		byte[] b=hexToArray("34800e15707fae815d7c90d49de44aca97e2d759");
		// args: targ len [num]
		
		// a! 34800e15707fae815d7c90d49de44aca97e2d759
		// xyz 66b27417d37e024c46526c2f6d358a754fc552f3
		
		// YOUR CODE HERE
	}
	private static class Worker extends Thread{
		private int mLen;
		private int from;
		private int to;
		public Worker( int from, int to){
			this.from=from;
			this.to=to;
			this.mLen=maxLen;
		}
		@Override
		public void run(){
			for(int start = from; start <to; start++){
				bruteForce(String.valueOf(CHARS[start]));
			}
		}
		private void bruteForce( String txt) {
			// TODO Auto-generated method stub
			if(maxLen<txt.length()) return;
			if(hashGen(txt).equals(target)){
				dehash = txt;
			}
			for(int c = 0; c<CHARS.length;c++){
				String txt2= txt+CHARS[c];
				bruteForce(txt2);
			}

			cdl.countDown(); //<-------------------
		}
	}
	private static String dehash;
	private static String startThreads(int num) {
		// TODO Auto-generated method stub

		int frac= CHARS.length/num;
		for(int i = 0; i<num;i++){
			int from =  i*frac;
			int to = from +frac;
		//	Cracker c = new Cracker();
			workers.add(new Worker( from, to));
		}
		for(Thread work : workers){
			work.start();
		}
		try{
			cdl.await();
			for(Thread w : workers)
				w.join();			
		}
		catch(InterruptedException e){
			//e.printStackTrace();
		}
		return dehash;
	}

	private static String hashGen(String string) {
		// TODO Auto-generated method stub
		try{
			MessageDigest m = MessageDigest.getInstance("SHA");
			m.update(string.getBytes());
			byte[] b=m.digest();
			return hexToString(b);
			
		}catch(NoSuchAlgorithmException e){
			//e.printStackTrace();
			
		}
		return "";
	}
}
