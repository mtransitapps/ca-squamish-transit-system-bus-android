package org.mtransit.parser.ca_squamish_transit_system_bus;

import static org.mtransit.commons.StringUtils.EMPTY;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mtransit.commons.CleanUtils;
import org.mtransit.parser.DefaultAgencyTools;
import org.mtransit.parser.MTLog;
import org.mtransit.parser.gtfs.data.GRoute;
import org.mtransit.parser.gtfs.data.GStop;
import org.mtransit.parser.mt.data.MAgency;

import java.util.regex.Pattern;

// https://www.bctransit.com/open-data
public class SquamishTransitSystemBusAgencyTools extends DefaultAgencyTools {

	public static void main(@NotNull String[] args) {
		new SquamishTransitSystemBusAgencyTools().start(args);
	}

	@Override
	public boolean defaultExcludeEnabled() {
		return true;
	}

	@NotNull
	@Override
	public String getAgencyName() {
		return "Squamish TS";
	}

	@NotNull
	@Override
	public Integer getAgencyRouteType() {
		return MAgency.ROUTE_TYPE_BUS;
	}

	@Override
	public boolean defaultRouteIdEnabled() {
		return true;
	}

	@Override
	public boolean useRouteShortNameForRouteId() {
		return false; // route ID used by GTFS-RT
	}

	@Override
	public @Nullable String getRouteIdCleanupRegex() {
		return "\\-[A-Z]+$";
	}

	@Override
	public boolean defaultRouteLongNameEnabled() {
		return true;
	}

	@NotNull
	@Override
	public String cleanRouteLongName(@NotNull String routeLongName) {
		routeLongName = CleanUtils.cleanSlashes(routeLongName);
		routeLongName = CleanUtils.cleanNumbers(routeLongName);
		routeLongName = CleanUtils.cleanStreetTypes(routeLongName);
		return CleanUtils.cleanLabel(routeLongName);
	}

	@Override
	public boolean defaultAgencyColorEnabled() {
		return true;
	}

	private static final String AGENCY_COLOR_GREEN = "34B233";// GREEN (from PDF Corporate Graphic Standards)
	// private static final String AGENCY_COLOR_BLUE = "002C77"; // BLUE (from PDF Corporate Graphic Standards)

	private static final String AGENCY_COLOR = AGENCY_COLOR_GREEN;

	@NotNull
	@Override
	public String getAgencyColor() {
		return AGENCY_COLOR;
	}

	@Nullable
	@Override
	public String provideMissingRouteColor(@NotNull GRoute gRoute) {
		int rsn = Integer.parseInt(gRoute.getRouteShortName());
		switch (rsn) {
		// @formatter:off
		case 1: return "004B8D";
		case 2: return "8CC63F";
		case 3: return "F78B1F";
		case 4: return "FB298C";
		case 5: return "00ADEE";
		case 9: return "AB5C3B";
		// @formatter:on
		default:
			throw new MTLog.Fatal("Unexpected route color for %s!", gRoute);
		}
	}

	@Override
	public boolean directionFinderEnabled() {
		return true;
	}

	private static final Pattern _DASH_ = Pattern.compile("(\\s*-\\s*)");
	private static final String _DASH_REPLACEMENT = " - ";

	private static final Pattern ENDS_WITH_ONLY = CleanUtils.cleanWords("only");

	private static final Pattern ENDS_WITH_DASH_ = Pattern.compile("(\\s*-\\s*$)");

	@NotNull
	@Override
	public String cleanTripHeadsign(@NotNull String tripHeadsign) {
		tripHeadsign = _DASH_.matcher(tripHeadsign).replaceAll(_DASH_REPLACEMENT);
		tripHeadsign = ENDS_WITH_ONLY.matcher(tripHeadsign).replaceAll(EMPTY);
		tripHeadsign = CleanUtils.keepToAndRemoveVia(tripHeadsign);
		tripHeadsign = ENDS_WITH_DASH_.matcher(tripHeadsign).replaceAll(EMPTY);
		tripHeadsign = CleanUtils.CLEAN_AND.matcher(tripHeadsign).replaceAll(CleanUtils.CLEAN_AND_REPLACEMENT);
		tripHeadsign = CleanUtils.cleanBounds(tripHeadsign);
		tripHeadsign = CleanUtils.cleanStreetTypes(tripHeadsign);
		tripHeadsign = CleanUtils.cleanNumbers(tripHeadsign);
		return CleanUtils.cleanLabel(tripHeadsign);
	}

	private static final Pattern STARTS_WITH_DCOM = Pattern.compile("(^(\\(-DCOM-\\)))", Pattern.CASE_INSENSITIVE);
	private static final Pattern STARTS_WITH_IMPL = Pattern.compile("(^(\\(-IMPL-\\)))", Pattern.CASE_INSENSITIVE);

	@NotNull
	@Override
	public String cleanStopName(@NotNull String gStopName) {
		gStopName = STARTS_WITH_DCOM.matcher(gStopName).replaceAll(EMPTY);
		gStopName = STARTS_WITH_IMPL.matcher(gStopName).replaceAll(EMPTY);
		gStopName = CleanUtils.cleanBounds(gStopName);
		gStopName = CleanUtils.CLEAN_AT.matcher(gStopName).replaceAll(CleanUtils.CLEAN_AT_REPLACEMENT);
		gStopName = CleanUtils.cleanStreetTypes(gStopName);
		gStopName = CleanUtils.cleanNumbers(gStopName);
		return CleanUtils.cleanLabel(gStopName);
	}

	@Override
	public int getStopId(@NotNull GStop gStop) { // used by GTFS-RT
		return super.getStopId(gStop);
	}
}
