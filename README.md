# CineMatch

CineMatch is a desktop application for movie discovery and recommendation. Built with JavaFX and Spring Boot, it leverages The Movie Database (TMDB) API to provide users with up-to-date movie information, search capabilities, and personalized recommendations.

## Features

*   **Discover Movies**: Fetch lists of popular movies and movies currently playing in theaters ("What's Hot").
*   **Search**: Find movies by title.
*   **Detailed Info**: Retrieve comprehensive details for specific movies, including reviews from TMDB and other users.
*   **Genre Filtering**: Browse movies by specific genres.
*   **Personalized Suggestions**: Get movie recommendations based on your starred movies.
*   **Movie Quiz**: Test your movie knowledge with a fun quiz.
*   **Leaderboard**: Compete with other users and see who has the highest quiz score.
*   **User Accounts**: Register and log in to star movies, write reviews, and get personalized suggestions.

## Tech Stack

*   **Java**: Core programming language (requires Java 11+ due to `HttpClient` usage).
*   **JavaFX**: Used for the graphical user interface.
*   **Spring Boot**: Framework for creating the backend service.
*   **Gson**: Used for JSON parsing of external API responses.
*   **TMDB API**: External data source for movie metadata.
*   **Google Gemini API**: Used to generate quiz questions.

## Getting Started

### Prerequisites

*   Java Development Kit (JDK) 11 or higher.
*   Maven or Gradle.
*   An API Key from The Movie Database (TMDB).
*   An API Key from Google AI Studio for the Gemini API.

### Configuration

You must configure your API keys for the application to function. Set the following environment variables:

*   `TMDB_API_KEY`: Your API key from The Movie Database.
*   `GOOGLE_API_KEY`: Your API key for the Google Gemini API.

For example, on Linux or macOS, you can set the environment variables like this:

```bash
export TMDB_API_KEY=YOUR_TMDB_API_KEY
export GOOGLE_API_KEY=YOUR_GOOGLE_API_KEY
```

On Windows, you can set them like this:

```bash
set TMDB_API_KEY=YOUR_TMDB_API_KEY
set GOOGLE_API_KEY=YOUR_GOOGLE_API_KEY
```

### Running the Application

1.  Clone the repository.
2.  Build the project using your preferred build tool (e.g., `mvn clean install`).
3.  Run the application by running the `HelloApplication` class.

## Contributing

We welcome contributions! Please see our CONTRIBUTING.md for guidelines on how to report bugs, suggest enhancements, and submit pull requests.

## Code of Conduct

Please note that this project is released with a Code of Conduct. By participating in this project you agree to abide by its terms.
