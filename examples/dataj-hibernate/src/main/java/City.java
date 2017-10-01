import org.dataj.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
@Entity
class City {

    @Id
    @GeneratedValue
    private long id;

    private String name;
}
