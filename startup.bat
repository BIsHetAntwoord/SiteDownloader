@echo off
set /p SiteName= "Enter the URL: "
java -cp bin;commons-codes-1.9.jar;commons-logging-1.2.jar;commons-validator-1.4.1.jar;fluent-hc-4.5.1.jar;httpclient-4.5.1.jar;httpclient-cache-4.5.1.jar;httpclient-win-4.5.1.jar;httpcore-4.4.3.jar;httpmime-4.5.1.jar;jna-4.1.0.jar;jna-platform-4.1.0.jar;jsoup-1.8.3.jar shadow.downloader.DownloaderMain %SiteName%
pause