package demo;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.WorkbookUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Utility {

	private final Logger log = LoggerFactory.getLogger(Utility.class);
	
	@Value("${Excel.file.name}")		// value is set in the "application.properties" file
	private String EXCEL_FILE_NAME;

	@Value("${google.maps.api.key}")    // value is set in the "application.properties" file
	private String geomapAPIKey;

	public List<HashMap<String, String>> getAllDataSets() throws MalformedURLException, IOException, JSONException {
		String url = "http://127.0.0.1:8080/getAllDataSets";

		List<HashMap<String, String>> records = new ArrayList<HashMap<String, String>>();
		String latitude = "";
		String longitude = "";

		String jsonString = null;
		jsonString = IOUtils.toString(new URL(url), "US-ASCII");

		JSONArray root = new JSONArray(jsonString);
		if (root != null && root.length() > 0) {
			DecimalFormat df = new DecimalFormat("#,##0.0000000");
			for (int i = 0; i < root.length(); i++) {
				latitude  = df.format(root.getJSONObject(i).getJSONObject("id").getDouble("latitude"));
				longitude = df.format(root.getJSONObject(i).getJSONObject("id").getDouble("longitude"));

				HashMap<String, String> record = new HashMap<String, String>();
				record.put("lat", latitude);
				record.put("lng", longitude);

				records.add(record);
			}
		}

		return records;
	}

	public Map<String, Double> getLatLng(String city, String country) throws MalformedURLException, IOException, JSONException {
		String url = "https://maps.googleapis.com/maps/api/geocode/json?address=" + StringUtils.trimToEmpty(city) + ",+" + StringUtils.trimToEmpty(country) + "&location_type=APPROXIMATE&key=" + geomapAPIKey;

		Map<String, Double> result = new HashMap<String, Double>();
		Double latitude = null, longitude = null;

		String jsonString = null;
		jsonString = IOUtils.toString(new URL(url), "US-ASCII");

		JSONObject root = new JSONObject(jsonString);
		if (root.getString("status").equalsIgnoreCase("OK")) {
			latitude = root.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lat");
			longitude = root.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lng");
		} else
			latitude = longitude = null;

		result.put("lat", latitude);
		result.put("lng", longitude);

		return result;
	}

	public Map<String, String> getCountryName(String latitude, String longitude) throws MalformedURLException, IOException, JSONException {
		String url = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + latitude + "," + longitude + "&result_type=country&key=" + geomapAPIKey;
		Map<String, String> result = new HashMap<String, String>();
		String countryShortName = "";
		String countryLongName = "";

		String jsonString = null;
		jsonString = IOUtils.toString(new URL(url), "US-ASCII");
		JSONObject root = new JSONObject(jsonString);
		if (root.getString("status").equalsIgnoreCase("OK")) {
			countryShortName = root.getJSONArray("results").getJSONObject(0).getJSONArray("address_components").getJSONObject(0).getString("short_name");
			countryLongName = root.getJSONArray("results").getJSONObject(0).getJSONArray("address_components").getJSONObject(0).getString("long_name");
		}
		else
			countryShortName = countryLongName = "";

		result.put("countryShortName", countryShortName);
		result.put("countryLongName", countryLongName);

		return result;
	}
	
	public void createExcelOutput(List<HashMap<String, String>> entries) throws IOException {

		// Create Excel worksheet.
        log.info("Creating Excel worksheet " + EXCEL_FILE_NAME);
        Workbook wb = new HSSFWorkbook();
	    Sheet sheet = wb.createSheet(WorkbookUtil.createSafeSheetName("RESULTS"));

	    // Create the header portion -----------------
	    
	    // Create cell styles
	    CellStyle headerCellStyle = wb.createCellStyle();
	    headerCellStyle.setAlignment(HorizontalAlignment.CENTER);
	    headerCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
	    headerCellStyle.setWrapText(true);

	    CellStyle coordinateCellStyle = wb.createCellStyle();
	    coordinateCellStyle.setAlignment(HorizontalAlignment.RIGHT);
	    headerCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

	    CellStyle distanceCellStyle = wb.createCellStyle();
	    distanceCellStyle.setAlignment(HorizontalAlignment.RIGHT);

	    // row 0
	    int rowNum = 0;
	    Row row = sheet.createRow(rowNum);
	    row.createCell(0).setCellValue("Coordinates");
	    row.createCell(3).setCellValue("Is the coordinate within 500mi radius of these cities? How far away is it from these cities (miles)?");
	    sheet.getRow(rowNum).getCell(0).setCellStyle(headerCellStyle);
	    sheet.getRow(rowNum).getCell(3).setCellStyle(headerCellStyle);

	    // row 1
	    rowNum=1;
	    row = sheet.createRow(rowNum);

	    row.createCell(3).setCellValue("Tokyo, Japan");
	    sheet.getRow(rowNum).getCell(3).setCellStyle(headerCellStyle);

	    row.createCell(5).setCellValue("Sydney, Australia");
	    sheet.getRow(rowNum).getCell(5).setCellStyle(headerCellStyle);
	    
	    row.createCell(7).setCellValue("Riyadh, Saudi Arabia");
	    sheet.getRow(rowNum).getCell(7).setCellStyle(headerCellStyle);
	    
	    row.createCell(9).setCellValue("Zurich, Switzerland");
	    sheet.getRow(rowNum).getCell(7).setCellStyle(headerCellStyle);
	    
	    row.createCell(11).setCellValue("Reykjavik, Iceland");
	    sheet.getRow(rowNum).getCell(11).setCellStyle(headerCellStyle);
	    
	    row.createCell(13).setCellValue("Mexico City, Mexico");
	    sheet.getRow(rowNum).getCell(13).setCellStyle(headerCellStyle);
	    
	    row.createCell(15).setCellValue("Lima, Peru");
	    sheet.getRow(rowNum).getCell(15).setCellStyle(headerCellStyle);

	    // Merge cells
	    sheet.addMergedRegion(new CellRangeAddress(1, 1, 15, 16));
	    sheet.addMergedRegion(new CellRangeAddress(1, 1, 13, 14));
	    sheet.addMergedRegion(new CellRangeAddress(1, 1, 11, 12));
	    sheet.addMergedRegion(new CellRangeAddress(1, 1, 9, 10));
	    sheet.addMergedRegion(new CellRangeAddress(1, 1, 7, 8));
	    sheet.addMergedRegion(new CellRangeAddress(1, 1, 5, 6));
	    sheet.addMergedRegion(new CellRangeAddress(1, 1, 3, 4));

	    sheet.addMergedRegion(new CellRangeAddress(0, 0, 3, 16));
	    sheet.addMergedRegion(new CellRangeAddress(0, 1, 0, 2));

	    // row 2
	    rowNum=2;
	    row = sheet.createRow(rowNum);
	    int col = 0;

	    row.createCell(col).setCellValue("Latitude");
	    sheet.getRow(rowNum).getCell(col++).setCellStyle(headerCellStyle);

	    row.createCell(col).setCellValue("Longitude");
	    sheet.getRow(rowNum).getCell(col++).setCellStyle(headerCellStyle);

	    row.createCell(col).setCellValue("In USA?");
	    sheet.getRow(rowNum).getCell(col++).setCellStyle(headerCellStyle);

	    row.createCell(col).setCellValue("Within 500mi ?");
	    sheet.getRow(rowNum).getCell(col++).setCellStyle(headerCellStyle);

	    row.createCell(col).setCellValue("Distance to here");
	    sheet.getRow(rowNum).getCell(col++).setCellStyle(headerCellStyle);

	    row.createCell(col).setCellValue("Within 500mi ?");
	    sheet.getRow(rowNum).getCell(col++).setCellStyle(headerCellStyle);

	    row.createCell(col).setCellValue("Distance to here");
	    sheet.getRow(rowNum).getCell(col++).setCellStyle(headerCellStyle);

	    row.createCell(col).setCellValue("Within 500mi ?");
	    sheet.getRow(rowNum).getCell(col++).setCellStyle(headerCellStyle);

	    row.createCell(col).setCellValue("Distance to here");
	    sheet.getRow(rowNum).getCell(col++).setCellStyle(headerCellStyle);

	    row.createCell(col).setCellValue("Within 500mi ?");
	    sheet.getRow(rowNum).getCell(col++).setCellStyle(headerCellStyle);

	    row.createCell(col).setCellValue("Distance to here");
	    sheet.getRow(rowNum).getCell(col++).setCellStyle(headerCellStyle);

	    row.createCell(col).setCellValue("Within 500mi ?");
	    sheet.getRow(rowNum).getCell(col++).setCellStyle(headerCellStyle);

	    row.createCell(col).setCellValue("Distance to here");
	    sheet.getRow(rowNum).getCell(col++).setCellStyle(headerCellStyle);

	    row.createCell(col).setCellValue("Within 500mi ?");
	    sheet.getRow(rowNum).getCell(col++).setCellStyle(headerCellStyle);

	    row.createCell(col).setCellValue("Distance to here");
	    sheet.getRow(rowNum).getCell(col++).setCellStyle(headerCellStyle);

	    row.createCell(col).setCellValue("Within 500mi ?");
	    sheet.getRow(rowNum).getCell(col++).setCellStyle(headerCellStyle);

	    row.createCell(col).setCellValue("Distance to here");
	    sheet.getRow(rowNum).getCell(col++).setCellStyle(headerCellStyle);

	    // Freeze the first 3 lines.
	    sheet.createFreezePane(0, 3);

	    // Create the data portion -----------------

		for (Map<String, String> entry : entries) {
		    rowNum++;
		    row = sheet.createRow(rowNum);
		    
			// Write entry into the Excel file.
		    col = 0;
		    row.createCell(col).setCellValue(entry.get("lat"));
		    sheet.getRow(rowNum).getCell(col++).setCellStyle(coordinateCellStyle);

		    row.createCell(col).setCellValue(entry.get("lng"));
		    sheet.getRow(rowNum).getCell(col++).setCellStyle(coordinateCellStyle);

		    row.createCell(col).setCellValue(entry.get("inTheStates"));
		    sheet.getRow(rowNum).getCell(col++).setCellStyle(headerCellStyle);
		    
		    for (int cityNumber=1; cityNumber <= 7; cityNumber++) {
			    row.createCell(col).setCellValue(entry.get("isWithin" + cityNumber));
			    sheet.getRow(rowNum).getCell(col++).setCellStyle(headerCellStyle);
			    
			    row.createCell(col).setCellValue(entry.get("distanceTo" + cityNumber));
			    sheet.getRow(rowNum).getCell(col++).setCellStyle(distanceCellStyle);			    
		    }
		}

		// Write the output to the Excel file
	    FileOutputStream fileOut = new FileOutputStream(EXCEL_FILE_NAME);
	    wb.write(fileOut);
	    fileOut.close();
	    wb.close();
	}

}
