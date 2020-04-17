package andrew.cinema.cinema.Repos;

import java.util.List;

import andrew.cinema.cinema.Entities.Film;

import retrofit2.Call;
import retrofit2.http.GET;

public interface FilmRepos {
    @GET("/films")
    Call<List<Film>> getData();
}
