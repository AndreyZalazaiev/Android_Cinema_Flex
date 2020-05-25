package andrew.cinema.cinema.Repos;

import java.util.List;

import andrew.cinema.cinema.Entities.Film;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface FilmRepos {
    @GET("/films")
    Call<List<Film>> getData();
    @GET("/films/actual")
    Call<List<Film>> getActual();
    @GET("/films/rating")
    Call<String> getRating(@Query("idfilm") Integer id);
}
