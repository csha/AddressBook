Address Book
Author: Daniel Sha

to use: run main.java

****Notes below in order of importance****

1) Elasticsearch now has their own java rest api, using it seems to defeat the purpose of this exercise so I did not.
	- However a large portion of their documentation for Java is outdated. Nodes and transportclients have been deprecated to promote their own Java API.
	- So I built this first using an arrayList/JSON instead of Indexes and attempted to integrate Elasticsearch afterwards.
2) Testing was done using "curl". This Java App works under the assumption that curl testing is accurate and fulfills the challenge's requirements.
	- 2 Examples of curl command (assuming cwd is curl.exe folder like "D:\code\curl\curl-7.58.0-win64-mingw\bin"
	- $ ./curl.exe http://localhost:8081/hello
	- $ ./curl.exe -X POST -d "name=frankSobotka&address=PalmerSt" http://localhost:8081/contact
	- Side note* newest windows powershell created an alias for "curl" to invoke "Invoke-WebRequest" instead. Call curl.exe to resolve.
3) post uses queryParams {name & address & email & number} to resolve.
	- Must provide name queryParam. All others are optional.
	- Not sure if this is correct, but google doc didn't specify a method to require these attributes.
	- Will still create contact if any of three attributes besides name has length > reasonable amount, but will be set to N/A
4) get "/contact/:name" might not be able to handle names with spaces. 
	- $ ./curl.exe http://localhost:8081/contact/frank Sobotka
	- Command above will fail in powershell with message "can not resolve host"
5) port can be changed by changing the variable "portToUse" near the top of this class file.
