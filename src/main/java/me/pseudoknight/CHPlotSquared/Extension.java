package me.pseudoknight.CHPlotSquared;

import com.laytonsmith.PureUtilities.SimpleVersion;
import com.laytonsmith.PureUtilities.Version;
import com.laytonsmith.core.Static;
import com.laytonsmith.core.extensions.AbstractExtension;
import com.laytonsmith.core.extensions.MSExtension;

import java.util.logging.Level;

@MSExtension("CHPlotSquared")
public class Extension extends AbstractExtension {

	private static final Version VERSION = new SimpleVersion(4,0,0);

	public Version getVersion() {
		return VERSION;
	}

	@Override
	public void onStartup() {
		Static.getLogger().log(Level.INFO, "CHPlotSquared " + getVersion() + " loaded.");
	}

	@Override
	public void onShutdown() {
		Static.getLogger().log(Level.INFO, "CHPlotSquared " + getVersion() + " unloaded.");
	}

}
