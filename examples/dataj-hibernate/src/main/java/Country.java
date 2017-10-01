import org.dataj.Data;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;

@Data
@Entity
@Table(name = "countries", schema = "public")
public class Country {

    @Id
    @GeneratedValue
    long id;

    private String name;

    @NaturalId
    private String isoCode;

    @OneToOne(cascade = CascadeType.ALL)
    private CityData capital;
}
