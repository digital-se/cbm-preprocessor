package com.digitalse.cbm.preprocessor.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.digitalse.cbm.preprocessor.enumerator.BufferedImageType;
import com.digitalse.cbm.rabbitmqtransferenceobjects.RTOBucket;

import org.opencv.core.Mat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PreprocessService {

    @Autowired
    private OpenCvService openCvService;

    public RTOBucket preProcess(RTOBucket rtoBucket) throws IOException {
        Mat image              = openCvService.imageRead(rtoBucket.getDados());
        int gaussianBlurSize   = 5; 
        int thresholdBlockSize = 15;
        double thresholdC      = 5;

        BufferedImage imageResult = preProcess(image, gaussianBlurSize, thresholdBlockSize, thresholdC);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(imageResult, "png", baos);
        byte[] bytes = baos.toByteArray();
        rtoBucket.setDados_processados(bytes);

        return rtoBucket;
    }

    public BufferedImage preProcess(Mat image, int gaussianBlurSize, int thresholdBlockSize, double thresholdC) {
        var gray         = openCvService.grayScale(image);
        var gaussian     = openCvService.gaussianBlur(gray, gaussianBlurSize);
        var matThreshold = openCvService.adaptiveThreshold(gaussian, thresholdBlockSize, thresholdC);
        var imageResult  = openCvService.bufferedImage(matThreshold, BufferedImageType.GRAY);
        
        return imageResult;
    }

    /*
     * public String recognizePlate(Mat image) {
     * 
     * var resized = openCvService.resize(image, 620, 480); var gray =
     * openCvService.grayScale(resized); var filter =
     * openCvService.bilateralFilter(gray, 13, 15, 15); var canny =
     * openCvService.canny(filter, 30, 200);
     * 
     * var plate = openCvService.findContours(canny).stream()
     * .sorted(openCvService::compare) .filter(openCvService::isRectangle).limit(10)
     * .map(openCvService::contour2Rect) .findFirst();
     * 
     * if (plate.isPresent()) { return recognize(resized.submat(plate.get())); }
     * 
     * return ""; }
     */

    /*
     * public String recognize(Mat image) {
     * 
     * var processedImage = preProcess(image, 3, 15, 2); var bufferedImage =
     * openCvService.bufferedImage(processedImage, BufferedImageType.GRAY);
     * 
     * return tesseractService.recognize(bufferedImage); }
     */

    /*
     * public BufferedImage recognize(Mat image) {
     * 
     * var processedImage = preProcess(image, 3, 15, 2);
     * System.out.println(processedImage.toString()); var bufferedImage =
     * openCvService.bufferedImage(processedImage, BufferedImageType.GRAY);
     * 
     * return bufferedImage; }
     */

    /*
     * public List<String> recognizeParagraphs(Mat image) {
     * 
     * var processedImage = preProcess(image, 67, 255, 2); var contours =
     * openCvService.findContours(processedImage);
     * 
     * return contours.stream() .map(contour -> recognize(contour, image))
     * .filter(StringUtils::hasText) .collect(Collectors.toList()); }
     */

    /*
     * private String recognize(MatOfPoint contour, Mat image) {
     * 
     * var rect = openCvService.contour2Rect(contour); var region =
     * image.submat(rect); var processedImage = preProcess(region, 3, 15, 2); var
     * recoginizedText = recognize(processedImage);
     * 
     * return recoginizedText .replace("\n", "") .replace("\r", ""); }
     */

}
