package shadow.downloader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class DownloaderThread implements Runnable
{
	private DownloaderMain main;
	private String file;
	DownloaderThread(DownloaderMain main, String file)
	{
		this.main = main;
		this.file = file;
	}
	public void run()
	{
	    synchronized(System.out)
	    {
	        System.out.println("Downloading file " + this.file);
	    }
	    CloseableHttpClient httpclient = HttpClients.createDefault();
	    HttpGet method = new HttpGet(this.file);
	    try(CloseableHttpResponse response = httpclient.execute(method))
	    {
	        File outfile = makeFile(this.file);
	        try(FileOutputStream output = new FileOutputStream(outfile))
	        {
	            HttpEntity entity = response.getEntity();
	            entity.writeTo(output);
	            EntityUtils.consume(entity);
	            Header type = response.getLastHeader("Content-Type");
	            if(type != null)
	            {
	                HeaderElement[] elems = type.getElements();
	                if(elems[0].getName().equalsIgnoreCase("text/html"))
	                {
	                    String charset = "UTF-8";
	                    for(HeaderElement elem : elems)
	                    {
	                        if(elem.getName().equalsIgnoreCase("Charset"))
	                        {
	                            String value = elem.getValue();
	                            if(value != null)
	                                charset = value;
	                        }
	                    }
	                    HTMLParser parser = new HTMLParser(outfile,this.main,charset,new URL(this.file));
	                    parser.searchLinks();
	                }
	            }
	        }
	    }
	    catch(IOException i)
	    {
	        synchronized(System.err)
	        {
	            i.printStackTrace();
	        }
	    }
	    try
	    {
	        httpclient.close();
	    }
	    catch(IOException i)
	    {
	        synchronized(System.err)
	        {
	            i.printStackTrace();
	        }
	    }
	}
	private static File makeFile(String urlstr)
	{
	    try
	    {
	        URL url = new URL(urlstr);
	        String path = url.getHost();
	        String urlpath = url.getPath();
	        if(urlpath.equals(""))
	            path += "/" + DownloaderSettings.DEFAULT_FILE;
	        else if(urlpath.endsWith("/"))
	            path += DownloaderSettings.DEFAULT_FILE;
	        else
	            path += "/" + urlpath;
	        File f = new File(path);
	        File parentfile = f.getParentFile();
	        if(parentfile != null && !parentfile.exists())
	            parentfile.mkdirs();
	        return f;
	    }
	    catch(MalformedURLException m)
	    {
	        return null;
	    }
	}
}
