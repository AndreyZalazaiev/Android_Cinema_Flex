package andrew.cinema.cinema.Entities;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Review {
    private Integer idreview;
    private Integer idfilm;
    private String idaccount;
    private String text;
    private Integer mark;
}
