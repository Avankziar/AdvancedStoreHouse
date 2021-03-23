package main.java.me.avankziar.general.objects;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.scheduler.BukkitRunnable;

import main.java.me.avankziar.spigot.ash.AdvancedStoreHouse;

public class ChestAnimation
{
	private Location chestLocation;
	
	public ChestAnimation(Location chestLocation)
	{
		setChestLocation(chestLocation);
	}

	public Location getChestLocation()
	{
		return chestLocation;
	}

	public void setChestLocation(Location chestLocation)
	{
		this.chestLocation = chestLocation;
	}
	
	public void startSingleChestAnimation(int animationLenght, Particle particle)
	{
		new BukkitRunnable()
		{
			long end = System.currentTimeMillis()+animationLenght;
			@Override
			public void run()
			{
				if(System.currentTimeMillis() > end)
				{
					cancel();
					return;
				}
				//Unten Unten Links
				chestLocation.getWorld().spawnParticle(particle, chestLocation,
                        1,
                        0, 0, 0,
                        0);
				chestLocation.getWorld().spawnParticle(particle, chestLocation.clone().add(0, 0, 0.5),
                        1,
                        0, 0, 0,
                        0);
				//Unten Unten Rechts
				chestLocation.getWorld().spawnParticle(particle, chestLocation.clone().add(0, 0, 1),
                        1,
                        0, 0, 0,
                        0);
				//Unten Oben Links
				chestLocation.getWorld().spawnParticle(particle, chestLocation.clone().add(0.5, 0, 0),
                        1,
                        0, 0, 0,
                        0);
				chestLocation.getWorld().spawnParticle(particle, chestLocation.clone().add(1, 0, 0),
                        1,
                        0, 0, 0,
                        0);
				//Unten Oben Rechts
				chestLocation.getWorld().spawnParticle(particle, chestLocation.clone().add(0.5, 0, 1),
                        1,
                        0, 0, 0,
                        0);
				chestLocation.getWorld().spawnParticle(particle, chestLocation.clone().add(1, 0, 0.5),
                        1,
                        0, 0, 0,
                        0);
				chestLocation.getWorld().spawnParticle(particle, chestLocation.clone().add(1, 0, 1),
                        1,
                        0, 0, 0,
                        0);
				//Oben Unten Links
				chestLocation.getWorld().spawnParticle(particle, chestLocation.clone().add(0, 0.5, 0),
                        1,
                        0, 0, 0,
                        0);
				chestLocation.getWorld().spawnParticle(particle, chestLocation.clone().add(0, 1, 0),
                        1,
                        0, 0, 0,
                        0);
				//Oben Unten Rechts
				chestLocation.getWorld().spawnParticle(particle, chestLocation.clone().add(0, 0.5, 1),
                        1,
                        0, 0, 0,
                        0);
				chestLocation.getWorld().spawnParticle(particle, chestLocation.clone().add(0, 1, 0.5),
                        1,
                        0, 0, 0,
                        0);
				chestLocation.getWorld().spawnParticle(particle, chestLocation.clone().add(0, 1, 1),
                        1,
                        0, 0, 0,
                        0);
				//Oben Oben Links
				chestLocation.getWorld().spawnParticle(particle, chestLocation.clone().add(0.5, 1, 0),
                        1,
                        0, 0, 0,
                        0);
				chestLocation.getWorld().spawnParticle(particle, chestLocation.clone().add(1, 0.5, 0),
                        1,
                        0, 0, 0,
                        0);
				chestLocation.getWorld().spawnParticle(particle, chestLocation.clone().add(1, 1, 0),
                        1,
                        0, 0, 0,
                        0);
				//Oben Oben Rechts
				chestLocation.getWorld().spawnParticle(particle, chestLocation.clone().add(0.5, 1, 1),
                        1,
                        0, 0, 0,
                        0);
				chestLocation.getWorld().spawnParticle(particle, chestLocation.clone().add(1, 0.5, 1),
                        1,
                        0, 0, 0,
                        0);
				chestLocation.getWorld().spawnParticle(particle, chestLocation.clone().add(1, 1, 0.5),
                        1,
                        0, 0, 0,
                        0);
				chestLocation.getWorld().spawnParticle(particle, chestLocation.clone().add(1, 1, 1),
                        1,
                        0, 0, 0,
                        0);

			}
		}.runTaskTimer(AdvancedStoreHouse.getPlugin(), 0L, 5L);
	}
}
