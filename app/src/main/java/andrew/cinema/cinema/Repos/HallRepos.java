package andrew.cinema.cinema.Repos;

import java.util.List;

import andrew.cinema.cinema.Entities.Halls;
import andrew.cinema.cinema.Entities.Session;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface HallRepos {
    @GET("/halls/find")
    Call<List<Halls>> Halls(@Query("idcinema") int id);
}
