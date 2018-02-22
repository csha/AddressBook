package contacts;

import static spark.Spark.*;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;

import org.apache.log4j.BasicConfigurator;
import org.elasticsearch.*;
import org.elasticsearch.action.support.TransportAction;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.client.*;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.*;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.elasticsearch.client.transport.*;
import org.elasticsearch.node.Node;

import com.fasterxml.jackson.databind.ObjectMapper;

/*
 * 	Author: Daniel Sha
 * 
 * 	****Notes in order of importance****
 * 
 * 	1) Elasticsearch now has their own java rest api, using it seems to defeat the purpose of this exercise so I did not.
 * 		- However a large portion of their documentation for Java is outdated. Some classes + methods have been deprecated.
 * 		- Thus this was built first using an arrayList/JSON instead of Indexes and attempted to integrate Elasticsearch afterwards.
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
 * 	
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
		
		//Four testing handlers to check Curl commands
        get("/hello", (req, res) -> "Hello World");
        
        put("/hello", (req, res)->
        {return "put Success!!";
        });
        
        post("/hello", (req, res)->
        {String name = req.queryParams("name");
        	return name;
        });
        
        get("/contact/elastic/:name", (req, res)-> {
        	
        	String def = "N/A";
        	String defSize = "10";
        	String defOffset = "0";
        	String pageSize = req.queryParamOrDefault("pageSize", defSize);
        	String pageOffset = req.queryParamOrDefault("pageOffset", defOffset);
        	String query = req.queryParamOrDefault("query", def);
        	String name = "";
        	name = req.params(":name");
        	
        	contact elasticTestContact = new contact("simon2","yowza","goodEmail","badNum");
        	contactManager.testElasticAdd(elasticTestContact);
        	ArrayList<String> myResults = contactManager.testElasticGet(name);
        	String toReturn = "";
        	for(int i = 0; i < myResults.size(); i++)
        	{
        		toReturn = (toReturn + (myResults.get(i)));
        	}
        	return mainMapper.writeValueAsString(toReturn);
        });
        
        
        //required handlers for Address Book
        //GET ALL + Query
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
