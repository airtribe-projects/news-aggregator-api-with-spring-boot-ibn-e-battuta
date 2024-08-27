# Air News Aggregator

## Table of Contents
1. [Introduction](#introduction)
2. [Features](#features)
3. [Technologies](#technologies)
4. [Setup](#setup)
5. [API Endpoints](#api-endpoints)
6. [Usage](#usage)
7. [Contributing](#contributing)
8. [License](#license)

## Introduction

Air News Aggregator is a Spring Boot application that provides a RESTful API for aggregating and managing news articles. It allows users to register, login, set news preferences, and interact with news articles from various sources.

## Features

- User registration and authentication with JWT
- Customizable news preferences
- Fetch top headlines and search for news articles
- Mark articles as read or favorite
- Caching mechanism for improved performance
- Periodic updates of news articles

## Technologies

- Java 21
- Spring Boot 3.3.2
- Spring Security
- Spring Data JPA
- H2 Database
- JWT for authentication
- Caffeine for caching
- Maven for dependency management

## Setup

1. Clone the repository:
   ```
   git clone https://github.com/yourusername/air-news-aggregator.git
   ```

2. Navigate to the project directory:
   ```
   cd air-news-aggregator
   ```

3. Build the project:
   ```
   ./mvnw clean install
   ```

4. Run the application:
   ```
   ./mvnw spring-boot:run
   ```

The application will start running at `http://localhost:8085`.

## API Endpoints

### Authentication
- `POST /api/auth/register`: Register a new user
- `POST /api/auth/login`: Login and receive JWT token
- `POST /api/auth/refresh-token`: Refresh JWT token
- `GET /api/auth/verify`: Verify user account
- `POST /api/auth/re-verify`: Resend verification token

### News Preferences
- `GET /api/preferences`: Get user's news preferences
- `PUT /api/preferences`: Update user's news preferences

### News
- `GET /api/news/top-headlines`: Get top headlines
- `GET /api/news/search/{keyword}`: Search for news articles
- `GET /api/news/everything`: Get news articles based on various criteria
- `POST /api/news/read`: Mark an article as read
- `POST /api/news/favorite`: Mark an article as favorite
- `POST /api/news/un-read/{articleId}`: Unmark an article as read
- `POST /api/news/un-favorite/{articleId}`: Unmark an article as favorite
- `GET /api/news/read`: Get all read articles
- `GET /api/news/favorites`: Get all favorite articles

## Usage

1. Register a new user using the `/api/auth/register` endpoint.
2. Verify your account using the link sent to your email (simulated in logs for this project).
3. Login using the `/api/auth/login` endpoint to receive a JWT token.
4. Use the JWT token in the Authorization header for subsequent requests.
5. Set your news preferences using the `/api/preferences` endpoint.
6. Start fetching news and interacting with articles using the various `/api/news` endpoints.