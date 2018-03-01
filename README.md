Address Book
Author: Daniel Sha

to use(windows): (run elasticsearch.exe, run main.java)

****Notes below in order of importance****

1) All handlers in main.java besides get Contact/?pageSize={}&pageOffset={}&query={} work.
	- The method: contactManager.querySearchMethod(givenQuery) works.
  	- Problem is that queryParamOrDefault always returns the default value in @get, but it works properly for @Post.
2) Testing was done using "curl" and unitTesting. 
	- Run JUnitTests.java for unit tests.
	- 4 Examples of curl command testing (assuming cwd is curl.exe folder like "D:\code\curl\curl-7.58.0-win64-mingw\bin"
	- $ ./curl.exe -X POST -d "name=frankSobotka&address=PalmerSt" http://localhost:8081/contact
	- $ ./curl.exe -X GET http://localhost:8081/contact/frankSobotka
	- $ ./curl.exe -X PUT -d "address=NewAddress" http://localhost:8081/contact/frankSobotka
	- $ ./curl.exe -X DELETE http://localhost:8081/contact/frankSobotka
	- Side note* newest windows powershell created an alias for "curl" to invoke "Invoke-WebRequest" instead. Call curl.exe to resolve.
3) post uses queryParams {name & address & email & number} to resolve.
	- Ex: $ ./curl.exe -X POST -d "name=FrankSobotka&address=Baltimore&email=baltimoreman@gmail.com&number=5555555" http://localhost:8081/contact
	- Must provide name queryParam. All others are optional.
	- Not sure if this is correct, but google doc didn't specify a method to require these attributes.
	- Will still create contact if any of three attributes besides name has length > reasonable amount, but will be set to N/A
4) get "/contact/:name" might not be able to handle names with spaces. 
	- $ ./curl.exe http://localhost:8081/contact/frank Sobotka
	- Command above will fail in powershell with message "can not resolve host"
5) ports can be changed by changing the variable "portToUse"(main.java) & "portElasticSearch"(contactManager.java) near the top of this class file.
6) Elasticsearch now has their own java rest api, and is deprecating many methods/classes rendering much documentation obsolete.
	- Thus this was built first using an arrayList/JSON instead of Indexes and attempted to integrate Elasticsearch afterwards.
