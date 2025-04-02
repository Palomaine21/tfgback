package es.upm.dit.isst.tfgapi.model;

import java.sql.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

@Entity
public class Sesion {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Future
    private Date fecha;
    private String lugar;
    @Size(min = 3, max = 3)
    private List<@Email @NotEmpty String> tribunal;
    @JsonIgnore
    @OneToMany(mappedBy = "sesion")
    private List<@Valid TFG> tfgs;

    // Cosntructor:
    // Se utiliza para crear un objeto Sesion con todos sus atributos inicializados.
    public Sesion(Long id, @Future Date fecha, String lugar,
            @Size(min = 3, max = 3) List<@Email @NotEmpty String> tribunal, List<@Valid TFG> tfgs) {
        this.id = id;
        this.fecha = fecha;
        this.lugar = lugar;
        this.tribunal = tribunal;
        this.tfgs = tfgs;
    }

    // Constructor vacío:
    // Se utiliza para crear un objeto Sesion sin inicializar sus atributos.
    public Sesion() {
        this.id = null;
        this.fecha = null;
        this.lugar = null;
        this.tribunal = null;
        this.tfgs = null;
    }

    // Getters y Setters:
    // Se utilizan para acceder y modificar los atributos de la clase Sesion.
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getLugar() {
        return lugar;
    }

    public void setLugar(String lugar) {
        this.lugar = lugar;
    }

    public List<String> getTribunal() {
        return tribunal;
    }

    public void setTribunal(List<String> tribunal) {
        this.tribunal = tribunal;
    }

    public List<TFG> getTfgs() {
        return tfgs;
    }

    @JsonGetter("tfgs")
    public String[] getEmailsTfgs() {
        if (tfgs != null) {
            return tfgs.stream().map(TFG::getAlumno).toArray(String[]::new);
        } else {
            return new String[0];
        }
    }

    @JsonProperty("tfgs")
    public void setTfgs(List<TFG> tfgs) {
        this.tfgs = tfgs;
    }

    // HashCode y Equals:
    // Se utilizan para comparar objetos de la clase Sesion y para generar un código
    // hash único para cada objeto.
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((fecha == null) ? 0 : fecha.hashCode());
        result = prime * result + ((lugar == null) ? 0 : lugar.hashCode());
        result = prime * result + ((tribunal == null) ? 0 : tribunal.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Sesion other = (Sesion) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (fecha == null) {
            if (other.fecha != null)
                return false;
        } else if (!fecha.equals(other.fecha))
            return false;
        if (lugar == null) {
            if (other.lugar != null)
                return false;
        } else if (!lugar.equals(other.lugar))
            return false;
        if (tribunal == null) {
            if (other.tribunal != null)
                return false;
        } else if (!tribunal.equals(other.tribunal))
            return false;
        return true;
    }

    // toString:
    // Se utiliza para representar un objeto de la clase Sesion como una cadena de
    // texto.
    @Override
    public String toString() {
        return "Sesion [id=" + id + ", fecha=" + fecha + ", lugar=" + lugar + ", tribunal=" + tribunal + "]";
    }
}
