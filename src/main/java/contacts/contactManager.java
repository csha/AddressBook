package contacts;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 *
 * @author danie
 * 
 * Note: this class can use either elasticsearch storage or built in arrayList storage.
 * The reason is because I created the arrayList based methods first then the elasticsearch.
 * The storages (addressbooks) are independent of one another. main.java currently uses the elasticsearch.
 * 
 * 
 */
public class contactManager {
	
	static int portElasticSearch = 9300;
    private static ArrayList<contact> contactList = new ArrayList<contact>();
    private static ObjectMapper managerMapper = new ObjectMapper();
    
    
    static TransportClient client = new PreBuiltTransportClient(Settings.EMPTY)
	        .addTransportAddress(new TransportAddress(new InetSocketAddress("localhost", portElasticSearch)));
	
    
    //Constructors
    public contactManager(){}
    
    public contactManager(ArrayList<contact> contactList)
    {contactManager.contactList = contactList;}
    
    
    //getters/setters and other methods
    public ArrayList<contact> getContactList()
    {return contactList;}
    
    public void setContactList(ArrayList<contact> contactList)
    {contactManager.contactList = contactList;}
    
    public static void addContact(contact newContact) throws JsonProcessingException
    {
        contactList.add(newContact);
        String jsonString = managerMapper.writeValueAsString(newContact);
        //Index = addressBook, type = contact
        IndexResponse response = client.prepareIndex("addressBook", "contact")
                .setSource(jsonString, XContentType.JSON)
                .get();
        
    }
    
    public static boolean elasticPost(contact newContact ) throws JsonProcessingException
    {
    	ArrayList<String> results = testElasticGet(newContact.getName());
    	if(results.size() > 0)
    	{
    		return false; //already exists
    	}
    	
    	testElasticAdd(newContact);
    	
    	return true;
    }
    
    public static boolean elasticPutAdd(contact newContact) throws JsonProcessingException
    {
    	testElasticAdd(newContact);
    		return true;
    	
    }
    
    public static boolean elasticDelete(contact newContact ) throws JsonProcessingException
    {   	
    	/*
    	 * BulkByScrollResponse response =
    	DeleteByQueryAction.INSTANCE.newRequestBuilder(client)
        .filter(QueryBuilders.matchQuery("gender", "male")) 
        .source("persons")                                  
        .get();                                             

		long deleted = response.getDeleted();        
    	 * */
    	
    	
    	QueryBuilder matchSpecificFieldQuery= QueryBuilders
    			  .matchQuery("name", newContact.getName());

      	SearchResponse response = client.prepareSearch().setQuery(matchSpecificFieldQuery).execute().actionGet();
      	List<SearchHit> searchHits = Arrays.asList(response.getHits().getHits());
      	if(searchHits.size() == 0)
    	{
    		return false; //doesn't exist to delete
    	}
      	//String docId = "";
      	//for(SearchHit h: searchHits)
      	//{
      	//	docId = h.getId();
      	//}
      	//DeleteResponse delResponse = client.prepareDelete().setQuery(matchSpecificFieldQuery)
      		  //.get();
      	
      	BulkByScrollResponse delresponse = DeleteByQueryAction.INSTANCE.newRequestBuilder(client)
      			.filter(QueryBuilders.matchQuery("name", newContact.getName())) 
      	        .source("addressbook")                                  
      	        .get(); 
      	
    	return true;
    }
    
    public static ArrayList<String> elasticGet(contact newContact) throws JsonProcessingException
    {
    	ArrayList<String> jsonResult = testElasticGet(newContact.getName());
    	return jsonResult;
    }
    
    public static void testElasticAdd(contact newContact ) throws JsonProcessingException
    {
    	String jsonString = managerMapper.writeValueAsString(newContact);
        //Index = addressBook, type = contact
        IndexResponse response = client.prepareIndex("addressbook", "contact")
                .setSource(jsonString, XContentType.JSON)
                .get();
    }
    
    public static ArrayList<String> testElasticGet(String nameToFind) throws JsonProcessingException
    {
    	
    	QueryBuilder matchSpecificFieldQuery= QueryBuilders
  			  .matchQuery("name", nameToFind);
  	
    	
    	SearchResponse response = client.prepareSearch().setQuery(matchSpecificFieldQuery).execute().actionGet();
    	List<SearchHit> searchHits = Arrays.asList(response.getHits().getHits());
    	ArrayList<String> results = new ArrayList<String>();
    	searchHits.forEach(
    	  hit -> {
    		  String toAdd = hit.getSourceAsString();
    		  results.add(toAdd);
    	  });
    	return results;
    }
    
    public static ArrayList<String> testElasticGetAll() throws JsonProcessingException
    {
    	SearchResponse response = client.prepareSearch().execute().actionGet();
    	List<SearchHit> searchHits = Arrays.asList(response.getHits().getHits());
    	ArrayList<String> results = new ArrayList<String>();
    	searchHits.forEach(
    	  hit -> {
    		  String toAdd = hit.getSourceAsString();
    		  results.add(toAdd);
    	  });
    	return results;
    }
    
    public boolean addContactByName(String name) //returns t/f on success/fail
    {
        for(contact c : contactList)
        {
            if(name.equals(c.toString()))
            {return false;}
        }
        contact newContact = new contact(name);
        contactList.add(newContact);
        return true;
    }
    
    public boolean addContactByFull(String name, String address, String email, String number) //returns t/f on success/fail
    {
        for(contact c : contactList)
        {
            if(name.equals(c.toString()))
            {return false;}
        }
        contact newContact = new contact(name,address,email,number);
        contactList.add(newContact);
        return true;
    }
    
    public boolean contains(String name)
    {
        for(contact c : contactList)
        {
            if(name.equals(c.toString()))
            {return true;}
        }
        return false;
    }
    
    public void replace(contact oldCt, String address, String email, String number)
    {
    	if(!(address.equals("N/A")))
    	{
    		oldCt.setAddress(address);
    	}
    	if(!(email.equals("N/A")))
    	{
    		oldCt.setEmail(email);
    	}
    	if(!(number.equals("N/A")))
    	{
    		oldCt.setPhoneNumber(number);
    	}
    }
    
    public contact getContact(String name)
    {
    	for(contact c: contactList)
    	{
    		if(name.equals(c.toString()))
    		{return c;}
    	}
    	//This is pretty sloppy code and assumes contains will always be called beforehand.
    	contact nullContact = new contact();
    	return nullContact;
    }
    
    
    public boolean deleteContact(String name) 
    {
        for(contact c : contactList)
        {
            if(name.equals(c.toString()))
            {   contactList.remove(c);
                return true;
            }
        }
        return false;
    }
    
    
    
}