/**
 * 
 */
package jsoupCrawler;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
/**
 * @author Maryam Sadat Daneshvarian
 *
 */
public class Crawler {
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		//check country name
		 Set<String> ISO_COUNTRIES = new HashSet<String>(Arrays.asList(Locale.getISOCountries()));
		 Set<String> COUNTRIES = new HashSet<String>();
	     
		 for(String code:ISO_COUNTRIES) {
	    	 Locale obj=new Locale("", code);
	    	 COUNTRIES.add(obj.getDisplayCountry(Locale.US));
	     }
		//object to write the result
		JSONObject exhibition = new JSONObject();
		
		//Exhibition web page
		Document d=Jsoup.connect("https://myfair.co/subExpo/detail/59272").timeout(6000).get();
		
		//id
		exhibition.put("id", 59272);
		
		//title
		Elements heading=d.select("h1");
		exhibition.put("title", heading.text());
		
		//description
		Elements description =d.select("div.default-introduce").select("p");
		String desc=description.first().text();
		exhibition.put("description", description.first().text());
		
		//location
		Elements locationEle=d.select("div.exhi_location");
		Elements location = locationEle.select("div.address");
		exhibition.put("location", location.text());
		
		//country
		String [] temp=location.text().split(" ");
		for(String str:temp) {
			if (str.startsWith(","))
				str=str.substring(1);
			else if(str.endsWith(","))
				str=str.substring(0, str.length()-1);
			if(COUNTRIES.contains(str))
				exhibition.put("country", str );
		}
		
		//hall name
		Elements hallName=locationEle.select("div.name").select("span");
		exhibition.put("hallName", hallName.text());
		
		//date
		Elements date=d.select("div.date-info");
		exhibition.put("startDate", date.select("span.start_date").text());
		exhibition.put("endDate", date.select("span.end_date").text());
		String dateStr=date.select("span.start_date").text();
		exhibition.put("month", dateStr.substring(5, 7));
		exhibition.put("year", dateStr.substring(0, 4));
		
        //time
		Elements time = d.select("div.fair-detailtime-info");
		exhibition.put("startTime", time.select("span.start_time").text());
		exhibition.put("endTime", time.select("span.end_time").text());
		
		//products
		Elements products=d.select("div.item-value-group").select("span");
		exhibition.put("products", products.text());
		
		//audience 
		Elements audience= d.select("div.tag-list").select("span");
		exhibition.put("audience", audience.text());
		
		//cycle
		Elements cycle=d.select("div.analytics-data.fair-cycle");
		String value = cycle.select("span.value").text();
		String unit=cycle.select("div.unit-info").text();
		exhibition.put("cycle",value+" "+unit);
		
		//last exhibition Result;
		Elements lastExhibition= d.select("div.section-content-area");
		Elements lastExhibitionResult=lastExhibition.select("div.analytics-data").select("span.value");
		String [] result=new String[3];
		int i=0;
		for(Element e: lastExhibitionResult) {
			
			result[i]=e.text();
			i++;
		}
		exhibition.put("lastExhibitionCompaniesCount", result[1]);
		exhibition.put("lastExhibitionVisitorsCount", result[2]);
		
		//Json Result
		JSONObject resultObj=new JSONObject();
		JSONArray exhibitionList=new JSONArray();
		exhibitionList.add(exhibition);
		resultObj.put("result", exhibitionList);
		
		//write to file
		FileWriter file=new FileWriter("D:/exhibitionList.json");
		try {
			file.write(resultObj.toJSONString());
		}catch(IOException e) {
			e.printStackTrace();
		} finally {
			try {
				file.flush();
				file.close();
			}catch(IOException e){
				e.printStackTrace();
			}
		}	
	}
}
