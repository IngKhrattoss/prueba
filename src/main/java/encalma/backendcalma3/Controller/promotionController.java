package encalma.backendcalma3.Controller;


import encalma.backendcalma3.Entity.promotion;
import encalma.backendcalma3.Repository.promotionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

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
    public promotion agregarPromocion(@RequestBody promotion promocion) {
        return repository.save(promocion);
    }


}
