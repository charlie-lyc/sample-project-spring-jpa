package com.ems.mockup.controller;

import com.ems.mockup.domain.SampleDatum;
import com.ems.mockup.service.SampleDatumService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class SampleDatumController {

    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    private final SampleDatumService sampleDatumService;
    private final Logger logger;

    @Autowired
    public SampleDatumController(SampleDatumService sampleDatumService, Logger logger) {
        this.sampleDatumService = sampleDatumService;
        this.logger = logger;
    }

    @GetMapping("/")
    public String hello() {
        return "This is the Seawater EMS API on Spring data JPA.";
    }

    @GetMapping("/mockup/data")
    public ResponseEntity<List<SampleDatum>> getAllSampleDataInJson() {
        List<SampleDatum> sampleData = sampleDatumService.getAllSampleData();
        return new ResponseEntity<>(sampleData, HttpStatus.OK);
    }

    @GetMapping("/mockup/datum")
    public ResponseEntity<SampleDatum> getSampleDatumOnTime(@RequestParam int hour) {
        Optional<SampleDatum> sampleDatum = sampleDatumService.getSampleDatumByHour(hour);
        return sampleDatum.map((datum) -> new ResponseEntity<>(datum, HttpStatus.OK))
                            .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/mockup/download/{filename}")
    public ResponseEntity<InputStreamResource> downloadSampleDataAsCsv(@PathVariable String filename) throws IOException {
        if (!filename.endsWith(".csv")) {
            //return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            return ResponseEntity.status(400).body(null);
        }
        File file = null;
        try {
            file = sampleDatumService.exportSampleDataToCSV(filename);
            FileInputStream fileInputStream = new FileInputStream(file);
            InputStreamResource resource = new InputStreamResource(fileInputStream);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + filename)
                    .contentType(MediaType.parseMediaType("application/csv"))
                    .body(resource);
        } catch (IOException e) {
            //return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            return ResponseEntity.status(500).body(null);
        } finally {
            if (file != null && file.exists()) {
                File finalFile = file;
                executorService.schedule(() -> {
                    boolean deleted = finalFile.delete();
                    if (!deleted) {
                        logger.info("Logger -> Failed to delete file: " + finalFile.getAbsolutePath());
                    }
                }, 3, TimeUnit.SECONDS);
            }
        }
    }

    @PostMapping("/mockup/upload")
    public ResponseEntity<String> uploadSampleDataAsCsv(@RequestPart("file") MultipartFile file) {
        if (file.isEmpty() || !Objects.requireNonNull(file.getOriginalFilename()).endsWith(".csv")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Not CSV file uploaded.");
        }
        try {
            sampleDatumService.importSampleDataFromCSV(file);
            return new ResponseEntity<>("Succeeded in uploading CSV file.", HttpStatus.valueOf(201));
        } catch (IOException e) {
            return new ResponseEntity<>("Failed to upload CSV file.", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

}
