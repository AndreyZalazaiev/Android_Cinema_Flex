package andrew.cinema.cinema.Repos;

import java.util.List;


import andrew.cinema.cinema.Entities.Cinema;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface CinemaRepos {
        @GET("/cinema")
        Call<List<Cinema>> getData();

}
