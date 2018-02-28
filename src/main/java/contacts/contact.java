package contacts;

public class contact {
    int maxAddressLength = 50;
    int maxEmailLength = 50;
    int maxPhoneNumberLength = 12;
    
    
    private String name;
    private String address;
    private String email;
    private String phoneNumber;
    
    public contact(){ //This is because every contact needs a name. SHould never be used.
    	name = "Error occured. probs Manager get call without contains check first";
    }
    
    public contact(String name)
    {
        this.name = name;
    }
    
    public contact(String name, String address, String email, String phoneNumber)
    {
        this.name = name;
        if(address.length() <= maxAddressLength){this.address = address;}
        else{this.address = "N/A";}
        if(email.length() <= maxEmailLength){this.email = email;}
        else{this.email = "N/A";}
        if(phoneNumber.length() <= maxPhoneNumberLength){this.phoneNumber = phoneNumber;}
        else{this.phoneNumber = "N/A";}
    }
    
    public String getName()
    {return name;}
   
    
    public void setName(String name)
    {this.name = name;}
    
    @Override
    public String toString(){ //ostensibly the same as getName
        return name;
    }
    
    public String getAddress(){
    return address;
    }
    
    public void setAddress(String address)
    {
        if(address.length() <= maxAddressLength){this.address = address;}
        else{this.address = "N/A";}
    }
    
    public String getEmail(){
    return email;
    }
    
    public void setEmail(String email)
    {if(email.length() <= maxEmailLength){this.email = email;}
    else{this.email = "N/A";}
    }
    
    public String getPhoneNumber(){
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber){
        if(phoneNumber.length() <= maxPhoneNumberLength){this.phoneNumber = phoneNumber;}
        else{this.phoneNumber = "N/A";}
    }
}
