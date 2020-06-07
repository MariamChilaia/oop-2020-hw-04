// Bank.java

/*
 Creates a bunch of accounts and uses threads
 to post transactions to the accounts concurrently.
*/

import java.io.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.*;
import java.util.concurrent.BlockingQueue;

public class Bank {
	public static final int BALANCE = 1000;
	public static final int ACCOUNTS = 20;	 // number of accounts
	public static final int THREADS = 2;
	private static ArrayList<Account> accounts = new ArrayList<Account>();
	private static ArrayList<Worker> workers = new ArrayList<Worker>();
	private static BlockingQueue<Transaction> transactions = new LinkedBlockingQueue<Transaction>();
	private static final Transaction last = new Transaction(-1,0,0);
	private static int nWorkers=1;
	private int [] transNum;
	private Semaphore transSem;
	
	public Bank(int nAccs, int nWorkers){
		transNum = new int[nAccs];
		for(int i =0; i<nAccs;i++){
			Account a = new Account(null,i,BALANCE);
			accounts.add(a);
		}
		for(int i =0; i<nWorkers; i++){
			Worker w =new Worker();

			workers.add(w);
			
		}
		for(Worker w: workers) w.start();  //we started the workers and they are doing the transaction business.
		//System.out.println("movedi constructorshi");
	}
	
	public void readFile(String file) {
			try {

//			System.out.println("movedi readfileshi");
			BufferedReader reader = new BufferedReader(new FileReader(file));

			System.out.println("movedi");
			// Use stream tokenizer to get successive words from file
			StreamTokenizer tokenizer = new StreamTokenizer(reader);
			
			while (true) {
				int read = tokenizer.nextToken();
				if (read == StreamTokenizer.TT_EOF){
					for(int i = 0; i<nWorkers;i++){
						transactions.put(last);
					}
					break;  // detect EOF
				}
				int from = (int)tokenizer.nval;
				tokenizer.nextToken();
				int to = (int)tokenizer.nval;
				tokenizer.nextToken();
				int amount = (int)tokenizer.nval;
				
				// Use the from/to/amount

				//System.out.println("joinn");
				transactions.put(new Transaction(to,from,-amount));
				transactions.put(new Transaction(from,to,amount));
			/*	transSem.acquire();
					transNum[to]++;
					transNum[from]++;
				transSem.release();
				*/
				
				
			}
		}
		catch (Exception e) {
			System.exit(1);
		}
	}

	/*
	 Processes one file of transaction data
	 -fork off workers
	 -read file into the buffer
	 -wait for the workers to finish
	*/
	
	
	public class Worker extends Thread{
		@Override
		public void run(){
			try{	
			while(true){
				
					Transaction next = transactions.take();
					next.toString();
					if(next.equals(last)) break;
					else{
						Account to = accounts.get(next.getTo());
						to.transact(next); 
						Account frm = accounts.get(next.getFrom());
					}
				}
			}catch(Exception e){}
		}
	}
	/*
	 Looks at commandline args and calls Bank processing.
	*/
	public static void main(String[] args) {
		// deal with command-lines args
		if (args.length == 0) {
			System.out.println("Args: transaction-file [num-workers [limit]]");
			return;                         
			//System.exit(1);
		}
		
		
		
		String file = args[0];

		int numWorkers = 1;
		if (args.length >= 2) {
			numWorkers = Integer.parseInt(args[1]);
			nWorkers=numWorkers;
			//System.out.println("Args: transactiossssssssssn-file [num-workers [limit]]");
		}
		 //nWorkers = 4;
		try{
			createBank(numWorkers,file,ACCOUNTS);
		}catch(Exception e){
		}
		// YOUR CODE HERE
	}
	public Account getTransactionNum(int i){
		return accounts.get(i);
	}
	private static  void createBank(int nWorkers, String file, int nAccs) throws InterruptedException{
		Bank b = new Bank(nAccs,nWorkers);
		b.readFile(file);
		try{
			for(Worker w : workers){
				w.join();
			}
			for(int i = 0; i< accounts.size();i++){
				Account a = accounts.get(i);
				System.out.println(a);
			}
		}catch(InterruptedException e){
		}
	}

	
	//private static Vector<Integer> getData(String s) {
	//	Vector<Integer> res = new Vector<Integer>();
	/*	 StringTokenizer st = new StringTokenizer(s);
	     while (st.hasMoreTokens()) {
	    	 res.add(Integer.parseInt(st.nextToken()));
	     }
	     */
	//	return res;
	//}
	

}

