package demo.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import demo.City;
import demo.Haversine;
import demo.Utility;

@Controller
public class ResultsPageController {
	private static final Logger log = LoggerFactory.getLogger(ResultsPageController.class);

	@Autowired
	private Utility utility;

	@RequestMapping("/results")
	public String results(Model model) throws MalformedURLException, IOException, JSONException {

		// Create an array of cities.
		log.info("Creating an array of cities.");
		List<City> cities = new ArrayList<City>();
		cities.add(new City("Tokyo", "Japan"));
		cities.add(new City("Sydney", "Australia"));
		cities.add(new City("Riyadh", "Saudi Arabia"));
		cities.add(new City("Zurich", "Switzerland"));
		cities.add(new City("Reykjavik", "Iceland"));
		cities.add(new City("Mexico City", "Mexico"));
		cities.add(new City("Lima", "Peru"));
		log.info("Done.");

		// Get their geographical information via the Google Maps Geocoding API
		log.info("Getting their geographical information via Google Maps Geocoding API.");
		for (City city : cities) {
			Map<String, Double> geoLocation = null;
			try {
				geoLocation = utility.getLatLng(URLEncoder.encode(city.getName(), "UTF-8"),
						                        URLEncoder.encode(city.getCountry(), "UTF-8"));
				city.setLatitude(geoLocation.get("lat"));
				city.setLongitude(geoLocation.get("lng"));
			} catch (UnsupportedEncodingException e) {
				log.error("IOException: {}", e);
			}
		}
		log.info("Done.");

		// Read all entries from the database into an arraylist.
		log.info("Reading all coordinates from the database into an arraylist.");
		List<HashMap<String, String>> entries = utility.getAllDataSets();
		log.info("Done.");

		// Process entries.
		log.info("Processing {} entries. Please, wait...", entries.size());
		int entryNum = 0;
		for (Map<String, String> entry : entries) {
			entryNum++;

			// Get the country name information for the entry via the Google Maps Geocoding API.
			Map<String, String> countryName = utility.getCountryName(entry.get("lat"), entry.get("lng"));
			entry.put("countryShortName", countryName.isEmpty() ? "" : countryName.get("countryShortName") );
			entry.put("countryLongName", countryName.isEmpty() ? "" : countryName.get("countryLongName") );

			// 1.)  Given the entry's coordinates, determine if those coordinates are within the United States.
			entry.put("inTheStates", entry.get("countryShortName").equalsIgnoreCase("US") ? "Y" : "N");

		    // 2.)  If they're not within the United States, determine if the coordinates are within 500 miles of the given cities.
			// 3.)  For each of the above, tell us how far away the entry's coordinates are from each city.
			int cityNumber = 0;
			for (City city : cities) {
				cityNumber++;
				Double distance = Haversine.distance(Double.parseDouble(entry.get("lat")), Double.parseDouble(entry.get("lng")), city.getLatitude(), city.getLongitude());
				DecimalFormat df = new DecimalFormat("#,##0.00");
				entry.put("distanceTo" + cityNumber, df.format(distance));
				entry.put("isWithin" + cityNumber, distance <= 500 ? "Y" : "N");
			}
			
			if(entryNum % 250 == 0)
				log.info("... {} entries processed so far.", entryNum);
		}

		log.info("Done... {} entries processed", entryNum);

		// Create the output in Excel worksheet format.
		utility.createExcelOutput(entries);
		log.info("Done.");

		log.info("Displaying the results.");
		model.addAttribute("entries", (List<HashMap<String, String>>) entries);
		log.info("Done.");

		return "resultsPage";
	}
}
