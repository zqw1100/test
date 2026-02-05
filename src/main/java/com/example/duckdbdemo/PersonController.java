package com.example.duckdbdemo;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/people")
public class PersonController {

    private final DuckdbPersonRepository repository;

    public PersonController(DuckdbPersonRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Person> list() throws Exception {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Person> get(@PathVariable long id) throws Exception {
        Person person = repository.findById(id);
        if (person == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(person);
    }

    @PostMapping
    public ResponseEntity<Person> create(@RequestBody PersonRequest request) throws Exception {
        Person created = repository.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Person> update(@PathVariable long id, @RequestBody PersonRequest request) throws Exception {
        Person updated = repository.update(id, request);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) throws Exception {
        boolean deleted = repository.delete(id);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}
