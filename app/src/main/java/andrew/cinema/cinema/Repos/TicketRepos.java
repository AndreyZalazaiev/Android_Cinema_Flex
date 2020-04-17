package andrew.cinema.cinema.Repos;



import java.util.List;

import andrew.cinema.cinema.Entities.Ticket;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface TicketRepos {
    @GET("/tickets/addmany")
    Call<String> addTickets(@Query("idaccount")  String idaccount,
                           @Query("idsession") Integer idsession,
                           @Query("price")String price,
                           @Query("place")String place,
                           @Query("row")String rownum
                          );
    @GET("tickets/this")
    Call<List<Ticket>> getTickets(@Query("idsession") Integer id);
}
