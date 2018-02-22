package contacts;

import java.util.ArrayList;

/**
 *
 * @author danie
 */
public class contactManager {
    private static ArrayList<contact> contactList = new ArrayList<contact>();
    
    //Constructors
    public contactManager(){}
    
    public contactManager(ArrayList<contact> contactList)
    {contactManager.contactList = contactList;}
    
    
    //getters/setters and other methods
    public ArrayList<contact> getContactList()
    {return contactList;}
    
    public void setContactList(ArrayList<contact> contactList)
    {contactManager.contactList = contactList;}
    
    public static void addContact(contact newContact)
    {
        contactList.add(newContact);
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