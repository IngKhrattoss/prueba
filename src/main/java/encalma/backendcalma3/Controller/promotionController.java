package encalma.backendcalma3.Controller;


import encalma.backendcalma3.DTO.ApiResponse;
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
    public ResponseEntity<?> obtenerPromociones() {
        List<promotion> promociones = repository.findAll();
        return ResponseEntity.ok(new ApiResponse<>("success", "Lista de promociones", promociones));
    }


    // Opcional: agregar una promoción
    @PostMapping
    public ResponseEntity<?> agregarPromocion(@RequestBody promotion promocion) {
        if (camposInvalidos(promocion)) {
            return ResponseEntity.badRequest().body(new ApiResponse<>( "error", "Todos los campos son obligatorios.", null));
        }

        if (promocion.getFechaFin().isBefore(promocion.getFechaInicio())) {
            return ResponseEntity.badRequest().body(new ApiResponse<>( "error", "La fecha de fin no puede ser anterior a la fecha de inicio.", null));
        }

        try {
            promotion nueva = repository.save(promocion);
            return ResponseEntity.ok(new ApiResponse<>( "success", "Promoción guardada exitosamente.", nueva));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>( "error", "Error al guardar promoción: " + e.getMessage(), null));
        }
    }



    // Editar una promoción existente
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarPromocion(@PathVariable Long id, @RequestBody promotion promocionActualizada) {
        Optional<promotion> optionalPromocion = repository.findById(id);

        if (!optionalPromocion.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>( "error", "Promoción no encontrada con ID: " + id, null));
        }

        if (camposInvalidos(promocionActualizada)) {
            return ResponseEntity.badRequest().body(new ApiResponse<>( "error", "Todos los campos son obligatorios.", null));
        }

        if (promocionActualizada.getFechaFin().isBefore(promocionActualizada.getFechaInicio())) {
            return ResponseEntity.badRequest().body(new ApiResponse<>( "error", "La fecha de fin no puede ser anterior a la fecha de inicio.", null));
        }

        promotion promocionExistente = optionalPromocion.get();
        promocionExistente.setTitulo(promocionActualizada.getTitulo());
        promocionExistente.setDescripcion(promocionActualizada.getDescripcion());
        promocionExistente.setImagenUrl(promocionActualizada.getImagenUrl());
        promocionExistente.setPrecio(promocionActualizada.getPrecio());
        promocionExistente.setFechaInicio(promocionActualizada.getFechaInicio());
        promocionExistente.setFechaFin(promocionActualizada.getFechaFin());

        return ResponseEntity.ok(new ApiResponse<>("success", "Promoción actualizada correctamente.", repository.save(promocionExistente)));
    }


    // Eliminar una promoción
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarPromocion(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>( "error", "Promoción no encontrada con ID: " + id, null));
        }

        repository.deleteById(id);
        return ResponseEntity.ok(new ApiResponse<>("success", "Promoción eliminada correctamente.", null));
    }



    @GetMapping("/actuales")
    public ResponseEntity<?> obtenerPromocionesActuales(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fecha) {
        LocalDate hoy = (fecha == null) ? LocalDate.now() : fecha;
        List<promotion> actuales = repository.findByFechaInicioLessThanEqualAndFechaFinGreaterThanEqual(hoy, hoy);
        return ResponseEntity.ok(new ApiResponse<>("success", "Promociones vigentes", actuales));
    }



    private boolean camposInvalidos(promotion p) {
        return p.getTitulo() == null || p.getTitulo().trim().isEmpty()
                || p.getDescripcion() == null || p.getDescripcion().trim().isEmpty()
                || p.getImagenUrl() == null || p.getImagenUrl().trim().isEmpty()
                || p.getPrecio() == null
                || p.getFechaInicio() == null
                || p.getFechaFin() == null;
    }


}
