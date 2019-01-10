package com.empresa.consumo.masivo.gestion.service;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.empresa.consumo.masivo.gestion.exception.FileStorageException;
import com.empresa.consumo.masivo.gestion.exception.ImageNotFoundException;

@Service
public class UploadService {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Value("${file.upload-dir}")
	String location;

	public static final String FOLDER_USER = "users";
	public static final String FOLDER_GROUP = "groups";
	public static final String FOLDER_SUPER_GROUPS = "supergroups";
	public static final String FOLDER_SERVICE = "service";

	private void createDirectories(Long serviceId, Long id, String subFolder) {

		String address = (subFolder.equalsIgnoreCase(FOLDER_SERVICE) ? serviceId + File.separator + subFolder
				: serviceId + File.separator + subFolder + File.separator + id);

		Path newDirectories = Paths.get(location + File.separator + address).toAbsolutePath().normalize();
		try {
			Files.createDirectories(newDirectories);
		} catch (Exception ex) {
			throw new FileStorageException("Could not create the directory where the uploaded files will be stored.");
		}
	}

	@Caching(evict = { @CacheEvict(value = "imageShort", key = "{#serviceId,#userId}", condition = "#result!=null"),
			@CacheEvict(value = "imageBig", key = "{#serviceId,#userId}", condition = "#result!=null"),
			@CacheEvict(value = "imageMed", key = "{#serviceId,#userId}", condition = "#result!=null") })
	public String uploadFile(MultipartFile file, Long serviceId, Long userId)
			throws IllegalStateException, IOException {

		
		// Normalize file name
		String fileName = StringUtils.cleanPath(file.getOriginalFilename());
		//FileOutputStream fo;
		BufferedImage image = ImageIO.read(file.getInputStream());
		BufferedImage imageShort = resize(image, 50, 50);
		BufferedImage imageMed = resize(image, 128, 128);
		

		try {
			if (fileName.contains("..")) {
				throw new FileStorageException("Sorry! Filename contains invalid path sequence :" + fileName);
			}
			this.createDirectories(serviceId, userId, FOLDER_USER);
			File outputShort = new File(location + File.separator + serviceId + File.separator + FOLDER_USER
					+ File.separator + userId + File.separator + "50x50.png");
			File outputMed = new File(location + File.separator + serviceId + File.separator + FOLDER_USER
					+ File.separator + userId + File.separator + "128x128.png");
			
			ImageIO.write(imageShort, "png", outputShort);
			ImageIO.write(imageMed, "png", outputMed);
			
			log.info("Uploaded image");
			return fileName;
		} catch (IOException ex) {
			log.error("Could not store file " + fileName + ". Please try again!", ex);
			throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
		}
	}

	@Cacheable(value = "imageShort", key = "{#serviceId,#userId}", unless = "#result == null")
	public ResponseEntity<byte[]> downloadShort(Long serviceId, Long userId, HttpServletRequest request,
			HttpServletResponse response)
			throws ImageNotFoundException, IOException {
		String fileName = "50x50.png";
		return this.download(serviceId, userId, fileName, request, response);
	}

	@Cacheable(value = "imageBig", key = "{#serviceId,#userId}", unless = "#result == null")
	public ResponseEntity<byte[]> downloadBig(Long serviceId, Long userId, HttpServletRequest request,
			HttpServletResponse response)
			throws  ImageNotFoundException, IOException {
		String fileName = "500x500.png";
		return this.download(serviceId, userId, fileName, request, response);
	}

	

	@Cacheable(value = "imageMed", key = "{#serviceId,#userId}", unless = "#result == null")
	public ResponseEntity<byte[]> downloadMed(Long serviceId, Long userId, HttpServletRequest request,
			HttpServletResponse response)
			throws ImageNotFoundException, IOException {
		String fileName = "128x128.png";
		return this.download(serviceId, userId, fileName, request, response);
	}

	private ResponseEntity<byte[]> download(Long serviceId, Long userId, String fileName, HttpServletRequest request,
			HttpServletResponse response)
			throws IOException, ImageNotFoundException {

		ServletContext context = request.getSession().getServletContext();
		// String appPath = context.getRealPath("");
		String filePath = location + File.separator + serviceId + File.separator + FOLDER_USER + File.separator + userId
				+ File.separator + fileName;
		String fullPath = filePath;
		File downloadFile = new File(fullPath);
		if (!downloadFile.exists()) {
			log.error("The Image doesn't exist: " + fileName);
			return new ResponseEntity<>(new byte[0], HttpStatus.OK);
			// throw new ImageNotFoundException("The Image doesn't exist: " + fileName);
		}

		Path newFilePath = Paths.get(fullPath);
		byte[] fileContent = Files.readAllBytes(newFilePath);
		HttpHeaders headers = new HttpHeaders();
		headers.setCacheControl(CacheControl.noCache().getHeaderValue());
		headers.setContentLength((int) downloadFile.length());
		String mimeType = context.getMimeType(fullPath);

		if (mimeType == null) {

			mimeType = "application/octet-stream";

		}
		if (mimeType.equals(MediaType.IMAGE_JPEG_VALUE)) {
			headers.setContentType(MediaType.IMAGE_JPEG);
		} else if (mimeType.equals(MediaType.IMAGE_PNG_VALUE)) {
			headers.setContentType(MediaType.IMAGE_PNG);
		} else {
			headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		}

		String headerKey = "Content-Disposition";
		String headerValue = String.format("attachment; filename=\"%s\"", userId);
		headers.set(headerKey, headerValue);
		// headers.setContentDisposition(contentDisposition);
		ResponseEntity<byte[]> responseEntity = new ResponseEntity<>(fileContent, headers, HttpStatus.OK);
		log.info("Downloaded in service");
		return responseEntity;

		
	}

	@Caching(evict = { @CacheEvict(value = "imageShort", key = "{#serviceId,#userId}", condition = "#result!=null"),
			@CacheEvict(value = "imageBig", key = "{#serviceId,#userId}", condition = "#result!=null"),
			@CacheEvict(value = "imageMed", key = "{#serviceId,#userId}", condition = "#result!=null") })
	public Long deleteFile(Long serviceId, Long userId) {

		Long result = 0l;
		Path directory = Paths
				.get(location + File.separator + serviceId + File.separator + FOLDER_USER + File.separator + userId)
				.toAbsolutePath().normalize();
		// Path fileDirectory = directory.resolve(fileName);

		
		try {
			Files.walk(directory).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
			
			result = 1l;
		} catch (Exception ex) {
			log.error("Could not delete the directory or file. " + ex);
			throw new FileStorageException("Could not delete the directory or file.");
		}
		
		return result;
	}

	private BufferedImage resize(BufferedImage img, int height, int width) {
		Image tmp = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
		BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = resized.createGraphics();
		g2d.drawImage(tmp, 0, 0, null);
		g2d.dispose();
		return resized;
	}
	private BufferedImage resize(BufferedImage img, double percent) {
		
		int scaledWidth = (int) (img.getWidth() * percent);
        int scaledHeight = (int) (img.getHeight() * percent);
        return resize(img, scaledWidth, scaledHeight);
		
		
	}
}
