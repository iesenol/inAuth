package demo.controller;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import demo.model.Coordinate;
import demo.model.CoordinateId;
import demo.model.repository.CoordinatesRepository;

@RestController
public class ServiceController {
	private static final Logger log = LoggerFactory.getLogger(ServiceController.class);

	@Autowired
	private CoordinatesRepository coordinatesRepository;

	@RequestMapping("/getData")
	public @ResponseBody Coordinate getData(@RequestParam BigDecimal latitude, @RequestParam BigDecimal longitude) {
		CoordinateId coordinateId = new CoordinateId(latitude, longitude); // primary-key
		Coordinate entry = coordinatesRepository.findOne(coordinateId);
		if (entry == null) {
			Coordinate result = new Coordinate();
			result.setId(coordinateId);
			result.setStatus("ENTRY_NOT_FOUND");
			log.info("Entry NOT found: " + result);
			return result;
		}
		else {
			log.info("Entry found: " + entry);
			entry.setStatus("ENTRY_FOUND");
			return entry;
		}
	}

	@RequestMapping("/addData")
	public @ResponseBody Coordinate addData (@RequestParam BigDecimal latitude, @RequestParam BigDecimal longitude) {
		Coordinate entry = getData(latitude, longitude); // search if entry already exists
		if (entry.getStatus().equalsIgnoreCase("ENTRY_NOT_FOUND")) {
			entry.setStatus("SAVED_OK");
			coordinatesRepository.save(entry);
			log.info("Saved " + entry);
		}
		else {
			entry.setStatus("ENTRY_ALREADY_EXIST");
			log.info("Not saved: coordinates already exist.");
		}
		return entry;
	}

	@RequestMapping("/getAllDataSets")
	public @ResponseBody Iterable<Coordinate> getAllDataSets() {
		return coordinatesRepository.findAll();
	}
}