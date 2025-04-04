package es.upm.dit.isst.tfgapi.controller;

import es.upm.dit.isst.tfgapi.model.*;
import es.upm.dit.isst.tfgapi.repository.*;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.slf4j.*;
import org.springframework.core.io.ByteArrayResource;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;

import jakarta.transaction.Transactional;

@RestController
@RequestMapping("/myApi")
public class TFGController {

    private final TFGRepository tfgRepository; // Repositorio para acceder a los TFGs.
    private final SesionRepository sesionRepository; // Repositorio para acceder a las sesiones de defensa.

    public static final Logger log = LoggerFactory.getLogger(TFGController.class);

    public TFGController(TFGRepository t, SesionRepository s) {
        this.tfgRepository = t;
        this.sesionRepository = s;
    }

    /**
     * Maneja tanto el caso en el que se pida el listado de todos TFGs
     * como el caso en el que se piden solo los TFGs de un tutor concreto.
     * 
     * @param tutor email del tutor (opcional)
     * @return lista de TFGs
     */

    // @CrossOrigin(origins = "http://localhost:8080")

    @GetMapping("/tfgs")
    List<TFG> readAll(@RequestParam(name = "tutor", required = false) String tutor) {

        // No me habría hecho falta especificar el nombre del parámetro,
        // porque coincide con el del método
        if (tutor != null && !tutor.isEmpty()) {
            return (List<TFG>) tfgRepository.findByTutor(tutor);
            // La lista puede estar vacía, pero eso no es un error
        } else {
            return (List<TFG>) tfgRepository.findAll();
        }
    }

    @PostMapping("/tfgs")
    ResponseEntity<TFG> create(@RequestBody TFG newTFG) throws URISyntaxException {
        // Devolver código de error si el TFG ya existe
        // En este caso, el ID del TFG es el email del alumno (ver modelo de TFG).
        if (tfgRepository.findById(newTFG.getAlumno()).isPresent()) {
            return new ResponseEntity<TFG>(HttpStatus.CONFLICT);
        }

        // Comprobar que el alumno satisface los
        // criterios económicos (matrícula)
        // y académicos (créditos aprobados, etc.)

        // Para hacer esto habría que manejar una entidad alumno con atributos como
        // matrícula,
        // créditos aprobados, etc. y comprobar que el alumno tiene derecho a solicitar
        // TFG.
        // En este caso, como no tenemos la entidad alumno, lo que hacemos es comprobar
        // que el email del alumno tiene el formato correcto. (Trabajamos con el email
        // del alumno nada más)

        if (!newTFG.getAlumno().endsWith("@alumnos.upm.es")) {
            log.error("El email del alumno no es correcto: " + newTFG.getAlumno());
            return ResponseEntity.badRequest().body(null);
        }

        // Comprobar que el tutor es profesor adscrito a la ETSIT
        // realmente comprobamos que sea de la UPM (email de la UPM, por la misma razón
        // que antes)

        if (!newTFG.getTutor().endsWith("@upm.es")) {
            log.error("El email del tutor no es correcto: " + newTFG.getTutor());
            return ResponseEntity.badRequest().body(null);
        }

        // Guardar el nuevo TFG en la base de datos.
        TFG result = tfgRepository.save(newTFG);
        log.info("Nuevo TFG creado para alumno: " + result.getAlumno());

        // TODO enviar notificación por e-mail al tutor
        // Enviar notificación por e-mail al tutor
        // En este caso no se envía el email (simplemente lo simulamos),
        // pero se podría hacer con un servicio de correo

        return ResponseEntity.created(new URI("/tfgs/" + result.getAlumno())).body(result);
    }

    @GetMapping("/tfgs/{id}")   
    ResponseEntity<TFG> readOne(@PathVariable String id) {
        return tfgRepository.findById(id).map(tfg -> ResponseEntity.ok().body(tfg))
                .orElse(new ResponseEntity<TFG>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/tfgs/{id}")
    ResponseEntity<TFG> update(@RequestBody TFG newTFG, @PathVariable String id) {
        return tfgRepository.findById(id).map(tfg -> {
            tfg.setAlumno(newTFG.getAlumno()); // En realidad nunca debería modificarse, producirá error 500
            tfg.setTutor(newTFG.getTutor());
            tfg.setTitulo(newTFG.getTitulo());
            tfg.setResumen(newTFG.getResumen());
            tfg.setEstado(newTFG.getEstado());
            tfg.setMemoria(newTFG.getMemoria());
            tfg.setCalificacion(newTFG.getCalificacion());
            tfg.setMatriculaHonor(newTFG.getMatriculaHonor());
            tfg.setSesion(newTFG.getSesion());
            tfgRepository.save(tfg);
            return ResponseEntity.ok().body(tfg);
        }).orElse(new ResponseEntity<TFG>(HttpStatus.NOT_FOUND));
    }

    @PatchMapping("/tfgs/{id}")
    ResponseEntity<TFG> partialUpdate(@RequestBody TFG newTFG, @PathVariable String id) {
        return tfgRepository.findById(id).map(tfg -> {
            if (newTFG.getAlumno() != null) {
                tfg.setAlumno(newTFG.getAlumno()); // En realidad nunca debería modificarse, producirá error 500
            }
            if (newTFG.getTutor() != null) {
                tfg.setTutor(newTFG.getTutor());
            }
            if (newTFG.getTitulo() != null) {
                tfg.setTitulo(newTFG.getTitulo());
            }
            if (newTFG.getResumen() != null) {
                tfg.setResumen(newTFG.getResumen());
            }
            if (newTFG.getEstado() != null) {
                tfg.setEstado(newTFG.getEstado());
            }
            if (newTFG.getMemoria() != null) {
                tfg.setMemoria(newTFG.getMemoria());
            }
            if (newTFG.getCalificacion() != null) {
                tfg.setCalificacion(newTFG.getCalificacion());
            }
            if (newTFG.getMatriculaHonor() != null) {
                tfg.setMatriculaHonor(newTFG.getMatriculaHonor());
            }
            if (newTFG.getSesion() != null) {
                tfg.setSesion(newTFG.getSesion());
            }
            tfgRepository.save(tfg);
            return ResponseEntity.ok().body(tfg);
        }).orElse(new ResponseEntity<TFG>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/tfgs/{id}")
    ResponseEntity<TFG> delete(@PathVariable String id) {
        tfgRepository.deleteById(id);
        return ResponseEntity.ok().body(null);
        // return new ResponseEntity<TFG>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/tfgs/{id}/estado/{estado}")
    @Transactional // Para evitar problemas de concurrencia al actualizar el estado.
    public ResponseEntity<?> actualizaEstado(@PathVariable String id, @PathVariable Estado estado) {
        // Spring ya devuelve un error 400 Bad Request si el estado no es válido
        return tfgRepository.findById(id).map(tfg -> {
            if (!tfg.getEstado().canTransitionTo(estado)) {
                return ResponseEntity.badRequest().body(
                        "No se puede pasar del estado " + tfg.getEstado() + " a " + estado);
            }
            /*
             * Comprobar que se cumplen otros requisitos
             * para poder cambiar de estado,
             * p. ej. no se puede avanzar a solicitada defensa
             * si no se ha subido la memoria,
             * se tienen que cumplir requisitos académicos
             * para avanzar a solicitada defensa,
             * no se puede avanzar a programada defensa
             * si no se ha asignado sesión,
             * no se puede avanzar a calificado
             * si no se ha llegado la fecha de la sesión de defensa,
             * etc.
             */
            if (estado == Estado.SOLICITADADEFENSA && tfg.getMemoria() == null) {
                return ResponseEntity.badRequest().body("No se puede solicitar defensa sin memoria subida.");
            }
            if (estado == Estado.PROGRAMADADEFENSA && tfg.getSesion() == null) {
                return ResponseEntity.badRequest().body("No se puede aceptar defensa si no hay sesión asignada.");
            }
            if (estado == Estado.CALIFICADO) {
                if (tfg.getSesion() == null || tfg.getCalificacion() == null) {
                    return ResponseEntity.badRequest()
                            .body("No se puede calificar sin sesión ni calificación asignadas.");
                }
                if (tfg.getSesion().getFecha().after(new Date())) {
                    return ResponseEntity.badRequest().body("La sesión de defensa aún no ha ocurrido.");
                }
            }

            tfg.setEstado(estado);
            tfgRepository.save(tfg);

            // Notificar a quien corresponda de los cambios de estado
            log.info("El TFG del alumno {} ha cambiado de estado a {}", tfg.getAlumno(), estado);
            return ResponseEntity.ok().body(tfg);
        }).orElse(ResponseEntity.notFound().build());
    }

    // ... métodos del controlador para el document de la memoria y la sesión

    @PutMapping(value = "/tfgs/{id}/memoria", consumes = "application/pdf")
    @io.swagger.v3.oas.annotations.Operation(requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = {
            @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/pdf", schema = @io.swagger.v3.oas.annotations.media.Schema(type = "string", format = "binary"))
    }))
    public ResponseEntity<?> subeMemoria(@PathVariable String id, @RequestBody byte[] fileContent) {
        return tfgRepository.findById(id).map(tfg -> {
            tfg.setMemoria(fileContent);
            tfgRepository.save(tfg);
            return ResponseEntity.ok("Documento subido correctamente");
        }).orElseThrow(
                // Similar to orElse(ResponseEntity.notFound().build());
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "TFG no encontrado"));
    }

    @GetMapping(value = "/tfgs/{id}/memoria", produces = "application/pdf")
    public ResponseEntity<?> descargaMemmoria(@PathVariable String id) {
        TFG tfg = tfgRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "TFG no encontrado"));
        if (tfg.getMemoria() == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"tfg_document_" + id + ".pdf" + "\"")
                .body(new ByteArrayResource(tfg.getMemoria()));
    }

    @PostMapping("/sesiones")
    ResponseEntity<Sesion> createSesion(@RequestBody Sesion newSesion) throws URISyntaxException {
        // No deberíamos recibir ID en el body, pero tampoco lo comprobaremos
        // No consideramos aquí el caso en el que recibamos TFGs,
        // sino que se asignan después
        Sesion result = sesionRepository.save(newSesion);
        return ResponseEntity.created(new URI("/sesiones/" + result.getId())).body(result);
    }

    @PostMapping("/sesiones/{id}/tfgs")
    ResponseEntity<?> asignaTFG(@PathVariable Long id,
            @RequestBody String alumno) {
        return sesionRepository.findById(id).map(sesion -> {
            TFG tfg = tfgRepository.findById(alumno).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "TFG no encontrado"));
            tfg.setSesion(sesion);
            tfgRepository.save(tfg);
            return ResponseEntity.ok().body(tfg);
        }).orElse(ResponseEntity.notFound().build());
    }
}