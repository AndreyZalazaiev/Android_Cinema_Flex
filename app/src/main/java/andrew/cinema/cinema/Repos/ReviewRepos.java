package andrew.cinema.cinema.Repos;

import java.util.List;

import andrew.cinema.cinema.Entities.Review;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ReviewRepos {
    @GET("/reviews/find")
    Call<List<Review>> findRev(@Query("idfilm") Integer id);
    @POST("/reviews/add")
    @FormUrlEncoded
    Call<String> addRev(
            @Field("idfilm") Integer idfilm,
            @Field("idaccount") String idaccount,
            @Field("text") String text,
            @Field("mark") Integer mark

    );
   /* @POST("/reviews/update")
    @FormUrlEncoded
    Call<String> Update(
            @Field("idreview") Integer idreview,
            @Field("text") String text,
            @Field("mark") Integer mark
    );
    */
   @GET("/reviews/update")
   Call<String> Update(@Query("idreview") Integer id,
                       @Query("text") String text,
                       @Query("mark") Integer mark);
}
