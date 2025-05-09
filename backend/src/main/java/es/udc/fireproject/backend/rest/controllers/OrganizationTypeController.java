package es.udc.fireproject.backend.rest.controllers;

import es.udc.fireproject.backend.model.entities.organization.OrganizationType;
import es.udc.fireproject.backend.model.exceptions.InstanceNotFoundException;
import es.udc.fireproject.backend.model.services.personalmanagement.PersonalManagementService;
import es.udc.fireproject.backend.rest.dtos.OrganizationTypeDto;
import es.udc.fireproject.backend.rest.dtos.UserDto;
import es.udc.fireproject.backend.rest.dtos.conversors.OrganizationTypeConversor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/organizationTypes")
public class OrganizationTypeController {

    @Autowired
    private PersonalManagementService personalManagementService;

    @GetMapping("")
    public List<OrganizationTypeDto> findAllOrganizationTypes(@RequestAttribute Long userId) {

        List<OrganizationTypeDto> organizationTypeDtos = new ArrayList<>();
        for (OrganizationType organizationType : personalManagementService.findAllOrganizationTypes()) {
            organizationTypeDtos.add(OrganizationTypeConversor.toOrganizationTypeDto(organizationType));
        }
        return organizationTypeDtos;
    }

    @GetMapping("/{id}")
    public OrganizationTypeDto findAllOrganizationTypes(@RequestAttribute Long userId, @PathVariable Long id)
            throws InstanceNotFoundException {
        OrganizationType organizationType = personalManagementService.findOrganizationTypeById(id);
        return OrganizationTypeConversor.toOrganizationTypeDto(organizationType);

    }

    @PostMapping("")
    public OrganizationTypeDto create(@RequestAttribute Long userId,
                                      @Validated({UserDto.AllValidations.class})
                                      @RequestBody OrganizationTypeDto organizationTypeDto) {

        OrganizationType organizationType = personalManagementService.createOrganizationType(organizationTypeDto.getName());

        return OrganizationTypeConversor.toOrganizationTypeDto(organizationType);
    }


}
