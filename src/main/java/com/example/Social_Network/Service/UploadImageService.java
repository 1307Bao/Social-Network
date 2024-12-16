package com.example.Social_Network.Service;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UploadImageService {


    static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    static final String SERVICE_ACCOUNT_KEY_PATH = getPathToGoogleCredentials();

    private static String getPathToGoogleCredentials() {
        return Paths.get(System.getProperty("user.dir"), "cred.json").toString();

    }

    public String getUrlAfterUploaded(MultipartFile multipartFile) throws GeneralSecurityException, IOException {
        // Chuyển đổi MultipartFile thành java.io.File
        java.io.File file = convertMultipartFileToFile(multipartFile);
        log.error("2 POST POSTED");
        // Thư mục Google Drive để upload
        String folderId = "1frtnH2gBL0C7p4xCi52wWQrwvN8o_bmP";
        Drive drive = createDriveService();
        log.error("3 POST POSTED");

        File fileMetaData = new File();
        fileMetaData.setName(file.getName());
        fileMetaData.setParents(Collections.singletonList(folderId));
        log.error("7 POST POSTED");

        FileContent fileContent = new FileContent(multipartFile.getContentType(), file);
        log.error("8 POST POSTED");
        File uploadFile = null;
        try {
            uploadFile = drive.files().create(fileMetaData, fileContent).setFields("id").execute();
        } catch (Exception e){
            log.error("3 POST POSTED");
            e.printStackTrace();
        }

        String imgUrl = "https://drive.google.com/thumbnail?id=" + uploadFile.getId() + "&sz=w800" ;
        log.error("4 POST POSTED");

        // Xóa file tạm sau khi upload
        if (file != null && file.exists()) {
            log.error("5 POST POSTED");

            file.delete();
        }

        log.error("6 POST POSTED");


        return imgUrl;
    }

    private java.io.File convertMultipartFileToFile(MultipartFile multipartFile) throws IOException {
        java.io.File convFile = java.io.File.createTempFile("temp", multipartFile.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(convFile)) {
            fos.write(multipartFile.getBytes());
        }
        return convFile;
    }

    private Drive createDriveService() throws IOException, GeneralSecurityException {
        GoogleCredentials credentials = GoogleCredentials.fromStream(new java.io.FileInputStream(SERVICE_ACCOUNT_KEY_PATH))
                .createScoped(Collections.singleton(DriveScopes.DRIVE));

        log.error("{}", SERVICE_ACCOUNT_KEY_PATH);

        return new Drive.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JSON_FACTORY,
                new HttpCredentialsAdapter(credentials)
        ).setApplicationName("Social Network").build();
    }

    public void deleteFile(String fileId) throws GeneralSecurityException, IOException {
        Drive drive = createDriveService();
        fileId = extractFileIdFromImageUrl(fileId);
        drive.files().delete(fileId).execute();
    }

    private String extractFileIdFromImageUrl(String imageUrl) {
        String prefix = "https://drive.google.com/uc?export=view&id=";
        if (imageUrl != null && imageUrl.startsWith(prefix)) {
            return imageUrl.substring(prefix.length());
        }
        return null;
    }
}
