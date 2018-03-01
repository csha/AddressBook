package contacts;

import static org.junit.Assert.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Arrays;
import java.util.List;
import org.apache.log4j.BasicConfigurator;
import org.elasticsearch.*;
import java.util.ArrayList;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;

public class JUnitTests {
	
	//There are currently 5 Unit tests. (Query/Get/Post/Delete/Put)
	
	//Because names have to be unique, these strings should be changed accordingly with each run.
	//On the first run: Get, Delete, and Put should fail if the respective (uniqueNameString:contact) pairs do not already exist.
	static String uniqueNamePost = "JohnWick32";
	static String uniqueNameGet = "JohnWick21";
	static String uniqueNameDelete = "JohnWick31";
	static String uniqueNamePut = "JohnWick29";
	
	
	@Test
	public void testQuery() throws JsonProcessingException
	{
		ObjectMapper objMapper = new ObjectMapper();
		
		//Below code ensures that the desired query items are created.
		//ElasticPost will automatically prevent a contact from being added if 
		contact queryContactToAdd1 = new contact("kanyeWestClone1", "Chicago", "gr8est@gmail.com", "19003334444");
		contact queryContactToAdd2 = new contact("kanyeWestClone2", "Chicago", "gr8est@gmail.com", "19003334444");
		contact queryContactToAdd3 = new contact("kanyeWestClone3", "Chicago", "gr8est@gmail.com", "19003334444");
		contact queryContactToAdd4 = new contact("TheRealKanyeWest", "Chicago", "gr8est@gmail.com", "19003334444");
		contactManager.elasticPost(queryContactToAdd1);
		contactManager.elasticPost(queryContactToAdd2);
		contactManager.elasticPost(queryContactToAdd3);
		contactManager.elasticPost(queryContactToAdd4);
		
		//Set query String **Note you have to escape everything pretty much
		String givenQuery = "\\{\"query_string\"\\:\\{\"address\"\\:\"Chicago\"\\}\\}";
		
		//contactManager.querySearchMethod(givenQuery):
		ArrayList<String> resultList = contactManager.querySearchMethod(givenQuery);
		
		//Print results
		for(int i = 0; i < resultList.size(); i++)
		{
			System.out.println(resultList.get(i));
		}
			
		//Expected Result contains all the Kanye West Contacts Listed above.
		String expectedResult = "[\"{\\\"name\\\":\\\"TheRealKanyeWest\\\",\\\"address\\\":\\\"Chicago\\\",\\\"email\\\":\\\"gr8est@gmail.com\\\",\\\"phoneNumber\\\":\\\"19003334444\\\"}\",\"{\\\"name\\\":\\\"kanyeWestClone3\\\",\\\"address\\\":\\\"Chicago\\\",\\\"email\\\":\\\"gr8est@gmail.com\\\",\\\"phoneNumber\\\":\\\"19003334444\\\"}\",\"{\\\"name\\\":\\\"kanyeWestClone1\\\",\\\"address\\\":\\\"Chicago\\\",\\\"email\\\":\\\"gr8est@gmail.com\\\",\\\"phoneNumber\\\":\\\"19003334444\\\"}\",\"{\\\"name\\\":\\\"kanyeWestClone2\\\",\\\"address\\\":\\\"Chicago\\\",\\\"email\\\":\\\"gr8est@gmail.com\\\",\\\"phoneNumber\\\":\\\"19003334444\\\"}\"]";
		assertEquals(expectedResult, objMapper.writeValueAsString(resultList));
	}
	
	
	
	@Test
	public void testPost() throws JsonProcessingException{
		
		// *Test succeeds if no other contact exists with name == uniqueNamePost
		// Tests the following:
		// Post creates contact on elasticserver iff the name does not already exist.
		// Test will succeed on first run, but fail on subsequent ones unless uniqueName is changed.
		
		contact testingPostContact = new contact(uniqueNamePost, "add1", "email1", "num1");
		assertEquals(true, contactManager.elasticPost(testingPostContact));
	}
	
	@Test
	public void testGet() throws JsonProcessingException{
		
		// *Test succeeds if contact with uniqueNameGet exists in elasticsearch.
		// Tests the following:
		// Gets the contact with uniqueName
		
		String expectedResult = "{\"name\":\"" + uniqueNameGet + "\",\"address\":\"add1\",\"email\":\"email1\",\"phoneNumber\":\"num1\"}";
		String actualResult;
		contact testingGetContact = new contact(uniqueNameGet);
		ArrayList<String> resultList = contactManager.elasticGet(testingGetContact);
		actualResult = resultList.get(0);
		assertEquals(expectedResult, actualResult);
	}
	
	@Test
	public void testDelete() throws JsonProcessingException
	{
				// *Test succeeds if contact with uniqueNameDelete exists in elasticsearch.
				//		*Note, this might interfere with other tests if uniqueNameDelete is same as uniqueNamePost, etc.
				// Tests the following:
				// Deletes the contact with uniqueName
		contact testingDeleteContact = new contact(uniqueNameDelete);
		assertEquals(true, contactManager.elasticDelete(testingDeleteContact));
	}

	
	@Test
	public void testPut() throws JsonProcessingException
	{
				// *Test succeeds if contact with uniqueNameDelete exists in elasticsearch.
				//		*Note, this might interfere with other tests if uniqueNameDelete is same as uniqueNamePost, etc.
				// Tests the following:
				// Deletes the contact with uniqueName
		contact testingPutContact = new contact(uniqueNamePut, "add2", "email2", "num2");
		assertEquals(true, contactManager.elasticPut(testingPutContact));
		String expectedResult = "{\"name\":\"" + uniqueNamePut + "\",\"address\":\"add2\",\"email\":\"email2\",\"phoneNumber\":\"num2\"}";
		String actualResult;
		contact testingPutContact2 = new contact(uniqueNamePut);
		ArrayList<String> resultList = contactManager.elasticGet(testingPutContact2);
		actualResult = resultList.get(0);
		assertEquals(expectedResult, actualResult);
		
	}
	
	
}
