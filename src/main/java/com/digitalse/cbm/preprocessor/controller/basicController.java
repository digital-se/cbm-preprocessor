package com.digitalse.cbm.preprocessor.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.NoSuchElementException;

import javax.imageio.ImageIO;

import com.digitalse.cbm.preprocessor.service.PreprocessService;
import com.digitalse.cbm.preprocessor.service.OpenCvService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping
public class basicController {

    @Autowired
    private PreprocessService preprocessService;

    @Autowired
    private OpenCvService openCvService;

    @PostMapping("/preprocessor/teste")
    public ResponseEntity<InputStreamResource> receiveImage1(@RequestPart(required = true) MultipartFile file)
            throws IOException {
        try {
            var image = openCvService.imageRead(file.getBytes());
            var result = preprocessService.preProcess(image, 3, 15, 2);

            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(result, "png", os);

            return ResponseEntity.status(HttpStatus.OK).contentLength(os.size())
            .contentType(org.springframework.http.MediaType.parseMediaType("image/png")) .body(new InputStreamResource(new ByteArrayInputStream(os.toByteArray())));
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getLocalizedMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getLocalizedMessage());
        }
    }

}
