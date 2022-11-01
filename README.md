# Home Budget App
 
**About project**

Repository contains web application for managing home budget(s). Application was built using Spring Boot and Spring Security. Application exposes REST API.

**Idea behind the project**

The idea was to create web application simplifying home finance management. Apart from the main goal, which was to build the app, project allowed me and still allows me to learn technologies and develop the neccessary skillset.  
App is currently deployed on DigitalOcean droplet using Docker Compose with self-signed certificate.


Current version: 0.2.0

**Technologies:**

- Java 17
- Spring Boot v2.6.10
- Spring Security
- MySQL


**Features:**

With this application you can:
- register your account
- add budgets to your profile
- manage your financial operations
- display total balance for budget and total income, expenses and savings,
- display total balance for budget and total income, expenses and savings for recent week, recent month, 
recent year and custom range of dates.
App contains full registration process as well as forgot password feature.


**Features in development:**

- migratiion to spring security 2.7+
- introduction of OAuth 2.0 
- loading operations from .csv file
- test coverage

**REST API Documentation**

For the ease of use, due to the fact that app only exposes rest api, 
deployed version comes with SwaggerUI.








