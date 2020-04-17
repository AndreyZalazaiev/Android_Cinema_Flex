package andrew.cinema.cinema.Repos;

import java.util.List;

import andrew.cinema.cinema.Entities.Account;
import andrew.cinema.cinema.Entities.Session;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface SessionRepos {
    @GET("/sessions/find")
    Call<List<Session>> Sessions(@Query("idfilm") int id);
}
