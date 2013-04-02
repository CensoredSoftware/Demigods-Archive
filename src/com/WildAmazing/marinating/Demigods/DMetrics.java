package com.WildAmazing.marinating.Demigods;

import com.WildAmazing.marinating.Demigods.Metrics.Graph;

import java.io.IOException;
import java.util.ArrayList;

public class DMetrics
{
	static Demigods plugin;

	public DMetrics(Demigods d)
	{
		plugin = d;
	}

	public static void allianceStatsPastWeek()
	{
		try
		{
			Metrics metrics = new Metrics(plugin);

			// New Graph
			Graph graph = metrics.createGraph("Alliances for the Past Week");

			// Gods
			graph.addPlotter(new Metrics.Plotter("Gods")
			{

				@Override
				public int getValue()
				{
					ArrayList<String> gods = new ArrayList<String>();
					for(String s : DUtil.getFullParticipants())
					{
						if(DUtil.getAllegiance(s).equalsIgnoreCase("god"))
						{
							if(DSave.hasData(s, "LASTLOGINTIME"))
							{
								if((Long) DSave.getData(s, "LASTLOGINTIME") < System.currentTimeMillis() - 604800000) continue;
							}
							gods.add(s);
						}
					}
					int numGods = gods.size();
					return numGods; // Number of players who are in other alliances
				}

			});

			// Titans
			graph.addPlotter(new Metrics.Plotter("Titans")
			{

				@Override
				public int getValue()
				{
					ArrayList<String> titans = new ArrayList<String>();
					for(String s : DUtil.getFullParticipants())
					{
						if(DUtil.getAllegiance(s).equalsIgnoreCase("titan"))
						{
							if(DSave.hasData(s, "LASTLOGINTIME"))
							{
								if((Long) DSave.getData(s, "LASTLOGINTIME") < System.currentTimeMillis() - 604800000) continue;
							}
							titans.add(s);
						}
					}
					int numTitans = titans.size();
					return numTitans; // Number of players who are in other alliances
				}

			});

			// Giants
			graph.addPlotter(new Metrics.Plotter("Giants")
			{

				@Override
				public int getValue()
				{
					ArrayList<String> giants = new ArrayList<String>();
					for(String s : DUtil.getFullParticipants())
					{
						if(DUtil.getAllegiance(s).equalsIgnoreCase("giant"))
						{
							if(DSave.hasData(s, "LASTLOGINTIME"))
							{
								if((Long) DSave.getData(s, "LASTLOGINTIME") < System.currentTimeMillis() - 604800000) continue;
							}
							giants.add(s);
						}
					}
					int numGiants = giants.size();
					return numGiants; // Number of players who are in other alliances
				}

			});

			// Other
			graph.addPlotter(new Metrics.Plotter("Other")
			{

				@Override
				public int getValue()
				{
					ArrayList<String> others = new ArrayList<String>();
					for(String s : DUtil.getFullParticipants())
					{
						if(!DUtil.getAllegiance(s).equalsIgnoreCase("god") && (!DUtil.getAllegiance(s).equalsIgnoreCase("titan")) && (!DUtil.getAllegiance(s).equalsIgnoreCase("giant")))
						{
							if(DSave.hasData(s, "LASTLOGINTIME"))
							{
								if((Long) DSave.getData(s, "LASTLOGINTIME") < System.currentTimeMillis() - 604800000) continue;
							}
							others.add(s);
						}
					}
					int numOthers = others.size();
					return numOthers; // Number of players who are in other alliances
				}

			});

			metrics.start();
		}
		catch(IOException e)
		{
			DUtil.consoleMSG("severe", e.getMessage());
		}
	}

	public static void allianceStatsAllTime()
	{
		try
		{
			Metrics metrics = new Metrics(plugin);

			// New Graph
			Graph graph = metrics.createGraph("Alliances for All Time");

			// Gods
			graph.addPlotter(new Metrics.Plotter("Gods")
			{

				@Override
				public int getValue()
				{
					ArrayList<String> gods = new ArrayList<String>();
					for(String s : DUtil.getFullParticipants())
					{
						if(DUtil.getAllegiance(s).equalsIgnoreCase("god"))
						{
							gods.add(s);
						}
					}
					int numGods = gods.size();
					return numGods; // Number of players who are in the God Alliance
				}

			});

			// Titans
			graph.addPlotter(new Metrics.Plotter("Titans")
			{

				@Override
				public int getValue()
				{
					ArrayList<String> titans = new ArrayList<String>();
					for(String s : DUtil.getFullParticipants())
					{
						if(DUtil.getAllegiance(s).equalsIgnoreCase("titan"))
						{
							titans.add(s);
						}
					}
					int numTitans = titans.size();
					return numTitans; // Number of players who are in the Titan Alliance
				}

			});

			// Giants
			graph.addPlotter(new Metrics.Plotter("Giants")
			{

				@Override
				public int getValue()
				{
					ArrayList<String> giants = new ArrayList<String>();
					for(String s : DUtil.getFullParticipants())
					{
						if(DUtil.getAllegiance(s).equalsIgnoreCase("giant"))
						{
							giants.add(s);
						}
					}
					int numGiants = giants.size();
					return numGiants; // Number of players who are in the Giant Alliance
				}

			});

			// Other
			graph.addPlotter(new Metrics.Plotter("Other")
			{

				@Override
				public int getValue()
				{
					ArrayList<String> others = new ArrayList<String>();
					for(String s : DUtil.getFullParticipants())
					{
						if(!DUtil.getAllegiance(s).equalsIgnoreCase("god") && (!DUtil.getAllegiance(s).equalsIgnoreCase("titan")) && (!DUtil.getAllegiance(s).equalsIgnoreCase("giant")))
						{
							others.add(s);
						}
					}
					int numOthers = others.size();
					return numOthers; // Number of players who are in other alliances
				}

			});

			metrics.start();
		}
		catch(IOException e)
		{
			DUtil.consoleMSG("severe", e.getMessage());
		}
	}
}
