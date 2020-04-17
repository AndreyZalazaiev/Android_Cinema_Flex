package andrew.cinema.cinema.Repos;

import java.sql.Date;
import java.util.List;


import andrew.cinema.cinema.Entities.Account;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface AccountRepos {
    @GET("/accounts/find")
    Call<Account> isExist(@Query("id") String id);
    @POST("/accounts/add")
    @FormUrlEncoded
    Call<String> registerUser(@Field("idaccount") String id,
                              @Field("picture") String pic,
                              @Field("bonus") Integer bonus,
                              @Field("name") String name,
                              @Field("email") String email,
                              @Field("date") String date
    );
    @GET("/accounts")
    Call<List<Account>> shortAccounts();
}
