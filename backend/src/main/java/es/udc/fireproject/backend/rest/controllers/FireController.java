package es.udc.fireproject.backend.rest.controllers;

import es.udc.fireproject.backend.model.entities.fire.Fire;
import es.udc.fireproject.backend.model.exceptions.AlreadyDismantledException;
import es.udc.fireproject.backend.model.exceptions.ExtinguishedFireException;
import es.udc.fireproject.backend.model.exceptions.InstanceNotFoundException;
import es.udc.fireproject.backend.model.services.firemanagement.FireManagementService;
import es.udc.fireproject.backend.rest.common.ErrorsDto;
import es.udc.fireproject.backend.rest.dtos.FireDto;
import es.udc.fireproject.backend.rest.dtos.conversors.FireConversor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


@RestController
@RequestMapping("/fires")
public class FireController {


    private static final String EXTINGUISHED_FIRE_EXCEPTION_CODE = "project.exceptions.ExtinguishedFireException";
    @Autowired
    private MessageSource messageSource;

    @ExceptionHandler(ExtinguishedFireException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorsDto handleExtinguishedFireException(ExtinguishedFireException exception, Locale locale) {

        String errorMessage = messageSource.getMessage(EXTINGUISHED_FIRE_EXCEPTION_CODE,
                new Object[]{exception.getId()}, EXTINGUISHED_FIRE_EXCEPTION_CODE, locale);

        return new ErrorsDto(errorMessage);
    }

    @Autowired
    FireManagementService fireManagementService;

    @PostMapping("")
    public ResponseEntity<FireDto> createFire(@RequestAttribute Long userId, @RequestBody FireDto fireDto) {
        Fire fire = FireConversor.toFire(fireDto);

        fire = fireManagementService.createFire(fire.getDescription(), fire.getType(), fire.getFireIndex());

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(fire.getId()).toUri();

        return ResponseEntity.created(location).body(FireConversor.toFireDto(fire));

    }

    @GetMapping("")
    public List<FireDto> findAllFires() {

        List<FireDto> fireDtos = new ArrayList<>();

        for (Fire fire : fireManagementService.findAllFires()) {
            fireDtos.add(FireConversor.toFireDto(fire));
        }
        return fireDtos;
    }

    @GetMapping("/{id}")
    public FireDto findFireById(@RequestAttribute Long userId, @PathVariable Long id)
            throws InstanceNotFoundException {
        return FireConversor.toFireDto(fireManagementService.findFireById(id));
    }

    @PostMapping("/{id}/extinguishFire")
    public FireDto extinguishFire(@RequestAttribute Long userId,
                                  @PathVariable Long id)

            throws InstanceNotFoundException, ExtinguishedFireException, AlreadyDismantledException {
        return FireConversor.toFireDto(fireManagementService.extinguishFire(id));
    }

    @PostMapping("/{id}/extinguishQuadrant")
    public FireDto extinguishFire(@RequestAttribute Long userId,
                                  @PathVariable Long id,
                                  @RequestParam(value = "quadrantId", required = true) Integer quadrantId)
            throws InstanceNotFoundException, ExtinguishedFireException, AlreadyDismantledException {
        return FireConversor.toFireDto(fireManagementService.extinguishQuadrantByFireId(id, quadrantId));
    }

    @PutMapping("/{id}")
    public FireDto updateFire(@RequestAttribute Long userId, @PathVariable Long id, @RequestBody FireDto fireDto)
            throws InstanceNotFoundException, ExtinguishedFireException {
        Fire fire = FireConversor.toFire(fireDto);
        return FireConversor.toFireDto(fireManagementService.updateFire(id, fire.getDescription(), fire.getType(), fire.getFireIndex()));
    }


}
