package com.empresa.consumo.masivo.gestion.service;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
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
	public static final String FOLDER_PRODUCTO = "productos";
	public static final String FOLDER_MATERIAL = "materiales";

	private void createDirectories(Long id, String subFolder) {

		String address = subFolder + File.separator + id;

		Path newDirectories = Paths.get(location + File.separator + address).toAbsolutePath().normalize();
		try {
			Files.createDirectories(newDirectories);
		} catch (Exception ex) {
			throw new FileStorageException("Could not create the directory where the uploaded files will be stored.");
		}
	}

	public String uploadUserImage(MultipartFile file, Long userId) throws IllegalStateException, IOException {
		return upload(file, userId, FOLDER_USER);
	}

	/*@Caching(evict = { @CacheEvict(value = "materialImageShort", key = "{#materialId}", condition = "#result!=null"),
			@CacheEvict(value = "materialImageBig", key = "{#materialId}", condition = "#result!=null"),
			@CacheEvict(value = "materialImageMed", key = "{#materialId}", condition = "#result!=null") })*/
	public String uploadMaterialImage(MultipartFile file, Long materialId) throws IllegalStateException, IOException {
		return upload(file, materialId, FOLDER_MATERIAL);
	}

	/*@Caching(evict = { @CacheEvict(value = "productoImageShort", key = "{#productoId}", condition = "#result!=null"),
			@CacheEvict(value = "productoImageBig", key = "{#productoId}", condition = "#result!=null"),
			@CacheEvict(value = "productoImageMed", key = "{#productoId}", condition = "#result!=null") })*/
	public String uploadProductoImage(MultipartFile file, Long productoId) throws IllegalStateException, IOException {
		return upload(file, productoId, FOLDER_PRODUCTO);
	}

	private String upload(MultipartFile file, Long id, String folder) throws IllegalStateException, IOException {

		// Normalize file name
		String fileName = StringUtils.cleanPath(file.getOriginalFilename());
		// FileOutputStream fo;
		BufferedImage image = ImageIO.read(file.getInputStream());
		BufferedImage imageShort = resize(image, 50, 50);
		BufferedImage imageMed = resize(image, 128, 128);
		BufferedImage imageBig = resize(image, 500, 500);

		try {
			if (fileName.contains("..")) {
				throw new FileStorageException("Sorry! Filename contains invalid path sequence :" + fileName);
			}
			this.createDirectories(id, folder);
			File outputShort = new File(
					location + File.separator + folder + File.separator + id + File.separator + "50x50.png");
			File outputMed = new File(
					location + File.separator + folder + File.separator + id + File.separator + "128x128.png");
			File outputBig = new File(
					location + File.separator + folder + File.separator + id + File.separator + "500x500.png");

			ImageIO.write(imageShort, "png", outputShort);
			ImageIO.write(imageMed, "png", outputMed);
			ImageIO.write(imageBig, "png", outputBig);

			log.info("Uploaded image");
			return fileName;
		} catch (IOException ex) {
			log.error("Could not store file " + fileName + ". Please try again!", ex);
			throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
		}
	}

	// USUARIO DOWNLOAD IMAGE
	//@Cacheable(value = "usuarioImageShort", key = "{#userId}", unless = "#result == null")
	public ResponseEntity<byte[]> downloadUserImageShort(Long userId, HttpServletRequest request,
			HttpServletResponse response) throws ImageNotFoundException, IOException {
		String fileName = "50x50.png";
		return this.downloadImage(userId, fileName, FOLDER_USER, request, response);
	}

	//@Cacheable(value = "usuarioImageBig", key = "{#userId}", unless = "#result == null")
	public ResponseEntity<byte[]> downloadUserImageBig(Long userId, HttpServletRequest request,
			HttpServletResponse response) throws ImageNotFoundException, IOException {
		String fileName = "500x500.png";
		return this.downloadImage(userId, fileName, FOLDER_USER, request, response);
	}

	//@Cacheable(value = "usuarioImageMed", key = "{#userId}", unless = "#result == null")
	public ResponseEntity<byte[]> downloadUserImageMed(Long userId, HttpServletRequest request,
			HttpServletResponse response) throws ImageNotFoundException, IOException {
		String fileName = "128x128.png";
		return this.downloadImage(userId, fileName, FOLDER_USER, request, response);
	}

	// MATERIAL DOWNLOAD IMAGE
	//@Cacheable(value = "materialImageShort", key = "{#materialId}", unless = "#result == null")
	public ResponseEntity<byte[]> downloadMaterialImageShort(Long materialId, HttpServletRequest request,
			HttpServletResponse response) throws ImageNotFoundException, IOException {
		String fileName = "50x50.png";
		return this.downloadImage(materialId, fileName, FOLDER_MATERIAL, request, response);
	}

	//@Cacheable(value = "materialImageBig", key = "{#materialId}", unless = "#result == null")
	public ResponseEntity<byte[]> downloadMaterialImageBig(Long materialId, HttpServletRequest request,
			HttpServletResponse response) throws ImageNotFoundException, IOException {
		String fileName = "500x500.png";
		return this.downloadImage(materialId, fileName, FOLDER_MATERIAL, request, response);
	}

	//Cacheable(value = "materialImageMed", key = "{#materialId}", unless = "#result == null")
	public ResponseEntity<byte[]> downloadMaterialImageMed(Long materialId, HttpServletRequest request,
			HttpServletResponse response) throws ImageNotFoundException, IOException {
		String fileName = "128x128.png";
		return this.downloadImage(materialId, fileName, FOLDER_MATERIAL, request, response);
	}

	// PRODUCTO DOWNLOAD IMAGE
	//@Cacheable(value = "productoImageShort", key = "{#productoId}", unless = "#result == null")
	public ResponseEntity<byte[]> downloadProductoImageShort(Long productoId, HttpServletRequest request,
			HttpServletResponse response) throws ImageNotFoundException, IOException {
		String fileName = "50x50.png";
		return this.downloadImage(productoId, fileName, FOLDER_PRODUCTO, request, response);
	}

	//@Cacheable(value = "productoImageBig", key = "{#productoId}", unless = "#result == null")
	public ResponseEntity<byte[]> downloadProductoImageBig(Long productoId, HttpServletRequest request,
			HttpServletResponse response) throws ImageNotFoundException, IOException {
		String fileName = "500x500.png";
		return this.downloadImage(productoId, fileName, FOLDER_PRODUCTO, request, response);
	}

	//@Cacheable(value = "productoImageMed", key = "{#productoId}", unless = "#result == null")
	public ResponseEntity<byte[]> downloadProductoImageMed(Long productoId, HttpServletRequest request,
			HttpServletResponse response) throws ImageNotFoundException, IOException {
		String fileName = "128x128.png";
		return this.downloadImage(productoId, fileName, FOLDER_PRODUCTO, request, response);
	}

	private ResponseEntity<byte[]> downloadImage(Long id, String fileName, String folder, HttpServletRequest request,
			HttpServletResponse response) throws IOException, ImageNotFoundException {

		ServletContext context = request.getSession().getServletContext();
		// String appPath = context.getRealPath("");
		String filePath = location + File.separator + folder + File.separator + id + File.separator + fileName;
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
		String headerValue = String.format("attachment; filename=\"%s\"", id);
		headers.set(headerKey, headerValue);
		// headers.setContentDisposition(contentDisposition);
		ResponseEntity<byte[]> responseEntity = new ResponseEntity<>(fileContent, headers, HttpStatus.OK);
		log.info("Downloaded in service");
		return responseEntity;

	}

	/*@Caching(evict = { @CacheEvict(value = "usuarioImageShort", key = "{#userId}", condition = "#result!=null"),
			@CacheEvict(value = "usuarioImageBig", key = "{#userId}", condition = "#result!=null"),
			@CacheEvict(value = "usuarioImageMed", key = "{#userId}", condition = "#result!=null") })*/
	public Long deleteUserImage(Long userId) {
		return deleteImage(userId, FOLDER_USER);
	}
	
	/*@Caching(evict = { @CacheEvict(value = "materialImageShort", key = "{#materialId}", condition = "#result!=null"),
			@CacheEvict(value = "materialImageBig", key = "{#materialId}", condition = "#result!=null"),
			@CacheEvict(value = "materialImageMed", key = "{#materialId}", condition = "#result!=null") })*/
	public Long deleteMaterialImage(Long materialId) {
		return deleteImage(materialId, FOLDER_MATERIAL);
	}
	
	/*@Caching(evict = { @CacheEvict(value = "productoImageShort", key = "{#productoId}", condition = "#result!=null"),
			@CacheEvict(value = "productoImageBig", key = "{#productoId}", condition = "#result!=null"),
			@CacheEvict(value = "productoImageMed", key = "{#productoId}", condition = "#result!=null") })*/
	public Long deleteProductoImage(Long productoId) {
		return deleteImage(productoId, FOLDER_PRODUCTO);
	}

	private Long deleteImage(Long id, String folder) {

		Long result = 0l;
		Path directory = Paths.get(location + File.separator + folder + File.separator + id).toAbsolutePath()
				.normalize();

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
