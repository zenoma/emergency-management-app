package es.udc.fireproject.backend.rest.controllers;

import es.udc.fireproject.backend.model.entities.quadrant.Quadrant;
import es.udc.fireproject.backend.model.exceptions.InstanceNotFoundException;
import es.udc.fireproject.backend.model.services.firemanagement.FireManagementService;
import es.udc.fireproject.backend.rest.dtos.QuadrantDto;
import es.udc.fireproject.backend.rest.dtos.QuadrantInfoDto;
import es.udc.fireproject.backend.rest.dtos.conversors.QuadrantConversor;
import es.udc.fireproject.backend.rest.dtos.conversors.QuadrantInfoConversor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/quadrants")
public class QuadrantController {

    @Autowired
    FireManagementService fireManagementService;

    @GetMapping("")
    public List<QuadrantDto> findAll(@RequestParam(required = false) String scale) {

        List<QuadrantDto> quadrantDtos = new ArrayList<>();

        if (scale != null) {
            for (Quadrant quadrant : fireManagementService.findQuadrantsByEscala(scale)) {
                quadrantDtos.add(QuadrantConversor.toQuadrantDto(quadrant));
            }
        } else {
            for (Quadrant quadrant : fireManagementService.findAllQuadrants()) {
                quadrantDtos.add(QuadrantConversor.toQuadrantDto(quadrant));
            }
        }
        return quadrantDtos;
    }

    @GetMapping("/{gid}")
    public QuadrantInfoDto findQuadrantById(@PathVariable Integer gid)
            throws InstanceNotFoundException {
        return QuadrantInfoConversor.toQuadrantDto(fireManagementService.findQuadrantById(gid));
    }


    @GetMapping("/active")
    public List<QuadrantDto> findQuadrantsWithActiveFire() {
        List<QuadrantDto> quadrantDtos = new ArrayList<>();

        for (Quadrant quadrant : fireManagementService.findQuadrantsWithActiveFire()) {
            quadrantDtos.add(QuadrantConversor.toQuadrantDto(quadrant));
        }

        return quadrantDtos;
    }

    @PostMapping("/{gid}/linkFire")
    public void linkFire(@RequestAttribute Long userId, @PathVariable Integer gid, @RequestBody Map<String, String> jsonParams)
            throws InstanceNotFoundException {

        fireManagementService.linkFire(gid, Long.valueOf(jsonParams.get("fireId")));
    }


}
