package de.hskl.rateme.service;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import de.hskl.rateme.dao.ImageDao;
import de.hskl.rateme.entity.Image;

@Service
public class ImageService {

    private static final int MAX_IMAGE_SIZE_BYTES = 5 * 1024 * 1024;

    private final ImageDao imageDao;

    public ImageService(ImageDao imageDao) {
        this.imageDao = imageDao;
    }

    public Image saveImage(byte[] imageBytes) {
        validateImageBytes(imageBytes);
        return imageDao.create(new Image(imageBytes));
    }

    public Image saveUploadedImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        try {
            return saveImage(file.getBytes());
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Image could not be read", e);
        }
    }

    public byte[] getImageBytes(Integer id) {
        Image image = imageDao.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Image not found"));

        return image.getImg();
    }

    public void deleteImage(Image image) {
        if (image != null) {
            imageDao.delete(image);
        }
    }

    private void validateImageBytes(byte[] imageBytes) {
        if (imageBytes == null || imageBytes.length == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Image must not be empty");
        }

        if (imageBytes.length > MAX_IMAGE_SIZE_BYTES) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Image must not be larger than 5 MB");
        }
    }
}
