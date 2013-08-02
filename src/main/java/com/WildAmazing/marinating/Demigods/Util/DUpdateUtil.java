package com.WildAmazing.marinating.Demigods.Util;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilderFactory;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginDescriptionFile;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DUpdateUtil
{
	private static final BukkitDevDownload bukkitDev = new BukkitDevDownload();

	public static boolean check()
	{
		return bukkitDev.updateNeeded();
	}

	public static String getLatestVersion()
	{
		check();
		return bukkitDev.getVersion();
	}

	public static boolean execute()
	{
		try
		{
			// Define variables
			byte[] buffer = new byte[1024];
			int read = 0;
			int bytesTransferred = 0;
			String downloadLink = bukkitDev.getJarLink();

			DMiscUtil.consoleMSG("info", "Attempting to download latest version...");

			// Set latest build URL
			URL plugin = new URL(downloadLink);

			// Open connection to latest build and set user-agent for download, also determine file size
			URLConnection pluginCon = plugin.openConnection();
			pluginCon.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2"); // FIXES 403 ERROR
			int contentLength = pluginCon.getContentLength();

			// Check for update directory
			File updateFolder = new File("plugins" + File.separator + Bukkit.getUpdateFolder());
			if(!updateFolder.exists()) updateFolder.mkdir();

			// Create new .jar file and add it to update directory
			File pluginUpdate = new File("plugins" + File.separator + Bukkit.getUpdateFolder() + File.separator + "Demigods.jar");
			DMiscUtil.consoleMSG("info", "File will been written to: " + pluginUpdate.getCanonicalPath());

			InputStream is = pluginCon.getInputStream();
			OutputStream os = new FileOutputStream(pluginUpdate);

			while((read = is.read(buffer)) > 0)
			{
				os.write(buffer, 0, read);
				bytesTransferred += read;

				if(contentLength > 0)
				{
					// Determine percent of file and add it to variable
					int percentTransferred = (int) (((float) bytesTransferred / contentLength) * 100);

					if(percentTransferred != 100)
					{
						DMiscUtil.consoleMSG("info", "Download progress: " + percentTransferred + "%");
					}
				}
			}

			is.close();
			os.flush();
			os.close();

			// Download complete!
			DMiscUtil.consoleMSG("info", "Download complete!");
			DMiscUtil.consoleMSG("info", "Update will complete on next server reload.");
			return true;
		}
		catch(MalformedURLException ex)
		{
			DMiscUtil.consoleMSG("warning", "Error accessing URL: " + ex);
		}
		catch(FileNotFoundException ex)
		{
			DMiscUtil.consoleMSG("warning", "Error accessing URL: " + ex);
		}
		catch(IOException ex)
		{
			DMiscUtil.consoleMSG("warning", "Error downloading file: " + ex);
		}
		return false;
	}

	static class BukkitDevDownload
	{
		private URL filesFeed;
		private String version;
		private String link;
		private String jarLink;

		public BukkitDevDownload()
		{
			try
			{
				this.filesFeed = new URL("http://dev.bukkit.org/server-mods/demigods/files.rss");
			}
			catch(Exception e)
			{
				DMiscUtil.consoleMSG("severe", "Could not connect to BukkitDev.");
			}
		}

		public synchronized boolean updateNeeded()
		{
			try
			{
				InputStream input = this.filesFeed.openConnection().getInputStream();
				Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(input);

				Node latestFile = document.getElementsByTagName("item").item(0);
				NodeList children = latestFile.getChildNodes();

				this.version = children.item(1).getTextContent().replaceAll("[a-zA-Z ]", "");
				try
				{
					this.link = children.item(3).getTextContent();
				}
				catch(Exception e)
				{
					DMiscUtil.consoleMSG("warning", "Failed to find download page.");
				}
				input.close();

				try
				{
					input = (new URL(this.link)).openConnection().getInputStream();
				}
				catch(Exception e)
				{
					DMiscUtil.consoleMSG("warning", "Failed to open connection with download page.");
				}

				BufferedReader reader = new BufferedReader(new InputStreamReader(input));
				String line;

				while((line = reader.readLine()) != null)
				{
					if(line.trim().startsWith("<li class=\"user-action user-action-download\">"))
					{
						this.jarLink = line.substring(line.indexOf("href=\"") + 6, line.lastIndexOf("\""));
						break;
					}
				}

				reader.close();
				input.close();

				PluginDescriptionFile pdf = DMiscUtil.getPlugin().getDescription();
				String currentVersion = pdf.getVersion();
				if(!currentVersion.equals(this.version)) return true;
			}
			catch(Exception e)
			{
				DMiscUtil.consoleMSG("warning", "Failed to read download page.");
			}

			return false;
		}

		public String getVersion()
		{
			return this.version;
		}

		public String getLink()
		{
			return this.link;
		}

		public String getJarLink()
		{
			return this.jarLink;
		}
	}
}
