package andrew.cinema.cinema.Entities;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Film {
    int idfilm;
    String description;
    String name;
    String trailer;
    String image;
    String ageLimit;
    String genre;
}
