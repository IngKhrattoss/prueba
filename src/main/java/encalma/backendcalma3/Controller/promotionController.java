package encalma.backendcalma3.Controller;


import encalma.backendcalma3.Entity.promotion;
import encalma.backendcalma3.Repository.promotionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/promociones")
// Esta clase es un controlador REST para manejar las promociones
@CrossOrigin(origins = "*")// para que Angular pueda acceder
public class promotionController {

    @Autowired
    private promotionRepository repository;

    @GetMapping
    public List<promotion> obtenerPromociones() {
        return repository.findAll();
    }

    // Opcional: agregar una promoción
    @PostMapping
    public ResponseEntity<?> agregarPromocion(@RequestBody promotion promocion) {
        if (promocion.getTitulo() == null || promocion.getTitulo().trim().isEmpty()
                || promocion.getDescripcion() == null || promocion.getDescripcion().trim().isEmpty()
                || promocion.getImagenUrl() == null || promocion.getImagenUrl().trim().isEmpty()
                || promocion.getPrecio() == null
                || promocion.getFechaInicio() == null
                || promocion.getFechaFin() == null) {
            return ResponseEntity.badRequest().body("Todos los campos son obligatorios.");
        }

        if (promocion.getFechaFin().isBefore(promocion.getFechaInicio())) {
            return ResponseEntity.badRequest().body("La fecha de fin no puede ser anterior a la fecha de inicio.");
        }

        try {
            promotion nueva = repository.save(promocion);
            return ResponseEntity.ok(nueva);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al guardar promoción: " + e.getMessage());
        }
    }



    // Editar una promoción existente
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarPromocion(@PathVariable Long id, @RequestBody promotion promocionActualizada) {
        Optional<promotion> optionalPromocion = repository.findById(id);

        if (!optionalPromocion.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Promoción no encontrada con ID: " + id);
        }

        if (promocionActualizada.getTitulo() == null || promocionActualizada.getTitulo().trim().isEmpty()
                || promocionActualizada.getDescripcion() == null || promocionActualizada.getDescripcion().trim().isEmpty()
                || promocionActualizada.getImagenUrl() == null || promocionActualizada.getImagenUrl().trim().isEmpty()
                || promocionActualizada.getPrecio() == null
                || promocionActualizada.getFechaInicio() == null
                || promocionActualizada.getFechaFin() == null) {
            return ResponseEntity.badRequest().body("Todos los campos son obligatorios.");
        }

        if (promocionActualizada.getFechaFin().isBefore(promocionActualizada.getFechaInicio())) {
            return ResponseEntity.badRequest().body("La fecha de fin no puede ser anterior a la fecha de inicio.");
        }

        promotion promocionExistente = optionalPromocion.get();
        promocionExistente.setTitulo(promocionActualizada.getTitulo());
        promocionExistente.setDescripcion(promocionActualizada.getDescripcion());
        promocionExistente.setImagenUrl(promocionActualizada.getImagenUrl());
        promocionExistente.setPrecio(promocionActualizada.getPrecio());
        promocionExistente.setFechaInicio(promocionActualizada.getFechaInicio());
        promocionExistente.setFechaFin(promocionActualizada.getFechaFin());

        return ResponseEntity.ok(repository.save(promocionExistente));
    }


    // Eliminar una promoción
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarPromocion(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Promoción no encontrada con ID: " + id);
        }

        repository.deleteById(id);
        return ResponseEntity.ok("Promoción eliminada correctamente.");
    }



    @GetMapping("/actuales")
    public List<promotion> obtenerPromocionesActuales(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fecha) {

        // Si no pasan fecha => usa la fecha de “hoy”
        LocalDate hoy = (fecha == null) ? LocalDate.now() : fecha;
        return repository.findByFechaInicioLessThanEqualAndFechaFinGreaterThanEqual(hoy, hoy);
    }


}
