package com.opd.patient;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/patients")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:8081"})
public class PatientController {
    private final PatientRepository patients;

    public PatientController(PatientRepository patients) {
        this.patients = patients;
    }

    @GetMapping
    public List<Patient> list(@RequestParam(defaultValue = "") String search) {
        return patients.search(search.trim());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Patient create(@Valid @RequestBody PatientRequest request) {
        return patients.save(new Patient(
                request.name().trim(), request.gender(), request.age(), request.phoneNumber().trim()));
    }
}
