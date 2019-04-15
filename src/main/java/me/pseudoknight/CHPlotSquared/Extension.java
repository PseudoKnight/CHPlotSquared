package me.pseudoknight.CHPlotSquared;

import com.laytonsmith.PureUtilities.SimpleVersion;
import com.laytonsmith.PureUtilities.Version;
import com.laytonsmith.core.extensions.AbstractExtension;
import com.laytonsmith.core.extensions.MSExtension;

@MSExtension("CHPlotSquared")
public class Extension extends AbstractExtension {

	public Version getVersion() {
		return new SimpleVersion(2,0,0, "SNAPSHOT");
	}

	@Override
	public void onStartup() {
		System.out.println("CHPlotSquared " + getVersion() + " loaded.");
	}

	@Override
	public void onShutdown() {
		System.out.println("CHPlotSquared " + getVersion() + " unloaded.");
	}

}
