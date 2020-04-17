package andrew.cinema.cinema.Entities;


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
        "idsession",
        "idfilm",
        "idhall",
        "start",
        "end"
})
@Data
public class Session {

    @JsonProperty("idsession")
    private Integer idsession;
    @JsonProperty("idfilm")
    private Integer idfilm;
    @JsonProperty("idhall")
    private Integer idhall;
    @JsonProperty("start")
    private String start;
    @JsonProperty("end")
    private String end;
    @JsonProperty("baseprice")
    private Double baseprice;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("idsession")
    public Integer getIdsession() {
        return idsession;
    }

    @JsonProperty("idsession")
    public void setIdsession(Integer idsession) {
        this.idsession = idsession;
    }

    @JsonProperty("idfilm")
    public Integer getIdfilm() {
        return idfilm;
    }

    @JsonProperty("idfilm")
    public void setIdfilm(Integer idfilm) {
        this.idfilm = idfilm;
    }

    @JsonProperty("idhall")
    public Integer getIdhall() {
        return idhall;
    }

    @JsonProperty("idhall")
    public void setIdhall(Integer idhall) {
        this.idhall = idhall;
    }

    @JsonProperty("start")
    public String getStart() {
        return start;
    }

    @JsonProperty("start")
    public void setStart(String start) {
        this.start = start;
    }

    @JsonProperty("end")
    public String getEnd() {
        return end;
    }

    @JsonProperty("end")
    public void setEnd(String end) {
        this.end = end;
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