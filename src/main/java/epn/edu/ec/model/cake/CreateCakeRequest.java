package epn.edu.ec.model.cake;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateCakeRequest {
    private String title;
    private String description;
}
