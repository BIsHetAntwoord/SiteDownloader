package shadow.downloader;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class DownloaderPoolExecutor extends ThreadPoolExecutor
{
    public DownloaderPoolExecutor(int coreno, int maxno, int time, TimeUnit timeunit, BlockingQueue<Runnable> commands)
    {
        super(coreno,maxno,time,timeunit,commands);
    }
    @Override
    public void afterExecute(Runnable r, Throwable t)
    {
        if(this.getActiveCount() == 1 && this.getQueue().size() == 0)
            this.shutdown();
    }
    
}
