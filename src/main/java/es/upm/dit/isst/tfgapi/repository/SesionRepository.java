package es.upm.dit.isst.tfgapi.repository;

import org.springframework.data.repository.CrudRepository;

import es.upm.dit.isst.tfgapi.model.Sesion;

public interface SesionRepository extends CrudRepository<Sesion, Long> {
    // Métodos de consulta personalizados pueden ser definidos aquí
    // Por ejemplo, para encontrar sesiones por año o por estado
    
}
