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
- MySQL v8.0.30


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

**REST API Documentation:**

For the ease of use, due to the fact that app only exposes rest api, 
deployed version comes with SwaggerUI.

Below is the quick overview of requests.


**User Controller:**

![user controller](https://github.com/barnackles/Home-Budget-App/blob/main/swaggerUiImages/userController.png?raw=true)

**Login Controller:**

![login controller](https://github.com/barnackles/Home-Budget-App/blob/main/swaggerUiImages/loginController.png?raw=true)

**Budget Controller:**

![budget controller](https://github.com/barnackles/Home-Budget-App/blob/main/swaggerUiImages/budgetController.png?raw=true)

**Operation Controller:**

![operation controller](https://github.com/barnackles/Home-Budget-App/blob/main/swaggerUiImages/operationController.png?raw=true)

**Category Controller:**

![category controller](https://github.com/barnackles/Home-Budget-App/blob/main/swaggerUiImages/categoryController.png?raw=true)

**How to use the app:**

To test the application you should register an account using /register (creatUser) endpoint.
After passing the request to the controller you will receive an e-mail requesting the confirmation of your email.

Clicking on the activation link will activate your account, otherwise your account will be deleted after 15 minutes.

Access to all secured endpoints requires a validd token.
To sign in with created user use /login (login) endpoint and provide valid credentials. As a response you should receive valid JWT access and refresh tokens. Click on Authorize button and paste received value to "Value:" field.
Now you should be able to send the requests to secured endpoints.

Bearer token is valid for 10 minutes, afterwards you need to use refresh token on /refresh (refreshToken) endpoint passing received refresh token value via SwaggerUI's "Value:" field or use /login (login) endpoint again. Bear in mind that After five failed login attempts your IP will be locked out.

Now you can add budgets and transactions and display them using provided endpoints.


