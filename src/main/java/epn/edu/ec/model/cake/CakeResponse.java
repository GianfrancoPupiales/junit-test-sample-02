package epn.edu.ec.model.cake;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CakeResponse {
    private long id;
    private String title;
    private String description;
}
