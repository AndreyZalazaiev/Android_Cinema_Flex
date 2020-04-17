package andrew.cinema.cinema.Entities;

import org.androidannotations.annotations.sharedpreferences.DefaultString;

import java.sql.Date;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Account {
    private String idaccount;
    private String name;
    private String picture;
    private String email;
    private Integer bonus;
    private String doB;

}
