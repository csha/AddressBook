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
	
	//There are currently 6 Unit tests. (Query/Get/Post/Delete/Put/Page Alg.)
	//	Methods to test are: 
	// 	Query): contactManager.querySearchMethod(givenQuery)
	// 	Post): contactManager.elasticPost(testingPostContact)
	// 	Get): contactManager.elasticGet(testingGetContact)
	// 	Delete): contactManager.elasticDelete(testingDeleteContact)
	// 	Put): contactManager.elasticGet(testingPutContact2)
	
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
		//elasticPost() will automatically prevent a contact from being added if the name is taken
		contact queryContactToAdd1 = new contact("kanyeWestClone1", "Chicago", "gr8est@gmail.com", "19003334444");
		contact queryContactToAdd2 = new contact("kanyeWestClone2", "Chicago", "gr8est@gmail.com", "19003334444");
		contact queryContactToAdd3 = new contact("kanyeWestClone3", "Chicago", "gr8est@gmail.com", "19003334444");
		contact queryContactToAdd4 = new contact("TheRealKanyeWest", "Chicago", "gr8est@gmail.com", "19003334444");
		contactManager.elasticPost(queryContactToAdd1);
		contactManager.elasticPost(queryContactToAdd2);
		contactManager.elasticPost(queryContactToAdd3);
		contactManager.elasticPost(queryContactToAdd4);
		
		//Set query String **Note you have to escape everything pretty much
		//	Need Escapes:			+ - = && || > < ! ( ) { } [ ] ^ " ~ * ? : \ /
		String givenQuery = "\\{\"query_string\"\\:\\{\"address\"\\:\"Chicago\"\\}\\}";
		
		//contactManager.querySearchMethod(givenQuery):
		ArrayList<String> resultList = contactManager.querySearchMethod(givenQuery);
		
		//Print results
		for(int i = 0; i < resultList.size(); i++)
		{
			System.out.println(resultList.get(i));
		}
			
		//Expected Result contains all the KanyeWest Contacts Listed above.
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
				//		*Note, this might interfere with other tests if uniqueNameDelete is same as uniqueNamePut, etc.
				// Tests the following:
				// Deletes the contact with uniqueName
		contact testingPutContact = new contact(uniqueNamePut, "add2", "email2", "num2");
		assertEquals(true, contactManager.elasticPut(testingPutContact));
		String expectedResult = "{\"name\":\"" + uniqueNamePut + "\",\"address\":\"add2\",\"email\":\"email2\",\"phoneNumber\":\"num2\"}";
		String actualResult;
		contact testingPutContact2 = new contact(uniqueNamePut);
		ArrayList<String> resultList = contactManager.elasticGet(testingPutContact2);
		if(!(resultList.isEmpty()))
			{
				actualResult = resultList.get(0);
				assertEquals(expectedResult, actualResult);
			}
		
	}
	
	@Test
	public void testPageAlgorithm()
	{
		//Creating the test data. 
		ArrayList<Integer> testingList = new ArrayList<Integer>();
		for(int i = 1; i <= 100; i ++)
		{
			testingList.add(i);
		}
		
		//Setting desired page and index.
		int pageSize = 5;
		int pageOff = 3;
		int startingIndex = pageSize * pageOff + 1;
		int finishingIndex = startingIndex + pageOff;
		
		//Setting up test
		ArrayList<Integer> cutList = new ArrayList<Integer>();
		ArrayList<Integer> expectedList = new ArrayList<Integer>();
		expectedList.add(16); expectedList.add(17); expectedList.add(18); 
		
		//Actual algorithm pasted from main.java
		if(startingIndex <= testingList.size()-1) 
		{
			if(finishingIndex > testingList.size()-1) {finishingIndex = testingList.size()-1;}
			
			for (int i = startingIndex; i < finishingIndex; i++)
			{
				
				cutList.add(i);
			}
		}
		
		assertEquals(expectedList, cutList);
		
	}
	
}
