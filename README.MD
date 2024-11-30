# About the project
This is a multi-modules Maven project, aim to address the requirements described for the Customer App.
## Structure
The project has 3 modules, each address particular requirements.
1. `org.example.common`: Contains common things like `DTO`, which are used in other modules
2. `org.example.coffee-shop`: This is a small project contains APIs for supporting the main module.
Although the Deliverable doesn't require to implement this module, but in my point of view,
the coffee-shop should be the place in which we put the logic to, as it serve the business of the shops.
**AND, BELIEVE ME, YOU NEED TO TAKE A LOOK AT THIS MODULE**.
3. `org.example.coffee-shop-customer`: The main module that solves Part 2 of the requirements.
It need some APIs from the `org.example.coffee-shop`.