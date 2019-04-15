# CHPlotSquared

Version 2.x is compatible with MethodScript 3.3.4 and PlotSquared 4

Version 1.0.2 is compatible with MethodScript 3.3.4 and PlotSquared 3.5.x

Version 1.0.1 is compatible with MethodScript 3.3.2 and PlotSquared 3.5.x

Version 1.0.0 is compatible with MethodScript 3.3.2 and PlotSquared 3.4.x

## Functions

#### array plot_list(world, [uuid])
Returns an array of plot ids. Throws InvalidWorldException if world is not a plot world.

#### string plot_at_loc(location)
Returns the plot id at the specified location, null if no plot exists there.

#### boolean plot_has_player(location, uuid | world, plotid, uuid)
Returns whether the player is added to a plot. Returns null if no plot exists at that location or by that plot id. 
Throws a FormatException if plotid is not two numbers separated by a comma or semi-colon.

#### array plot_info(location | world, plotid)
Returns an associative array of plot info, or null if not a plot. The array will contain the indexes "owners",
"members", "trusted", and "denied", each with an array of UUIDs. 
Throws a FormatException if plotid is not two numbers separated by a comma or semi-colon.