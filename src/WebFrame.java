import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;


public class WebFrame extends JFrame{
	private String fileName= "links.txt";
	private JProgressBar bar;
	private JTable table;
	private JPanel panel;
	private DefaultTableModel model;
	private JLabel completed;
	private JLabel running;
	private JLabel elapsed;
	private JScrollPane scrollpane;
	private long startingTime=0;

	private static final int WIDTH = 5;
	
	private JButton single;
	private JButton concurrent;
	private JButton stop;
	private Launcher launcher = null;
	private JTextField threadNum;
	
	private Semaphore semWorkers;
	
	private AtomicInteger runningThread;
	private AtomicInteger completedThread;
	//constructor
	public WebFrame(String sup){
		super(sup);
		
		model = new DefaultTableModel(new  String[]{"URL", "status"}, 0);
		putURLinModel();
		panel = new JPanel();
		table = new JTable(model);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
	
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
		
		scrollpane = new JScrollPane(table);
		scrollpane.setPreferredSize(new Dimension(600,300));
		
			
		//create buttons labels and textfields.
		stop = new JButton("Stop");
		stop.setEnabled(false);
		completed = new JLabel("Completed: ");
		elapsed = new JLabel("Elapsed: ");
		
		
		single= new JButton("Single Thread Fetch.");
		concurrent=new JButton("Concurrent Fetch.");
		running = new JLabel("Running: ");
		int r = model.getRowCount();
		bar= new JProgressBar(0,r);
		threadNum = new JTextField("4",WIDTH);
		threadNum.setMaximumSize(threadNum.getPreferredSize());
		
		addActionListeners();
		
        //ADD TO PANEL
		panel.add(scrollpane);
		panel.add(single);
		panel.add(concurrent);
		panel.add(threadNum);
		panel.add(running);
		panel.add(completed);
		panel.add(elapsed);
		panel.add(bar);
		panel.add(stop);
		add(panel);
		pack();
		setVisible(true);
	}
	private void addActionListeners() {
		// TODO Auto-generated method stub
		stop.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				if(launcher!=null) launcher.interrupt();
				launcher=null;
				finishFetching();
			}
		});
		single.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				if(launcher!=null)launcher.interrupt();
				int single = 1;
				launching(single);
				fetching(single);
			}

		});
		
		concurrent.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				if(launcher!=null) launcher.interrupt();
				launching(0);
				fetching(0);
			}
		});
		
		
	}
	
	

	//private void setupTSP( ) {
		// TODO Auto-generated method stub
		
	//}
	private void putURLinModel() {
		// TODO Auto-generated method stub
		try{
			BufferedReader bf = new BufferedReader(new FileReader(new File(fileName)));
			 
			while(true){
				 String line=bf.readLine();
				 if(line==null) break;
				 model.addRow(new String[]{line,""});
				//System.out.println(line);
			}
		} catch(IOException e){
			e.printStackTrace();
		}
	}
	private void launching(int threads){
		if(threads==0){
			try{
				String s = threadNum.getText();
				threads=Integer.parseInt(s);
			}catch(NumberFormatException e){
				threads= 1;
			}
		}
		launcher = new Launcher(this,threads,model.getRowCount());
		launcher.start();
	}
	
	public void fetching(int a){
		startingTime = System.currentTimeMillis();
		stop.setEnabled(true);
		//bar must have 0 value at first.
		bar.setValue(0);
		single.setEnabled(false);
		concurrent.setEnabled(false);
		for(int k = 0; k<model.getRowCount();k++){
			model.setValueAt("", k, 1);
		}
	}
	public void finishFetching(){
		concurrent.setEnabled(true);
		single.setEnabled(true);
		stop.setEnabled(false);
	}
	
	public void letWorkerGo(int r, String s){
		changeData(s,r);
		runningThread.decrementAndGet();
		completedThread.incrementAndGet();
		semWorkers.release();
		update();
	}

	private void update() {
		// TODO Auto-generated method stub
		final long time = System.currentTimeMillis() - startingTime;
		SwingUtilities.invokeLater(new Runnable() {
			
			public void run() {
				// TODO Auto-generated method stub
				completed.setText("Complete: " + completedThread);
				running.setText("Running: " +runningThread);
				elapsed.setText("Elapsed: "+time);
				//set bar.
				bar.setValue(completedThread.get());
			}
		});
	}
	public void changeData( String s, int r){
		model.setValueAt(s,r,1);
	}
	
	public class Launcher extends Thread{
		private WebFrame f;
		private int urlCount;
		
		private ArrayList<WebWorker> 
		workers = new ArrayList<WebWorker>();
		
		public Launcher(WebFrame frame, int threadCnt, int urlCount){
			semWorkers= new Semaphore(threadCnt);
			runningThread = new AtomicInteger(0);
			completedThread = new AtomicInteger(0);
			f=frame;
			this.urlCount=urlCount;
		}
		
		private void interruptWorkers(boolean b){
			if(b==true){
				for(int i = 0; i<workers.size();i++){
					workers.get(i).interrupt();
				}
			}
		}
		
		public void run(){
			runningThread.incrementAndGet();
			try{
				for (int i = 0; i < urlCount; i++) {
	                if (isInterrupted()){
	                	throw new InterruptedException();
	                }
	                String url= (String) model.getValueAt(i, 0);
	                WebWorker w= new WebWorker(url, i, f);
	                workers.add(w);
	            }
				for(WebWorker w : workers){
					if(isInterrupted()) throw new InterruptedException();
					semWorkers.acquire();runningThread.incrementAndGet();
					w.start();
					//w.interrupt();
				}
				for(WebWorker w : workers){
					w.join();
				}
			}catch(InterruptedException e){
				interruptWorkers(true);
			}
			
			runningThread.decrementAndGet();
			
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					// TODO Auto-generated method stub
					finishFetching();
				}
			});
			
		}
	}
	
	//where the main magic happens. we give Webloader to the webframe constructor for the super method.
	public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new WebFrame("WebLoader");
            }
        });
    }
	
}
