# ArtWoodCBA
A Minified Verison of Core Banking Application

# HOW TO USE

## RUN WITH DOCKER COMPOSE

Make sure docker is installed on your machine

Set your DB credentials in the enviroment section of the services in the docker compose

MYSQL_ROOT_PASSWORD: <your db password>
MYSQL_ROOT_USER: <your db username>

run the docker compose file with the command below

cmd: docker compose up -d

application server runs on port 9081

## RUN WITHOUT DOCKER COMPOSE

Clone the repository

Install MYSQL Database on Your local machine(if you do not already have one)

In the Application.yml set up your DB credentials for your local setUp

Run the application in preferred IDE

Docker image can be gotten with command

docker pull richieoscar/artwoodcba:lts

# TECHNOLOGIES 

JAVA 21

SPRINGBOOT 3.2.4


# CICD 

Github Actions



# Postman Documentation

Link : https://documenter.getpostman.com/view/18126363/2sA3Bj7tUW
