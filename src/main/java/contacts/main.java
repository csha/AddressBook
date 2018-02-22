package contacts;

import static spark.Spark.*;
import org.apache.log4j.BasicConfigurator;
import com.fasterxml.jackson.databind.ObjectMapper;

/*
 * 	Author: Daniel Sha
 * 
 * 	****Notes in order of importance****
 * 
 * 	1) Testing was done using "curl". This Java App works under the assumption that curl testing is accurate and fulfills the challenge's requirements.
 * 		- 2 Examples of curl command (assuming cwd is curl.exe folder like "D:\code\curl\curl-7.58.0-win64-mingw\bin"
 * 		- $ ./curl.exe http://localhost:8081/hello
 * 		- $ ./curl.exe -X POST -d "name=frankStalone&address=PalmerSt" http://localhost:8081/contact
 *		- Side note* newest windows powershell created an alias for "curl" to invoke "Invoke-WebRequest" instead. Call curl.exe to resolve.
 * 	2) post uses queryParams {name & address & email & number} to resolve.
 * 		- Must provide name queryParam. All others are optional.
 * 		- Not sure if this is correct, but google doc didn't specify a method to require these attributes.
 * 		- Will still create contact if any of three attributes besides name has length > reasonable amount, but will be set to N/A
 * 	3) get "/contact/:name" might not be able to handle names with spaces. 
 * 		- $ ./curl.exe http://localhost:8081/contact/frank Stalone
 * 		- Command above will fail in powershell with message "can not resolve host"
 * 	4) port can be changed by changing the variable "portToUse" near the top of this class file.
 * 	5) BasicConfigurator was imported to resolve an error. I still don't quite understand it to be honest.
 * 	 
 * 
 */

public class main {
	
	static int portToUse = 8081;  
	private static contactManager mainManager = new contactManager();
	private static ObjectMapper mainMapper = new ObjectMapper();
	
	public static void main(String[] args) {
		BasicConfigurator.configure();
		port(portToUse);
		
		//three test handlers to check Curl
        get("/hello", (req, res) -> "Hello World");
        
        put("/hello", (req, res)->
        {return "put Success!!";
        });
        
        post("/hello", (req, res)->
        {String name = req.queryParams("name");
        	return name;
        });
        
        
        //required handlers for Address Book
        //GET ALL + Query
        get("/contact", (req, res)-> {
            return "PLACEHOLDER PLACEHOLDER PLACEHOLDER PLACEHOLDER";
        });
        
        //POST
        post("/contact", (req, res)->{
        	String name = req.queryParams("name");
        	String def = "N/A";
        	String address = req.queryParamOrDefault("address", def);
        	String email = req.queryParamOrDefault("email", def);
        	String number = req.queryParamOrDefault("number", def);
        	if(mainManager.contains(name)) //has to be unique
        		{
        			res.status(403);
        			return "Name is unique and already in contacts";
        		}
        	else
        	{
        		contact newContact = new contact(name, address, email, number);
        		contactManager.addContact(newContact);
        		res.status(201);
        		return "Success! \n" + mainMapper.writeValueAsString(newContact);
        	}
        	
        });
        
        //GET SPECIFIC CONTACT
        get("/contact/:name", (req,res)-> {
        	String name = req.params(":name");
        	if(mainManager.contains(name))
        	{
        		res.status(200);
        		contact desiredContact = mainManager.getContact(name);
        		return "Success! \n" + mainMapper.writeValueAsString(desiredContact);
        	}
        	else
        	{
        		res.status(404);
        		return "That name has not been found in our Address Book";
        	}
        		});
        
        
    }
}
