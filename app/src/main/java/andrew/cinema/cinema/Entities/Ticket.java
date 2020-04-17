package andrew.cinema.cinema.Entities; ;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "idticket",
        "idaccount",
        "idsession",
        "price",
        "place",
        "rownum"
})

@Data
public class Ticket {

    @JsonProperty("idticket")
    private Integer idticket;
    @JsonProperty("idaccount")
    private String idaccount;
    @JsonProperty("idsession")
    private Integer idsession;
    @JsonProperty("price")
    private Float price;
    @JsonProperty("place")
    private Integer place;
    @JsonProperty("rownum")
    private Integer rownum;

}