package me.pseudoknight.CHPlotSquared;

import com.laytonsmith.PureUtilities.Version;
import com.laytonsmith.abstraction.MCLocation;
import com.laytonsmith.annotations.api;
import com.laytonsmith.core.MSVersion;
import com.laytonsmith.core.ObjectGenerator;
import com.laytonsmith.core.Static;
import com.laytonsmith.core.constructs.CArray;
import com.laytonsmith.core.constructs.CBoolean;
import com.laytonsmith.core.constructs.CNull;
import com.laytonsmith.core.constructs.CString;
import com.laytonsmith.core.constructs.Target;
import com.laytonsmith.core.environments.Environment;
import com.laytonsmith.core.exceptions.CRE.*;
import com.laytonsmith.core.exceptions.ConfigRuntimeException;
import com.laytonsmith.core.functions.AbstractFunction;
import com.laytonsmith.core.natives.interfaces.Mixed;
import com.plotsquared.core.PlotSquared;
import com.plotsquared.core.location.Location;
import com.plotsquared.core.plot.Plot;
import com.plotsquared.core.plot.PlotArea;
import com.plotsquared.core.plot.PlotId;
import com.sk89q.worldedit.math.BlockVector3;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;

public class Functions {
	public static String docs() {
		return "These functions are used to access and modify PlotSquared data.";
	}

	protected static abstract class PlotSquaredFunction extends AbstractFunction {
		@Override
		public String getName() {
			return getClass().getSimpleName();
		}

		@Override
		public boolean isRestricted() {
			return true;
		}

		@Override
		public Boolean runAsync() {
			return false;
		}

		@Override
		public Version since() {
			return MSVersion.V3_3_1;
		}
	}

	@api
	public static class plot_list extends PlotSquaredFunction {
		@Override
		public String docs() {
			return "array {world, [uuid]} Returns an array of plot ids for a world. Optionally filtered by owner UUID."
					+ " Throws InvalidWorldException if world is not a plot world.";
		}

		@Override
		public Integer[] numArgs() {
			return new Integer[]{1,2};
		}

		@Override
		public Mixed exec(Target t, Environment environment, Mixed... args) throws ConfigRuntimeException {
			String world = args[0].val();
			if(!PlotSquared.get().getPlotAreaManager().hasPlotArea(world)) {
				throw new CREInvalidWorldException("Invalid plot world", t);
			}
			Set<PlotArea> areas = PlotSquared.get().getPlotAreaManager().getPlotAreasSet(world);
			Collection<Plot> plots = new ArrayList<>();
			if(args.length == 2){
				UUID uuid = Static.GetUUID(args[1], t);
				for(PlotArea area : areas) {
					plots.addAll(area.getPlots(uuid));
				}
			} else {
				for(PlotArea area : areas) {
					plots.addAll(area.getPlots());
				}
			}
			CArray cplots = new CArray(t);
			for(Plot plot : plots) {
				cplots.push(new CString(plot.getId().toString(), t), t);
			}
			return cplots;
		}

		@Override
		public Class<? extends CREThrowable>[] thrown() {
			return new Class[]{CREInvalidWorldException.class, CRELengthException.class, 
					CREIllegalArgumentException.class};
		}
	}

	@api
	public static class plot_at_loc extends PlotSquaredFunction {
		@Override
		public String docs() {
			return "string {location} Returns the plot id at the specified location, null if no plot exists there.";
		}

		@Override
		public Integer[] numArgs() {
			return new Integer[]{1};
		}

		@Override
		public Mixed exec(Target t, Environment environment, Mixed... args) throws ConfigRuntimeException {
			MCLocation l = ObjectGenerator.GetGenerator().location(args[0], null, t);
			Location loc = Location.at(l.getWorld().getName(), BlockVector3.at(l.getBlockX(), l.getBlockY(), l.getBlockZ()));
			PlotArea area = loc.getPlotArea();
			if(area == null){
				return CNull.NULL;
			}
			PlotId plotid = area.getPlotManager().getPlotId(loc.getX(), loc.getY(), loc.getZ());
			if(plotid == null){
				return CNull.NULL;
			}
			return new CString(plotid.toString(), t);
		}

		@Override
		public Class<? extends CREThrowable>[] thrown() {
			return new Class[]{CREFormatException.class};
		}
	}

	@api
	public static class plot_has_player extends PlotSquaredFunction {
		@Override
		public String docs() {
			return "mixed {location, uuid | world, plotarea, plotid, uuid} Returns whether the player is added to a plot."
					+ " Returns null if no plot exists at that location, or by that plotarea or plot id."
					+ " Throws a FormatException if plotid is not two numbers separated by a comma or semi-colon."
					+ " Throws InvalidWorldException if world is not a plot world.";
		}

		@Override
		public Integer[] numArgs() {
			return new Integer[]{2,4};
		}

		@Override
		public Mixed exec(Target t, Environment environment, Mixed... args) throws ConfigRuntimeException {
			Plot plot = GetPlot(t, args);
			if(plot == null){
				return CNull.NULL;
			}
			UUID uuid = Static.GetUUID(args[args.length - 1], t);
			return CBoolean.get(plot.isAdded(uuid));
		}

		@Override
		public Class<? extends CREThrowable>[] thrown() {
			return new Class[]{CREInvalidWorldException.class, CREFormatException.class,
					CRELengthException.class, CREIllegalArgumentException.class};
		}
	}

	@api
	public static class plot_info extends PlotSquaredFunction {
		@Override
		public String docs() {
			return "mixed {location | world, plotarea, plotid} Returns an associative array of plot info."
					+ " Returns null if no plot exists at that location, or by that plotarea or plot id."
					+ " The array will contain the indexes \"owners\", \"members\", \"trusted\", and \"denied\", each"
					+ " with an array of UUIDs."
					+ " Throws a FormatException if plotid is not two numbers separated by a comma or semi-colon."
					+ " Throws InvalidWorldException if world is not a plot world.";
		}

		@Override
		public Integer[] numArgs() {
			return new Integer[]{1,3};
		}

		@Override
		public Mixed exec(Target t, Environment environment, Mixed... args) throws ConfigRuntimeException {
			Plot plot = GetPlot(t, args);
			if(plot == null){
				return CNull.NULL;
			}

			CArray info = CArray.GetAssociativeArray(t);

			CArray owners = new CArray(t);
			for(UUID uuid : plot.getOwners()){
				owners.push(new CString(uuid.toString(), t), t);
			}
			info.set("owners", owners, t);

			CArray members = new CArray(t);
			for(UUID uuid : plot.getMembers()){
				members.push(new CString(uuid.toString(), t), t);
			}
			info.set("members", members, t);

			CArray trusted = new CArray(t);
			for(UUID uuid : plot.getTrusted()){
				trusted.push(new CString(uuid.toString(), t), t);
			}
			info.set("trusted", trusted, t);

			CArray denied = new CArray(t);
			for(UUID uuid : plot.getDenied()){
				denied.push(new CString(uuid.toString(), t), t);
			}
			info.set("denied", denied, t);

			return info;
		}

		@Override
		public Class<? extends CREThrowable>[] thrown() {
			return new Class[]{CREInvalidWorldException.class, CREFormatException.class};
		}
	}

	private static Plot GetPlot(Target t, Mixed... args){
		if(args[0] instanceof CArray){
			MCLocation l = ObjectGenerator.GetGenerator().location(args[0], null, t);
			Location plotLoc = Location.at(l.getWorld().getName(), BlockVector3.at(l.getBlockX(), l.getBlockY(), l.getBlockZ()));
			PlotArea area = plotLoc.getPlotArea();
			return area == null ? null : area.getPlot(plotLoc);
		} else {
			String worldName = args[0].val();
			String areaName = args[1].val();
			if(!PlotSquared.get().getPlotAreaManager().hasPlotArea(worldName)) {
				throw new CREInvalidWorldException("Invalid plot world", t);
			}
			PlotArea area = PlotSquared.get().getPlotAreaManager().getPlotArea(worldName, areaName);
			if(area == null) {
				return null;
			}
			PlotId plotId;
			try {
				plotId = PlotId.fromString(args[2].val());
			} catch(IllegalArgumentException ex) {
				throw new CREFormatException("Invalid plot id format.", t);
			}
			return area.getPlot(plotId);
		}
	}
}
