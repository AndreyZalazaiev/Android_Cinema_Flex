package andrew.cinema.cinema.Entities;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Film {
    private int idfilm;
    private String description;
    private String name;
    private String trailer;
    private String image;
    private String ageLimit;
    private String genre;
}
