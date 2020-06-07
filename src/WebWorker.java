import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.*;

public class WebWorker extends Thread {
	private int rows;
	private WebFrame wf;
	private String stat;
	private String urlS;
	
	public WebWorker(String url, int rowsUpdate, WebFrame f){
		this.urlS=url;
		this.rows=rowsUpdate;
		this.wf=f;
	}
	public void run(){
		fork();
		wf.letWorkerGo(rows, stat);
	}

	private void fork() {
		// TODO Auto-generated method stub
		InputStream input = null;
		StringBuilder contents = null;
		try {
			URL url = new URL(urlS);
			URLConnection connection = url.openConnection();
		
			// Set connect() to throw an IOException
			// if connection does not succeed in this many msecs.
			connection.setConnectTimeout(5000);
			
			connection.connect();
			input = connection.getInputStream();

			BufferedReader reader  = new BufferedReader(new InputStreamReader(input));
		
			char[] array = new char[1000];
			long startingTime = System.currentTimeMillis();
			
			int len;
			int z = 0;
			contents = new StringBuilder(1000);
			while ((len = reader.read(array, 0, array.length)) > 0) {
				contents.append(array, 0, len);
				Thread.sleep(100);
				z=z+len;
			}
			
			// Successful download if we get here
			long end = System.currentTimeMillis();
			makestat(startingTime, end, z);
		} 
		// Otherwise control jumps to a catch...
		catch(MalformedURLException ignored) {
			stat="err";
		}
		catch(InterruptedException exception) {
			// YOUR CODE HERE
			stat="interrupted";
			// deal with interruption
		}
		catch(IOException ignored) {
			stat="err.";
		}
		// "finally" clause, to close the input stream
		// in any case
		
		finally {
			try{
				if (input != null) input.close();
			}
			catch(IOException ignored) {
				ignored.printStackTrace();
			}
		}
	}
private void makestat(long startingTime, long end, int z) {
	// TODO Auto-generated method stub
	stat = new SimpleDateFormat("HH:mm:ss").format(new Date(startingTime))+ " " + (end-startingTime)+ "ms "+ z+ " bytes";
	
}
	
}
