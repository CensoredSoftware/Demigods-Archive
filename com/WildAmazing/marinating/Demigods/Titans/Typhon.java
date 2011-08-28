package com.WildAmazing.marinating.Demigods.Titans;

import org.bukkit.Material;
import com.WildAmazing.marinating.Demigods.Utilities.Deity;
import com.WildAmazing.marinating.Demigods.Utilities.Divine;

public class Typhon extends Deity {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5452083554070420482L;
	private boolean CHARGE = false;
	private Material CHARGEITEM;
	private long FURYTIME;
	
	public Typhon(String p){
		super(Divine.TYPHON, p);
		FURYTIME = System.currentTimeMillis();
	}
	public boolean getCharge(){
		return CHARGE;
	}
	public void setCharge(boolean b){
		CHARGE = b;
	}
	public Material getChargeItem(){
		return CHARGEITEM;
	}
	public void setChargeItem(Material m){
		CHARGEITEM = m;
	}
	public void setFuryTime(long l){
		FURYTIME = l;
	}
	public long getFuryTime(){
		return FURYTIME;
	}
}	