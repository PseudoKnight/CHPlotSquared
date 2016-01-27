package me.pseudoknight.CHPlotSquared;

import com.intellectualcrafters.plot.config.C;
import com.intellectualcrafters.plot.object.Location;
import com.intellectualcrafters.plot.object.Plot;
import com.intellectualcrafters.plot.object.PlotId;
import com.intellectualcrafters.plot.util.MainUtil;
import com.laytonsmith.PureUtilities.Version;
import com.laytonsmith.abstraction.MCLocation;
import com.laytonsmith.annotations.api;
import com.laytonsmith.core.CHVersion;
import com.laytonsmith.core.ObjectGenerator;
import com.laytonsmith.core.Static;
import com.laytonsmith.core.constructs.CArray;
import com.laytonsmith.core.constructs.CBoolean;
import com.laytonsmith.core.constructs.CNull;
import com.laytonsmith.core.constructs.Construct;
import com.laytonsmith.core.constructs.CString;
import com.laytonsmith.core.constructs.Target;
import com.laytonsmith.core.environments.Environment;
import com.laytonsmith.core.exceptions.CRE.CREFormatException;
import com.laytonsmith.core.exceptions.CRE.CREIllegalArgumentException;
import com.laytonsmith.core.exceptions.CRE.CREInvalidWorldException;
import com.laytonsmith.core.exceptions.CRE.CRELengthException;
import com.laytonsmith.core.exceptions.CRE.CREThrowable;
import com.laytonsmith.core.exceptions.ConfigRuntimeException;
import com.laytonsmith.core.functions.AbstractFunction;
import com.intellectualcrafters.plot.PS;

import java.util.Collection;
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
			return CHVersion.V3_3_1;
		}
	}

	@api
	public static class plot_list extends PlotSquaredFunction {
		@Override
		public String docs() {
			return "array {world, [uuid]} Returns an array of plot ids. Throws InvalidWorldException"
					+ " if world is not a plot world.";
		}

		@Override
		public Integer[] numArgs() {
			return new Integer[]{1,2};
		}

		@Override
		public Construct exec(Target t, Environment environment, Construct... args) throws ConfigRuntimeException {
			Collection<Plot> plots;
			String world = args[0].val();
			if(!PS.get().isPlotWorld(world)) {
				throw new CREInvalidWorldException(C.NOT_VALID_PLOT_WORLD.s(), t);
			}
			if(args.length == 2){
				UUID uuid = Static.GetUUID(args[1], t);
				plots = PS.get().getPlots(world, uuid);
			} else {
				plots = PS.get().getPlotsInWorld(world);
			}
			CArray cplots = new CArray(t);
			for(Plot plot : plots) {
				cplots.push(new CString(plot.getId().toString(), t), t);
			}
			return cplots;
		}

		@Override
		public Class<? extends CREThrowable>[] thrown() {
			return new Class[]{CREInvalidWorldException.class,
					CRELengthException.class,CREIllegalArgumentException.class};
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
		public Construct exec(Target t, Environment environment, Construct... args) throws ConfigRuntimeException {
			MCLocation l = ObjectGenerator.GetGenerator().location(args[0], null, t);
			PlotId plotid = MainUtil.getPlotId(new Location(
					l.getWorld().getName(), l.getBlockX(), l.getBlockY(), l.getBlockZ(), l.getYaw(), l.getPitch()));
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
			return "mixed {location, uuid | world, plotid, uuid} Returns whether the player is added to a plot."
					+ " Returns null if no plot exists at that location or by that plot id.";
		}

		@Override
		public Integer[] numArgs() {
			return new Integer[]{2,3};
		}

		@Override
		public Construct exec(Target t, Environment environment, Construct... args) throws ConfigRuntimeException {
			Plot plot = GetPlot(t, args);
			if(plot == null){
				return CNull.NULL;
			}
			UUID uuid = Static.GetUUID(args[args.length - 1], t);
			return CBoolean.get(plot.isAdded(uuid));
		}

		@Override
		public Class<? extends CREThrowable>[] thrown() {
			return new Class[]{CREInvalidWorldException.class,CREFormatException.class,
					CRELengthException.class,CREIllegalArgumentException.class};
		}
	}

	@api
	public static class plot_info extends PlotSquaredFunction {
		@Override
		public String docs() {
			return "mixed {location | world, plotid} Returns an associative array of plot info, or null if not a plot."
					+ " The array will contain the indexes \"owners\", \"members\", \"trusted\", and \"denied\", each"
					+ " with an array of UUIDs.";
		}

		@Override
		public Integer[] numArgs() {
			return new Integer[]{1,2};
		}

		@Override
		public Construct exec(Target t, Environment environment, Construct... args) throws ConfigRuntimeException {
			Plot plot = GetPlot(t, args);
			if(plot == null){
				return null;
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
			return new Class[]{CREInvalidWorldException.class,CREFormatException.class};
		}
	}

	private static Plot GetPlot(Target t, Construct... args){
		if(args[0] instanceof CArray){
			MCLocation l = ObjectGenerator.GetGenerator().location(args[0], null, t);
			Location plotLoc = new Location(
					l.getWorld().getName(), l.getBlockX(), l.getBlockY(), l.getBlockZ(), l.getYaw(), l.getPitch());
			return(MainUtil.getPlot(plotLoc));
		} else {
			String worldName = args[0].val();
			PlotId plotId = PlotId.fromString(args[1].val());
			if(plotId == null){
				throw new CREFormatException("Invalid plot id format.", t);
			}
			return(MainUtil.getPlot(worldName, plotId));
		}
	}
}
