package com.clashnia.ClashniaUpdate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Logger;

import org.bukkit.plugin.PluginDescriptionFile;

import com.WildAmazing.marinating.Demigods.DUtil;

public class DemigodsUpdate {
	static Logger log = Logger.getLogger("Minecraft");
	
	public static boolean shouldUpdate() {
		PluginDescriptionFile pdf = DUtil.getPlugin().getDescription();
		String latestVersion = pdf.getVersion();
		String onlineVersion;
		
		if (latestVersion.startsWith("d")) return false; // development versions shouldn't downgrade

		try {
			URL version = new URL("http://www.clashnia.com/plugins/demigods/version.txt");
			URLConnection versionCon = version.openConnection();
			versionCon.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2"); //FIXES 403 ERROR
			BufferedReader in = new BufferedReader(new InputStreamReader(versionCon.getInputStream()));
			onlineVersion = in.readLine();
			if (latestVersion.equals(onlineVersion) || onlineVersion.startsWith("d")) {
				log.info("[Demigods] Demigods is up to date. Version "
						+ latestVersion);
				in.close();
				return false;
			} else {
				log.info("[Demigods] Demigods is not up to date...");
				log.info("[Demigods] Current version: " + latestVersion);
				log.info("[Demigods] New version: " + onlineVersion);
				in.close();
				return true;
			}
		} catch (MalformedURLException ex) {
			log.warning("[Demigods] Error accessing version URL.");
		} catch (IOException ex) {
			log.warning("[Demigods] Error checking for update.");
		}
		return false;
	}
}