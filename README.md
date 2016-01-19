# CHPlotSquared

## Functions

#### array plot_list(world, [uuid])
Returns an array of plot ids. Throws InvalidWorldException if world is not a plot world.

#### string plot_at_loc(location)
Returns the plot id at the specified location, null if no plot exists there.

#### boolean plot_has_player(location, uuid | world, plotid, uuid)
Returns whether the player is added to a plot. Returns null if no plot exists at that location or by that plot id.

#### array plot_info(location | world, plotid)
Returns an associative array of plot info, or null if not a plot. The array will contain the indexes "owners",
"members", "trusted", and "denied", each with an array of UUIDs.