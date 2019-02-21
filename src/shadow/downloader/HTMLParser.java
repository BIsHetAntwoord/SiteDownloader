package shadow.downloader;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.validator.routines.UrlValidator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class HTMLParser
{
    private Document doc;
    private DownloaderMain main;
    private URL referer;
    public HTMLParser(File input, DownloaderMain main, String contenttype, URL referer) throws IOException
    {
        this.doc = Jsoup.parse(input,contenttype);
        this.main = main;
        this.referer = referer;
    }
    public void searchLinks()
    {
        this.search("a","href");
        this.search("script","src");
        this.search("link","href");
        this.search("source","src");
        this.search("embed","src");
        this.search("iframe","src");
        this.search("img","src");
        this.search("area","href");
        this.search("track","src");
    }
    private void search(String tag, String attr)
    {
        Elements links = this.doc.getElementsByTag(tag);
        for(Element elem : links)
        {
            String src = elem.attr(attr);
            if(src != null)
            {
                String file = HTMLParser.getFullUrl(referer,src);
                if(file != null)
                    main.putFile(file);
            }
        }
    }
    public static String getFullUrl(URL referer, String link)
    {
        UrlValidator validator = new UrlValidator(UrlValidator.ALLOW_LOCAL_URLS | UrlValidator.ALLOW_ALL_SCHEMES | UrlValidator.ALLOW_2_SLASHES);
        if(validator.isValid(link))
        {
            if(DownloaderSettings.DOWNLOAD_EXTERNALS)
            {
                String substr = link.substring(0,link.indexOf(':'));
                switch(substr.toLowerCase())
                {
                    case "http":
                    case "ftp":
                    case "https":
                        return link;
                    default:
                        return null;
                }
            }
            else
                return null;
        }
        else if(link.startsWith("//"))
        {
            if(DownloaderSettings.DOWNLOAD_EXTERNALS)
            {
                StringBuilder builder = new StringBuilder();
                builder.append(referer.getProtocol());
                builder.append(":");
                builder.append(link);
                return builder.toString();
            }
            else
                return null;
        }
        else if(link.startsWith("/"))
        {
            StringBuilder builder = new StringBuilder();
            builder.append(referer.getProtocol());
            builder.append("://");
            builder.append(referer.getHost());
            builder.append(link);
            return builder.toString();
        }
        else if(link.startsWith("#"))
            return null;
        else
        {
            StringBuilder builder = new StringBuilder();
            builder.append(referer.getProtocol());
            builder.append("://");
            builder.append(referer.getHost());
            String refpath = referer.getPath();
            int index = refpath.lastIndexOf("/");
            if(index >= 0)
                refpath = refpath.substring(0,index);
            builder.append(refpath);
            builder.append("/");
            builder.append(link);
            return builder.toString();
        }
    }
}
