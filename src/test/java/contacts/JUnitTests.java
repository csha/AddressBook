package contacts;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;

public class JUnitTests {

	//Because names have to be unique, these strings should be changed accordingly.
	//On the first run, GET, Delete, and Put should fail if the respective string:contact pairs do not already exist.
	static String uniqueNamePost = "JohnWick32";
	static String uniqueNameGet = "JohnWick21";
	static String uniqueNameDelete = "JohnWick31";
	static String uniqueNamePut = "JohnWick29";
	
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
