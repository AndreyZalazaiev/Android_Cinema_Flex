package andrew.cinema.cinema.Entities;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "idhall",
        "idcinema",
        "type",
        "name"
})
public class Halls {

    @JsonProperty("idhall")
    private Integer idhall;
    @JsonProperty("idcinema")
    private Integer idcinema;
    @JsonProperty("type")
    private String type;
    @JsonProperty("name")
    private String name;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("idhall")
    public Integer getIdhall() {
        return idhall;
    }

    @JsonProperty("idhall")
    public void setIdhall(Integer idhall) {
        this.idhall = idhall;
    }

    @JsonProperty("idcinema")
    public Integer getIdcinema() {
        return idcinema;
    }

    @JsonProperty("idcinema")
    public void setIdcinema(Integer idcinema) {
        this.idcinema = idcinema;
    }

    @JsonProperty("type")
    public String getType() {
        return type;
    }

    @JsonProperty("type")
    public void setType(String type) {
        this.type = type;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}