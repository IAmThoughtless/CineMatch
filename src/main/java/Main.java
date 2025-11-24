import com.google.gson.Gson;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


    public static void main(String[] args) throws Exception {

        String apiKey = "71890bc04b3c153a8abf55ea6cdfbe46";
        if (apiKey == null) {
            System.out.println("ERROR: TMDB_API_KEY environment variable not set!");
            return;
        }

        String url = "https://api.themoviedb.org/3/movie/popular?api_key=" + apiKey;

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        Gson gson = new Gson();
        MovieResponse movieResponse =
                gson.fromJson(response.body(), MovieResponse.class);

        printMovies(movieResponse);
    }

    private static void printMovies(MovieResponse movieResponse) {
        System.out.println("\n===============================================");
        System.out.println("         🎬 POPULAR MOVIES");
        System.out.println("===============================================\n");
        int count = 0;
        for (Movie m : movieResponse.results) {
            if (count>=10){
                break;
            }
            System.out.println("🎬 Title: " + m.title);
            System.out.println("📅 Release Date: " + m.release_date);
            System.out.println("⭐ Rating: " + m.vote_average + " (" + m.vote_count + " votes)");
            System.out.println("📈 Popularity: " + Math.round(m.popularity));
            System.out.println("📝 Overview: "
                    + (m.overview.length() > 140
                    ? m.overview.substring(0, 140) + "..."
                    : m.overview));
            System.out.println("-----------------------------------------------------");
            count++;
        }
    }

