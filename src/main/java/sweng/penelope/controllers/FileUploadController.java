package sweng.penelope.controllers;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import sweng.penelope.services.StorageService;

/**
 * <code>FileUploadController</code> handles all upload endpoints.
 */
@Controller
@RequestMapping(path = "/api/file")
@Api(tags = "File upload operations")
@ApiImplicitParams({
        @ApiImplicitParam(paramType = "header", name = "IDENTITY", required = true),
        @ApiImplicitParam(paramType = "header", name = "KEY", required = true)
})
public class FileUploadController {
    @Autowired
    private StorageService storageService;
    @Autowired
    private CacheManager cacheManager;

    /**
     * Transforms an input image into a rounded png.
     * 
     * @param file     The input image.
     * @param campusId The campus id the resource belongs to.
     * @param fileName The file name.
     * @return {@link ResponseEntity}
     */
    private ResponseEntity<String> processImage(MultipartFile file, String campusId, String fileName) {
        try {
            // Load input file
            BufferedImage bufferedImage = ImageIO.read(file.getInputStream());

            int width = bufferedImage.getWidth();
            int height = bufferedImage.getHeight();

            // To get a circle, we want to use the short side of the image as a measure to
            // crop.
            int size = Math.min(width, height);

            // ARGB for transparency
            BufferedImage outputImage = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
            Graphics2D graphics2d = outputImage.createGraphics();

            // Draw circle image
            graphics2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics2d.fillOval(0, 0, size, size);
            graphics2d.setComposite(AlphaComposite.SrcIn);
            graphics2d.drawImage(bufferedImage, 0, 0, null);

            String processedFileName = fileName.split("\\.")[0] + "_processed.png";

            if (storageService.storeProcessedImage(processedFileName, campusId, outputImage))
                return ResponseEntity.ok(String.format("image/%s/%s", campusId, processedFileName));
        } catch (IOException e) {
            e.printStackTrace();

            return ResponseEntity.internalServerError().body("Could not process image file");
        }
        return ResponseEntity.ok("null");
    }

    @PostMapping("{campusId}/new")
    @ApiOperation("Stores the uploaded file")
    public ResponseEntity<String> handleFileUpload(@RequestParam MultipartFile file,
            @RequestParam String type,
            @RequestParam(required = false) boolean process, @PathVariable Long campusId) {
        if ((type.equals("audio") || type.equals("video") || type.equals("image")) && file != null) {
            String originalfileName = file.getOriginalFilename();
            if (originalfileName != null && !originalfileName.contains("..")) {
                String fileName = Paths.get(originalfileName).getFileName().toString();

                CacheUtils.evictCache(cacheManager, CacheUtils.ASSETS, fileName);

                if (StringUtils.countOccurrencesOf(fileName, ".") > 1)
                    return ResponseEntity.badRequest().body("File name connot contain dots");
                if (type.equals("image") && process)
                    return processImage(file, campusId.toString(), fileName);
                else if (storageService.store(type, campusId.toString(), file))
                    return ResponseEntity.ok().body(String.format("%s/%s/%s", type, campusId.toString(), fileName));
                else
                    return ResponseEntity.internalServerError().body("Could not store file.");
            }
            return ResponseEntity.badRequest().body("File name cannot contain \"..\" and cannot be null");
        }

        return ResponseEntity.badRequest().body("File type is not supported");
    }
}
