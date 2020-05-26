package tests.rest;

import java.io.FileInputStream;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class TC_004_GetWeather_Cities {

	@DataProvider(name="Cities", parallel=true)
	public String[] getcities(){
		String[] data = new String[3];
		data[0]="Chennai";
		data[1]="London";
		data[2]="washington";
		return data;
	}


	@DataProvider(name="citiesfromExcel", parallel=true)
	public String [] [] getCitiesFromExcel() {

		Object[][] dataExcel = null ;

		try {
			String text = "./data/Cities.xlsx";
			System.out.println(text);
			FileInputStream fis = new FileInputStream("./data/Cities.xlsx");
			XSSFWorkbook workbook = new XSSFWorkbook(fis);
			XSSFSheet sheet = workbook.getSheetAt(0);	

			// get the number of rows
			int rowCount = sheet.getPhysicalNumberOfRows();

			// get the number of columns
			int columnCount = sheet.getRow(0).getLastCellNum();
			dataExcel = new String[rowCount][columnCount];

			// loop through the rows
			for(int i=0; i <rowCount; i++){
				try {
					XSSFRow row = sheet.getRow(i);
					for(int j=0; j <columnCount; j++){ // loop through the columns
						try {
							String cellValue = "";
							try{
								cellValue = row.getCell(j).getStringCellValue();
							}catch(NullPointerException e){

							}
                            System.out.println(" value i row : "+ i + "value of J col : " + j);
							dataExcel[i][j]  = cellValue; // add to the data array
						} catch (Exception e) {
							e.printStackTrace();
						}				
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			fis.close();
			workbook.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return (String[][]) dataExcel;

	}


	@Test(dataProvider="citiesfromExcel")
	public void getWeathercity(String cityName ) {

		RestAssured.baseURI= "http://api.openweathermap.org/data/2.5/weather";

		Response response = RestAssured
				.given()
				.log().all()
				.queryParam("q", cityName)
				.queryParam("appid", "c397236a177654c953b206cf4304b40f")
				.accept(ContentType.JSON)
				.get();

		response.prettyPrint();


		JsonPath jsonPath = response.jsonPath();
		System.out.println("Maximum Temperature in : " +  cityName + " is: " + jsonPath.get("main.temp_max"));
		System.out.println("Sunset time in : "+  cityName + " is: "  + jsonPath.get("sys.sunset"));
		System.out.println("Wind Speed in : " +  cityName + " is: " + jsonPath.get("wind.speed"));


	}
}
