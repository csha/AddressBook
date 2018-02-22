Address Book
Author: Daniel Sha

to use: run main.java

****Notes below in order of importance****
1) Testing was done using "curl". This Java App works under the assumption that curl testing is accurate and fulfills the challenge's requirements.
	- 2 Examples of curl command (assuming cwd is curl.exe folder like "D:\code\curl\curl-7.58.0-win64-mingw\bin"
	- $ ./curl.exe http://localhost:8081/hello
	- $ ./curl.exe -X POST -d "name=frankStalone&address=PalmerSt" http://localhost:8081/contact
	- Side note* newest windows powershell created an alias for "curl" to invoke "Invoke-WebRequest" instead. Call curl.exe to resolve.
2) post uses queryParams {name & address & email & number} to resolve.
	- Must provide name queryParam. All others are optional.
	- Not sure if this is correct, but google doc didn't specify a method to require these attributes.
	- Will still create contact if any of three attributes besides name has length > reasonable amount, but will be set to N/A
3) get "/contact/:name" might not be able to handle names with spaces. 
	- $ ./curl.exe http://localhost:8081/contact/frank Stalone
	- Command above will fail in powershell with message "can not resolve host"
4) port can be changed by changing the variable "portToUse" near the top of this class file.
5) BasicConfigurator was imported to resolve an error. I still don't quite understand it to be honest.
