package contacts;

import static spark.Spark.*;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.elasticsearch.*;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.TransportAction;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.client.*;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.*;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.elasticsearch.client.transport.*;
import org.elasticsearch.node.Node;
import org.elasticsearch.search.SearchHit;

import com.fasterxml.jackson.databind.ObjectMapper;

/*
 * 	Author: Daniel Sha
 * 
 * 	****Notes in order of importance****
 * 
 * 	1) All methods besides get Contact/?pageSize={}&pageOffset={}&query={} work.
 * 		- I wrote the theoretical (untested) code below (lines 72 - 148) on potential correct implementation, but commented out so this can compile.
 * 		- I spent a long time trying but could not figure out why queryParamOrDefault always returns the default value.
 * 		- I am also unsure how to enter a querystring in curl as double quotations ruin the command in powershell. Single quotations dont appear to work either.
 * 	2) Testing was done using "curl". This Java App works under the assumption that curl testing is accurate and fulfills the challenge's requirements.
 * 		- 2 Examples of curl command (assuming cwd is curl.exe folder like "D:\code\curl\curl-7.58.0-win64-mingw\bin"
 * 		- $ ./curl.exe http://localhost:8081/hello
 * 		- $ ./curl.exe -X POST -d "name=frankSobotka&address=PalmerSt" http://localhost:8081/contact
 *		- Side note* newest windows powershell created an alias for "curl" to invoke "Invoke-WebRequest" instead. Call curl.exe to resolve.
 * 	3) post uses queryParams {name & address & email & number} to resolve.
 * 		- Must provide name queryParam. All others are optional.
 * 		- Not sure if this is correct, but google doc didn't specify a method to require these attributes.
 * 		- Will still create contact if any of three attributes besides name has length > reasonable amount, but will be set to N/A
 * 	4) get/put/delete "/contact/:name" might not be able to handle names with spaces. 
 * 		- $ ./curl.exe http://localhost:8081/contact/frank Sobotka
 * 		- Command above will fail in powershell with message "can not resolve host"
 * 	5) ports can be changed by changing the variable "portToUse"(main.java) & "portElasticSearch"(contactManager.java) near the top of this class file.
 * 	6) Elasticsearch now has their own java rest api, and is deprecating many methods/classes rendering much documentation obsolete.
		- Thus this was built first using an arrayList/JSON instead of Indexes and attempted to integrate Elasticsearch afterwards.
 * 	 
 * 
 */

public class main {
	
	static int portToUse = 8081;  //spark framework's port. curl commands target here. For elasticsearch, go to contactManager.java.
	private static contactManager mainManager = new contactManager();
	private static ObjectMapper mainMapper = new ObjectMapper();

	
	public static void main(String[] args) {
		BasicConfigurator.configure();
		port(portToUse);
		
                
        //required handlers for Address Book
        //GET
		/*Code currently only returns first 20 entities. 
		 * Theoretical (untested) code to correctly implement requirements is commented out*/
        get("/contact", (req, res)-> {
        	String def = "N/A";
        	String defSize = "20";
        	String defOffset = "0";
        	//for some reason these three fields are always going to default.
        	//even though queryParamOrDefault works for POST below.
        	String pageSz = req.queryParamOrDefault("pageSize",defSize);
        	String pageOff = req.queryParamOrDefault("pageOffset", defOffset);
        	String query = req.queryParamOrDefault("query", def);
        	
        	int pSize = 0; int pOff = 0;
        	pSize = Integer.valueOf(pageSz);
        	pOff = Integer.valueOf(pageOff);
        	if(pSize <= 0) {pSize = 100;}
        	if(pOff < 0) {pOff = 0;}
        	
        	if(query.equals("N/A"))
        	{
        		int startingIndex = pSize * pOff;
        		int finishIndex = startingIndex + pSize;
        		ArrayList<String> resultList = contactManager.testElasticGetAll();
        		ArrayList<String> cutList = new ArrayList<String>();
        		if(startingIndex > resultList.size()-1) {return "";}
        		else {
        			for (int i = startingIndex; i < finishIndex; i++)
        			{
        				cutList.add(resultList.get(i));
        				if (i + 1 >= resultList.size()) {break;}
        			}
        		}
        		//Should be return mainMapper.writeValueAsString(cutList);
        		//but the params always go to the default value for some reason I cannot figure out.
        		return mainMapper.writeValueAsString(resultList);
        	}
        	else //it never reaches else right now, even if -d "query={querystringhere} is entered
        	{
        		/*The correct implementation might use something such as
        		call contactManager.querySearchMethod(query);
        		
        		
        		Method in contactManager class:
        		public static ArrayList<String> querySearchMethod(String query)
        		{
        			QueryBuilder myQuery = QueryBuilders.queryStringQuery(query)
        			SearchResponse response = client.prepareSearch().setQuery(myQuery).execute().actionGet();
    				List<SearchHit> searchHits = Arrays.asList(response.getHits().getHits());
    				ArrayList<String> results = new ArrayList<String>();
    				searchHits.forEach(
    	  			hit -> {
    		  			String toAdd = hit.getSourceAsString();
    		  			results.add(toAdd);
    	  			});
    				return results;
        		}
        		
        		
        		then with results, use same method as posted above to return only page/offset results
        		
        		int startingIndex = pSize * pOff;
        		int finishIndex = startingIndex + pSize;
        		ArrayList<String> resultList = contactManager.querySearchMethod(query)
        		if(startingIndex > resultList.size()-1) {return "";}
        		else {
        			for (int i = startingIndex; i < finishIndex; i++)
        			{
        				cutList.add(resultList.get(i));
        				if (i + 1 >= resultList.size()) {break;}
        			}
        		}
        		
        		return mainMapper.writeValueAsString(cutList);
        		 
        		 * */
        		return "placeholder";
        	}
        	
        });
        
        //POST
        post("/contact", (req, res)->{
        	String name = req.queryParams("name");
        	String def = "N/A";
        	String address = req.queryParamOrDefault("address", def);
        	String email = req.queryParamOrDefault("email", def);
        	String number = req.queryParamOrDefault("number", def);
        	contact newContact = new contact(name, address, email, number);
        	if(contactManager.elasticPost(newContact)) //has to be unique
        		{
        			res.status(201);
        			return "Success!";
        		}
        	else
        	{
        		res.status(403);
    			return "Name is unique and already in contacts";
        	}
        	
        });
        
        //GET SPECIFIC CONTACT
        get("/contact/:name", (req,res)-> {
        	String name = req.params(":name");
        	contact getContact = new contact(name);
        	ArrayList<String> jsonStringList = contactManager.elasticGet(getContact);
        	if(jsonStringList.size() == 0) 
        	{res.status(404); return "That name has not been found in our Address Book";}
        	else 
        	{
        		return mainMapper.writeValueAsString(jsonStringList);
        	}
        		});
        
        //PUT SPECIFIC CONTACT
        put("/contact/:name", (req,res)-> {
        		String name = req.params(":name");
            	contact oldContact = new contact(name);
            	ArrayList<String> jsonStringList = contactManager.elasticGet(oldContact);
            	if(jsonStringList.size() == 0) 
            	{res.status(404); return "That name has not been found in our Address Book";}
            	
            		contactManager.elasticDelete(oldContact);
            		
            		String newName = req.params(":name");
            		String def = "N/A";
                	String address = req.queryParamOrDefault("address", def);
                	String email = req.queryParamOrDefault("email", def);
                	String number = req.queryParamOrDefault("number", def);
            		contact desiredContact = new contact(newName, address, email, number);
            		
            		if(contactManager.elasticPutAdd(desiredContact))
            		{res.status(200);
            		return("Success!");}
            		
            		else {res.status(400);return "Unexpected Failure";}
            	
        });
        
      //DELETE SPECIFIC CONTACT
        delete("/contact/:name", (req,res)-> {
        	String name = req.params(":name");
        	contact contactToDelete = new contact(name);
        	if(contactManager.elasticDelete(contactToDelete))
        	{
        		res.status(200);
            	return ("Success! Deleted" + name);
        	}
        	else
        	{
        		res.status(404);
        		return "That name has not been found in our Address Book";
        	}
        });
        
    }
}
