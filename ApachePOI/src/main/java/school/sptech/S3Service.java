package school.sptech;

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class S3Service implements AutoCloseable {

    private final S3Client s3;
    private final String bucketName;

    public S3Service() {
        this(Config.S3_BUCKET_NAME, Region.of(Config.S3_REGION));
    }

    public S3Service(String bucketName, Region region) {
        this.bucketName = bucketName;
        this.s3 = S3Client.builder()
                .region(region)
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }

    public List<String> listarArquivos(String prefix) {
        List<String> keys = new ArrayList<>();
        String continuationToken = null;
        boolean hasMore = true;

        while (hasMore) {
            ListObjectsV2Request.Builder builder = ListObjectsV2Request.builder()
                    .bucket(bucketName);

            if (prefix != null && !prefix.isEmpty()) {
                builder.prefix(prefix);
            }
            if (continuationToken != null) {
                builder.continuationToken(continuationToken);
            }

            ListObjectsV2Response response = s3.listObjectsV2(builder.build());

            for (S3Object obj : response.contents()) {
                if (!obj.key().endsWith("/") && obj.size() > 0) {
                    keys.add(obj.key());
                }
            }

            hasMore = response.isTruncated() != null && response.isTruncated();
            continuationToken = response.nextContinuationToken();
        }

        return keys;
    }

    public Path download(String key, Path destino) throws Exception {
        Files.createDirectories(destino.getParent());
        Files.deleteIfExists(destino);

        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        s3.getObject(request, destino);
        return destino;
    }

    public String getBucketName() {
        return bucketName;
    }

    @Override
    public void close() {
        if (s3 != null) {
            s3.close();
        }
    }
}
