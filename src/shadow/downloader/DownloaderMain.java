package shadow.downloader;

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;


public class DownloaderMain
{
	private static final int THREADS = 8;
	private DownloaderPoolExecutor executor;
	private ArrayList<String> downloaded = new ArrayList<String>();
	private Object lock = new Object();
	public DownloaderMain()
	{
	    BlockingQueue<Runnable> commands = new ArrayBlockingQueue<Runnable>(DownloaderMain.THREADS*2);
	    this.executor = new DownloaderPoolExecutor(DownloaderMain.THREADS,DownloaderMain.THREADS*2,3,TimeUnit.MINUTES,commands);
	}
	public void putFile(String file)
	{
		synchronized(lock)
		{
			if(!this.downloaded.contains(file))
			{
				this.downloaded.add(file);
				this.executor.execute(new DownloaderThread(this,file));
			}
		}
	}
	public static void main(String[] args)
	{
		if(args.length < 1)
		{
			System.err.println("No arguments given");
			return;
		}
		else
		{
            DownloaderMain main = new DownloaderMain();
		    for(String s : args)
		        main.putFile(s);
		}
	}
}
