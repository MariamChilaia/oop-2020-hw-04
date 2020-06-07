// JCount.java

/*
 Basic GUI/Threading exercise.
*/

import javax.swing.*;

import java.awt.Dimension;
import java.awt.event.*;

public class JCount extends JPanel {
	
	private class Worker extends Thread{
		private boolean running;
		private int inputNum;
		public Worker(String text) {
			// TODO Auto-generated constructor stub
			inputNum= Integer.parseInt(text);
			running=false;
		}

		@Override 
		public void run(){
			for(int i = 0; i<inputNum+1; i++){
				
				if(isInterrupted()) break;
				if(i%10000==0){
				try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();//it got interrupted
						break;
					}
					
					final String outputNum = Integer.toString(i);
					SwingUtilities.invokeLater(new Runnable(){
						public void run(){
							label.setText(outputNum);
						}
					});
				}
			}
		}
	}
	private Worker work=null;
	private JButton start;
	private JButton stop;
	private JTextField tf;
	private JLabel label;
	public JCount() {
		
		// Set the JCount to use Box layout
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		
		start = new JButton("START");
		stop= new JButton ("STOP");
		
		tf = new JTextField("100000000",9);
		add(tf);
		label=new JLabel("0");
		add(label);
		add(start);
		add(stop);
		add(Box.createRigidArea(new Dimension(0,40)));
		stop.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				if(work==null){
					System.out.println("STOP button can't work, no START has happened");
					return;
				}
				if(work!=null){
					work.interrupt();
					work.running=false;
				}
			}
		});
		//override the actionPerformed for start
		start.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				if(work!=null)work.interrupt();
				work=new Worker(tf.getText());
				work.running=true;
				work.start();
				
			}
			
		});
		
		
		// YOUR CODE HERE
	}

	// Creates a frame with 4 jcounts in it.
	private static void createGUI(){
		JFrame frame = new JFrame("The Count");
		frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(new JCount());
		frame.add(new JCount());
		frame.add(new JCount());
		frame.add(new JCount());
		
		frame.pack();
		frame.setVisible(true);
	}
	
	static public void main(String[] args)  {
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				createGUI();
			}
		});
		
	}
}

